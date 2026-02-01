<template>
  <div
    v-if="!isMobile()"
    class="user-center"
  >
    <ak-form
      ref="formRef"
      v-model="formModel"
      class="flex-form flex-form-2"
      pk="id"
      label-width="120"
      :data="formData"
      label-suffix="："
      :after="after"
      :api="{edit: 'userEdit', detail:'userInfo' }"
    >
      <el-form-item label="绑定微信">
        <el-button
          type="primary"
          @click="bindWenXin"
        >
          {{ formModel.bindWX ? '解绑' : '点击绑定' }}
        </el-button>
      </el-form-item>
    </ak-form>
  </div>
  <div
    v-else
    style="background: #fff;height:100vh"
  >
    <w-form
      ref="formRef"
      :data="wapFormData"
      submit-url="userEdit"
      request-url="userInfo"
      :after="wapFormAfter"
    >
      <template #formBottom>
        <van-field label="绑定微信">
          <template #input>
            <van-button
              size="small"
              type="primary"
              @click="bindWenXin"
            >
              {{ formModel.bindWX ? '解绑' : '点击绑定' }}
            </van-button>
          </template>
        </van-field>
      </template>
    </w-form>
  </div>
  <scan-code
    ref="scanCodeRef"
    mode="dialog"
  />
</template>
<route>
{meta:{permissions:"none"}}
</route>
<script setup lang="ts">
  import {ref, onMounted, computed} from 'vue'
  import validate from "@/components/form/validate";
  import {useLayoutStore} from '@/store/layout'
  import {ElMessage} from "element-plus";
  import {getRequest} from "@/api";
  import scanCode from '@/components/scan/index.vue'
  import {useRoute} from 'vue-router'
  import {isMobile} from "@/utils";
  import wapLogin from "@/utils/wWx";

  const route = useRoute()
  const layoutStore = useLayoutStore();
  const dict = layoutStore.getSystemDict()
  const formRef = ref();
  const formModel = ref<any>({})
  const formData = ref([
    {
      prop: 'userName',
      label: '用户名称',
      render: 'text',
    },
    {
      prop: 'statusName',
      label: '状态',
      render: 'text',
    },
    {
      prop: 'sexName',
      label: '性别',
      render: 'text'
    },
    {
      prop: 'tidName',
      label: '直属上级',
      render: 'text'
    },
    {
      prop: 'departmentName',
      label: '所属部门',
      render: 'text',
    },
    {
      prop: 'creatTime',
      label: '创建时间',
      render: 'text',
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      render: 'text',
    },
    {
      prop: 'lastLogin',
      label: '最后登录时间',
      render: 'text',
    },
    {
      prop: 'loginTimer',
      label: '登录次数',
      render: 'text',
    },
    {
      prop: 'ip',
      label: '最后登录ip',
      render: 'text',
    },
    /*{
      prop: 'roleId',
      label: '角色权限',
      render: 'text',
    },*/
    {
      prop: 'phone',
      label: '手机号码',
      formItem: {
        rules: [validate('mobile'), validate('required', '请输入手机号码')],
      }
    },
    {
      prop: 'password',
      label: '登录密码',
      attr: {
        type: 'password',
        placeholder: '不修改请留空'
      },
      /*formItem: {
        rules: [{
          required: true,
          message: '请输入登录密码',
          trigger: 'blur'
        }]
      }*/
    },

    {
      prop: 'weixin',
      label: '微信号码'
    },
    {
      prop: 'qq',
      label: 'qq号码'
    }
  ])

  const after = (data: any, success: boolean, type: string) => {
    if (type === 'detail') {
      data.statusName = dict?.status[data.status];
      data.sexName = dict?.sex[data.sex];
    }
    return data;
  }

  const scanCodeRef = ref()
  const bindOrUnbind = (param: any) => {
    getRequest("bindWx", param)
        .then(() => {
          ElMessage.success('绑定成功')
          formRef.value.getData()
        })
  }
  const bindWenXin = () => {
    if (formModel.value.bindWX) {
      // 解绑
      bindOrUnbind({unbind: true})
    } else {
      if (isMobile()) {
        wapLogin('/system/userCenter')
      } else {
        scanCodeRef.value.open()
      }
    }

  }
  const getScanBind = () => {
    const code = route.query.code
    const state = route.query.state
    if (code && state) {
      bindOrUnbind({code: code})
    }
  }
  // wap
  const wapFormData = computed(() => {
    const temp: any = []
    formData.value.forEach((item: any) => {
      if (item.formItem?.rules) {
        item.rules = item.formItem?.rules
        delete item.formItem?.rules
      }
      if (item.prop === 'password') {
        item.field = {type: 'password'}
        item.placeholder = '不修改请留空'
      }
      temp.push(item)
    })
    return temp

  })
  const wapFormAfter = (type: string, res: any, isSuccess: boolean) => {
    after(res, isSuccess, 'detail')
    formModel.value = res
  }
  onMounted(() => {
    formRef.value.getData()
    if (isMobile()) {
      layoutStore.setNavBarTitle("个人中心")
      layoutStore.setRightSearchArrow(false)
    }
    getScanBind()
  })
</script>

<style scoped lang="scss">
  .user-center {
    background: #fff;
    padding: 50PX
  }
</style>