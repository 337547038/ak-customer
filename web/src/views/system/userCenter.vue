<template>
  <div class="user-center">
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
  <scan-code
    ref="scanCodeRef"
    mode="dialog"
  />
</template>
<route>
{meta:{permissions:"none"}}
</route>
<script setup lang="ts">
  import {ref, onMounted} from 'vue'
  import validate from "@/components/form/validate";
  import {useLayoutStore} from '@/store/layout'
  import {ElMessage} from "element-plus";
  import {getRequest} from "@/api";
  import scanCode from '@/components/scan/index.vue'
  import {useRoute} from 'vue-router'

  const route = useRoute()
  const layoutStore = useLayoutStore();
  const dict = layoutStore.getSystemDict()
  const formRef = ref();
  const formModel = ref({})
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
      prop: 'phone',
      label: '手机号码',
      formItem: {
        rules: [validate('mobile'), validate('required', '请输入手机号码')],
      }
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
  const bindOrUnbind = (param:any) => {
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
      scanCodeRef.value.open()
    }

  }
  const getScanBind = () => {
    const code = route.query.code
    const state = route.query.state
    if (code && state) {
      bindOrUnbind({code: code})
    }
  }
  onMounted(() => {
    formRef.value.getData()
    getScanBind()
  })
</script>

<style scoped lang="scss">
.user-center {
  background: #fff;
  padding: 50px
}
</style>