<template>
  <div
    v-if="!isMobile()"
    class="container"
  >
    <div class="form">
      <h3>AK客户管理系统</h3>
      <ak-form
        v-model="formModel"
        label-width="100"
        :data="formData"
        :api="{ add: 'userLogin' }"
        :after="loginAfter"
      >
        <el-form-item
          label="验证码"
          class="item-code"
        >
          <el-input
            v-model="formModel.code"
            placeholder="输入验证码"
          />
          <img
            :src="src"
            class="img"
            alt="获取验证码"
            @click="getCaptcha"
          >
        </el-form-item>
      </ak-form>
      <div
        class="weixin"
        style="padding:10px 0"
      >
        <el-button
          type="primary"
          text
          @click="wxLoginClick"
        >
          微信扫码登录
        </el-button>
      </div>
      <wx-login ref="wxLoginRef" />
    </div>
  </div>
  <div
    v-else
    class="login-page"
  >
    <div class="box">
      <van-form @submit="onSubmit">
        <div class="h1">
          AK客户管理系统
        </div>
        <van-cell-group
          inset
        >
          <van-field
            v-model="wForm.userName"
            name="username"
            label="用户名"
            placeholder="用户名"
            :rules="[{ required: true, message: '请填写用户名' }]"
          />
          <van-field
            v-model="wForm.password"
            type="password"
            name="password"
            label="密码"
            placeholder="密码"
            :rules="[{ required: true, message: '请填写密码' }]"
          />
          <van-field
            v-model="wForm.code"
            name="password"
            label="验证码"
            placeholder="验证码"
            :rules="[{ required: true, message: '请填写验证码' }]"
          >
            <template #button>
              <img
                :src="src"
                class="img"
                alt="获取验证码"
                @click="getCaptcha"
              >
            </template>
          </van-field>
        </van-cell-group>
        <div style="margin: 16px;">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
          >
            登录
          </van-button>
        </div>
        <div
          class="login-type"
          @click="loginTypeChange"
        >
          使用微信登录
        </div>
      </van-form>
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
  import {setStorage, removeStorage, isMobile} from '@/utils'
  import WxLogin from '@/components/scan/index.vue'
  import wapLogin from "@/utils/wWx"

  const router = useRouter()
  const route = useRoute()

  const useStore = useLayoutStore()

  const wxLoginRef = ref()
  const wxLoginClick = () => {
    wxLoginRef.value.open()
  }

  const loginAfter = (data:any, success?:boolean) => {
    if (success) {
      removeStorage('resources', true)
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
  const formModel = ref({userName: 'admin', password: '123456'})
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
          wForm.value.codeId = codeId
        })
        .catch(() => {
        })
  }
  const getScanLogin = () => {
    const code = route.query.code
    const state = route.query.state
    if (code && state) {
      // 扫码登录回调
      getRequest("scanLogin", {code: code})
          .then((res) => {
            loginAfter(res.data, true)
          })
          .catch(() => {
            router.push({path: "/login", query: {redirect: '/system/usercenter'}})
            getCaptcha()
          })
    }
  }

  // wap start
  interface WForm {
    userName: string
    password: string
    code: string
    codeId: string
  }
  const wForm = ref<WForm>({
    userName: 'admin',
    password: '123456',
    code: '',
    codeId: ''
  })
  const loginTypeChange = () => {
    wapLogin()
  }
  const onSubmit = () => {
    getRequest('userLogin', wForm.value)
        .then((res:any) => {
          loginAfter(res.data,true)
        })
        .catch(() => {
          getCaptcha()
        })
  }
  // wap end
  onMounted(() => {
    getCaptcha()
    getScanLogin()
  })
</script>
<style scoped lang="scss">
.container {
  width: 100%;
  height: 100vh;
  background: url("../../assets/bg.jpg") no-repeat center center fixed;
  display: flex;
  align-items: center;
  justify-content: center;


  .form {
    overflow: hidden;
    position: relative;
    background: #fff;
    border-radius: 5PX;
    padding: 30PX 100PX 30PX 100PX;
    width: 580PX;
    min-width: 400PX;
    box-sizing: border-box;

    h3 {
      text-align: center;
      font-size: 24PX;
      margin-bottom: 20PX;
    }
  }

  :deep(.item-code .el-form-item__content) {
    display: flex;
    justify-content: space-between;
    flex-wrap: nowrap
  }

  .img {
    height: 30PX;
    width: 100PX;
    margin-left: 10PX;
    cursor: pointer
  }

  .weixin {
    display: flex;
    justify-content: center
  }
}

.login-page {
  background: url("../../assets/bg.jpg") no-repeat center top / auto 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  .h1 {
    font-size: 36px;
    text-align: center;
    margin-bottom: 30px
  }

  .box {
    background: #fff;
    border-radius: 10px;
    margin: 0 20px;
    padding: 50px 0;
    flex: 2;
  }

  .login-type {
    font-size: 24px;
    text-align: center;
    color: #1989fa
  }
}
</style>