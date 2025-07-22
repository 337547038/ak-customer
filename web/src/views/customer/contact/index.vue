<template>
  <div>
    <ak-list
        ref="tableListEl"
        pk="id"
        :columns="columns"
        :api="{ list: 'contactList'}"
        :controlBtn="[{key:'add',click:()=>{addEditEvent(true)}}]"
    >
    </ak-list>
    <el-dialog v-model="visible" width="800" :title="title" class="form-dialog" :before-close="formCancelClick">
      <ak-form
          ref="formRef"
          :data="formData"
          label-width="100"
          class="flex-form flex-form-2"
          :api="{ detail: 'contactGet',add:'contactSave', edit:'contactGet' }"
          @cancel="formCancelClick">
      </ak-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import {ref, markRaw, nextTick} from 'vue'
  import customerSelect from '@/components/customerSelect/index.vue'

  const visible = ref(false)
  const title = ref('新增联系人')
  const formRef = ref()
  const formModel=ref({})
  const addEditEvent = (add: boolean, row?: { [key: string]: any }) => {
    visible.value = true
    if (add) {
      title.value = '新增联系人'
    } else {
      title.value = `编辑${row.name}信息`
      nextTick(() => {
        formRef.value.getData()
      })
    }
  }
  const columns = [
    {
      prop: 'checked',
      type: 'selection',
    },
    {
      label: '姓名',
      prop: 'name',
    },
    {
      label: '客户名称',
      prop: 'company',
      search: {
        render: 'component',
        component: markRaw(customerSelect)
      }
    },
    {
      label: '是否决策人',
      prop: 'decisionMaker',
      render: 'tag',
      options: 'decisionMaker',
      custom: {1: 'success', 2: 'primary', 3: 'warning'},
      search: false
    },
    {
      label: '手机',
      prop: 'phone',
    },
    {
      label: '性别',
      prop: 'sex',
      render: 'tag',
      options: 'sex',
      search: false
    },
    {
      label: '微信',
      prop: 'weixin',
    },
    {
      label: '电话',
      prop: 'tel',
      search: false
    },
    {
      label: 'QQ',
      prop: 'qq',
      search: false
    },
    {
      label: '电子邮件',
      prop: 'email',
      search: false
    },
    {
      label: '职位',
      prop: 'position',
      search: false
    },
    {
      prop: 'creatDate',
      label: '创建时间',
      render: 'datetime',
      search: false
    },
    {
      prop: 'nextTime',
      label: '下次联系时间',
      render: 'datetime',
      search: false
    },
    {
      prop: 'operate',
      label: '操作',
      render: 'buttons',
      search: false,
      buttons: [
        {
          key: 'edit',
          label: '编辑',
          attr: {
            text: true
          },
          click: (row: any) => {
            addEditEvent(false, row)
          }
        },
        {
          key: 'del',
          label: '删除',
          icon: '',
          attr: {
            text: true
          }
        }
      ]
    }
  ]

  const formCancelClick = () => {
    visible.value = false
  }
  const formData = [
    {
      label: '客户名称',
      prop: 'company',
      attr: {
        disabled: true
      }
    },
    {
      label: '姓名',
      prop: 'name',
    },
    {
      label: '手机',
      prop: 'phone',
    },
    {
      label: '电话',
      prop: 'tel',
    },
    {
      label: '微信号',
      prop: 'weixin',
    },
    {
      label: 'QQ',
      prop: 'qq',
    },
    {
      label: '电子邮件',
      prop: 'email',
    },
    {
      label: '职务',
      prop: 'position',
    },
    {
      label: '是否决策人',
      prop: 'decisionMaker',
    },
    {
      label: '性别',
      prop: 'sex',
    },
    {
      label: '生日',
      prop: 'birthday',
    },
    {
      label: '地址',
      prop: 'address',
    },
    {
      label: '下次跟进时间',
      prop: 'nextTime',
      render: 'datePicker',
    },
    {
      label: '创建时间',
      prop: 'creatDate',
      attr: {
        disabled: true
      }
    },
    {
      label: '备注',
      prop: 'remark',
      attr:{
        type: 'textarea',
        rows:3
      }
    }
  ]
</script>

<style scoped lang="scss">

</style>