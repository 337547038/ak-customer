<template>
  <div class="container">
    <div class="form">
      <h3>xx客户管理系统</h3>
      <ak-form
          label-width="100"
          :data="formData"
          :api="{ add: 'userLogin' }"
          :after="loginAfter"
          v-model="formModel">
        <el-form-item label="验证码" class="item-code">
          <el-input v-model="formModel.code" placeholder="输入验证码"></el-input>
          <img
              :src="src"
              class="img"
              alt=""
              @click="getCaptcha"
          >
        </el-form-item>
      </ak-form>
      <div class="weixin">
        <el-button type="text">微信扫码登录</el-button>
      </div>
    </div>
  </div>
</template>
<route>
{ meta:{title:'登录',layout:'hidden',permissions:false} }
</route>
<script setup lang="ts">
  import {ref, onMounted, nextTick} from 'vue'
  import {getRequest} from "@/api"
  import {useRouter, useRoute} from 'vue-router'
  import {useLayoutStore} from '@/store/layout'
  import {setStorage,removeStorage} from '@/utils'

  const router = useRouter()
  const route = useRoute()

  const useStore = useLayoutStore()

  const loginAfter = (data, success) => {
    if (success) {
      removeStorage('resources',true)
      // 统一方法保存token
      useStore.setLoginInfo(data, true)
      // 获取权限菜单信息
      getRoleResources(data.roleId).then(() => {
        const path = route.query.redirect ? route.query.redirect : '/'
        nextTick(() => {
          router.push({path: path})
        })
      })
    } else {
      getCaptcha()
    }
  }
  // 加载资源角色
  const getRoleResources = (roleIds: string) => {
    return new Promise((resolve) => {
      if (!roleIds) {
        return resolve()
      }
      getRequest('roleGetByIds', {id: roleIds})
          .then((res) => {
            const resources = res.data
            setStorage('resources', resources, 0) // 用于权限判断
            resolve()
          })
    })
  }
  const formModel = ref({userName: 'admin', password: '12345'})
  const formData = ref([
    {
      prop: 'userName',
      label: '用户名',
      formItem: {
        rules: [
          {
            required: true,
            message: '请输入用户名',
            trigger: 'blur'
          }
        ]
      },
      attr: {}
    },
    {
      prop: 'password',
      label: '密码',
      formItem: {
        rules: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur'
          }
        ]
      },
      attr: {
        type: 'password'
      }
    },
  ])
  const src = ref()
  const getCaptcha = () => {
    getRequest('getCaptcha', {})
        .then((res: { data: { base64: any, codeId: any } }) => {
          const {base64, codeId} = res.data
          src.value = 'data:image/png;base64,' + base64
          //codeId.value = a
          formModel.value.codeId = codeId
        })
        .catch(() => {
        })
  }
  onMounted(() => {
    getCaptcha()
  })
</script>
<style scoped lang="scss">
.container {
  width: 100%;
  height: 100vh;
  background: url("../../assets/bg.jpg") no-repeat center center fixed;
  display: flex;
  align-items: center;
  justify-content: center
}

.form {
  background: #fff;
  border-radius: 5px;
  padding: 30px 100px 30px 100px;
  width: 580px;
  min-width: 400px;
  box-sizing: border-box;

  h3 {
    text-align: center;
    font-size: 24px;
    margin-bottom: 20px;
  }
}

:deep(.item-code .el-form-item__content) {
  display: flex;
  justify-content: space-between;
  flex-wrap: nowrap
}

.img {
  height: 30px;
  width: 100px;
  margin-left: 10px;
  cursor: pointer
}

.weixin {
  display: flex;
  justify-content: center
}
</style>