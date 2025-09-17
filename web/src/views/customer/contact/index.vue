<template>
  <div>
    <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'contactList',del:'contactDel'}"
      :control-btn="controlBtn"
      :before="beforeList"
      :show-search="!detailTabProps.disabled"
      :columns-icon-visible="!detailTabProps.disabled"
      :auto-load="!detailTabProps.isComponents"
      :key-columns="keyColumns"
    />
    <el-dialog
      v-model="visible"
      width="800"
      :title="title"
      class="form-dialog"
      :before-close="formCancelClick"
    >
      <ak-form
        ref="formRef"
        v-model="formModel"
        pk="id"
        :data="formData"
        label-width="100"
        class="flex-form flex-form-2"
        :api="{ detail: 'contactGet',add:'contactSave', edit:'contactEdit' }"
        :after="afterForm"
        :before="beforeForm"
        @cancel="formCancelClick"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import {ref, markRaw, nextTick, computed, inject} from 'vue'
  import customerSelect from '@/components/customerSelect/index.vue'
  import validate from "@/components/form/validate";
  import {useLayoutStore} from '@/store/layout'

  const props = withDefaults(
      defineProps<{
        company?: string
        keyColumns?: string
      }>(),
      {
        company:'',
        keyColumns:''
      }
  )
  const detailTabProps = inject('detailTabsProps', ref({}));
  const layoutStore = useLayoutStore()
  const controlBtn = [
    {
      key: 'add', click: () => {
        addEditEvent(true)
      },
      display: () => {
        return !detailTabProps.value.disabled
      }
    }
  ]
  const tableListEl = ref()
  const visible = ref(false)
  const title = ref('新增联系人')
  const formRef = ref()
  const formModel = ref({decisionMaker: 1, sex: 1})
  const isEdit = ref(false)

  const beforeList = (type: string, data: any) => {
    if (type === 'get') {
      if (detailTabProps.value.isComponents) {
        // 作为组件调用,仅查询当前客户下的
        data.tid = detailTabProps.value.customerId
        data.userId = detailTabProps.value.userId
        if (detailTabProps.value.tabsName === 'shareWithMe') {
          // 查看共享给我/公海/无效的客户时，这里添加个参数
          data.specialCustomer = detailTabProps.value.customerId
          data.extend.search = 'comInvalidShare'
        }
      }
    }
    return data
  }
  const addEditEvent = (add: boolean, row?: { [key: string]: any }) => {
    visible.value = true
    isEdit.value = !add
    if (add) {
      title.value = `新增${props.company || ''}联系人`
    } else {
      title.value = `编辑${row?.name}信息`
      nextTick(() => {
        formRef.value.getData({id: row.id})
      })
    }
  }
  const showCompany = computed(() => {
    return !detailTabProps.value.isComponents
  })
  const showUserId = computed(() => {
    if (detailTabProps.value.isComponents) {
      // 在列表页引用时，在查看下属会员客户时显示
      return detailTabProps.value.tabsName === 'child'
    } else {
      return layoutStore.userInfo?.hasChild
    }
  })
  const currentUserId = ref()
  const isDecisionMaker = {1: '是', 2: '否', 3: '未知'}

  const columns = ref([
    {
      prop: 'checked',
      type: 'selection',
    },
    {
      label: '所属人员',
      prop: 'userId',
      visible: false,
      search: {
        changeRefresh: true,
        style: {width: '230px'},
        visible: showUserId,
        render: 'select',
        ajax: {
          api: 'userChildList',
          data: {},
          label: 'userName',
          value: 'id'
        },
        clearable: true,
        onChange: (val: number) => {
          currentUserId.value = val
        }
      }
    },
    {
      label: '客户名称',
      prop: 'tid',
      search: {
        userId: currentUserId,
        visible: showCompany,
        render: 'component',
        component: markRaw(customerSelect)
      },
      formatter: (row: any) => {
        return row.company
      },
      visible: showCompany,
      width: 160,
      showOverflowTooltip: true
    },
    {
      label: '联系人',
      prop: 'name',
      width: 80
    },
    {
      label: '是否决策人',
      prop: 'decisionMaker',
      render: 'tag',
      replaceValue: isDecisionMaker,
      custom: {1: 'success', 2: 'primary', 3: 'warning'},
      search: false,
      width: 95,
    },
    {
      label: '手机',
      prop: 'phone',
    },
    {
      label: '性别',
      prop: 'sex',
      render: 'tag',
      replaceValue: 'sex',
      search: false,
      width: 60,
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
      search: false,
      width: 120,
      showOverflowTooltip: true
    },
    {
      prop: 'lastTime',
      label: '最后联系时间',
      render: 'datetime',
      search: false,
      width: 120,
      showOverflowTooltip: true
    },
    {
      prop: 'nextTime',
      label: '下次联系时间',
      render: 'datetime',
      search: false,
      width: 120,
      showOverflowTooltip: true
    },
    {
      visible: () => {
        return !detailTabProps.value.disabled
      },
      fixed: 'right',
      prop: 'operate',
      label: '操作',
      render: 'buttons',
      width: '120px',
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
  ])

  const formCancelClick = () => {
    visible.value = false
    formModel.value = {decisionMaker: 1, sex: 1}
  }
  const beforeForm = (model: any, type: string) => {
    if (type === 'add' && detailTabProps.value.isComponents) {
      model.tid = detailTabProps.value.customerId
    }
    return model
  }
  const afterForm = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      formCancelClick()
      getData()
    }/* else if (type === 'detail') {
    }*/
    return result
  }
  const formData = ref([
    {
      label: '客户名称',
      prop: 'tid',
      attr: {
        style: {width: '350px'},
        userId: currentUserId,
      },
      render: 'component',
      component: markRaw(customerSelect),
      visible: showCompany,
      formItem: {
        rules: [validate('required', '客户名称不能为空')]
      }
    },
    {
      label: '姓名',
      prop: 'name',
      formItem: {
        rules: [validate('required', '姓名不能为空')]
      }
    },
    {
      label: '手机',
      prop: 'phone',
      formItem: {
        rules: [validate('mobile'), validate('required', '请输入手机号')]
      }
    },
    {
      label: '职务',
      prop: 'position',
    },
    {
      label: '电话',
      prop: 'tel',
      formItem: {
        rules: [validate('tel')]
      }
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
      formItem: {
        rules: [validate('email')]
      }
    },
    {
      label: '是否决策人',
      prop: 'decisionMaker',
      render: 'radio',
      options: isDecisionMaker
    },
    {
      label: '性别',
      prop: 'sex',
      render: 'radio',
      options: 'sex'
    },
    {
      label: '生日',
      prop: 'birthday',
      render: 'datePicker',
      attr: {
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD'
      }
    },
    {
      label: '地址',
      prop: 'address',
    },
    {
      label: '最后联系时间',
      prop: 'lastTime',
      attr: {
        disabled: true
      },
      visible: isEdit
    },
    {
      label: '下次跟进时间',
      prop: 'nextTime',
      render: 'datePicker',
      attr: {
        type: 'datetime',
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD HH:mm:ss'
      }
    },
    {
      label: '创建时间',
      prop: 'creatDate',
      attr: {
        disabled: true
      },
      visible: isEdit
    },
    {
      label: '备注',
      prop: 'remark',
      attr: {
        style: {width: '100%'},
        type: 'textarea',
        rows: 3
      }
    }
  ])

  const getData = () => {
    tableListEl.value.getData()
  }
  defineExpose({getData})
</script>

<style scoped lang="scss">

</style>