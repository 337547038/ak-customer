<template>
  <div>
    <ak-list
        ref="tableListEl"
        pk="id"
        :columns="columns"
        :api="{ list: 'followList',del:'followDel'}"
        :controlBtn="controlBtn"
        :before="beforeList"
        :auto-load="!isComponents"
        :show-search="!disabled"
        :columnsIconVisible="!disabled"
        :keyColumns="keyColumns"
        @formFieldChange="searchFormChange"
    >
      <template #expand="{row}">
        <div style="padding: 0 20px;margin-bottom: 8px;">跟进时间：{{ dateFormatting(row.dateTime) }}</div>
        <div style="padding: 0 20px">跟进内容：{{ row.remark }}</div>
      </template>
    </ak-list>
    <el-dialog v-model="visible" width="800" :title="title" class="form-dialog" :before-close="formCancelClick">
      <ak-form
          pk="id"
          ref="formRef"
          :data="formData"
          label-width="100"
          class="flex-form flex-form-2"
          :api="{ detail: 'followGet',add:'followSave' }"
          @cancel="formCancelClick"
          :after="afterForm"
          :before="beforeForm"
          @change="formFiledChange"
          v-model="formModel">
      </ak-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import {ref, markRaw, nextTick, computed} from 'vue'
  import customerSelect from '@/components/customerSelect/index.vue'
  import contactSelect from '@/components/contactSelect/index.vue'
  import validate from "@/components/form/validate";
  import {dateFormatting, getStorage} from "@/utils";
  import {useLayoutStore} from "@/store/layout";

  const props = withDefaults(
      defineProps<{
        isComponents?: boolean
        customerId?: number
        userId?: number
        company?: string
        disabled?: boolean
        keyColumns?: string
      }>(),
      {
        isComponents: false
      }
  )
  const layoutStore = useLayoutStore()
  const controlBtn = [
    {
      key: 'add',
      click: () => {
        addEditEvent(true)
      },
      display: () => {
        return !props.disabled
      }
    }
  ]
  const tableListEl = ref()
  const visible = ref(false)
  const title = ref('新增联系人')
  const formRef = ref()
  const formModel = ref({})
  const userInfo = computed(() => {
    return getStorage('userInfo', true) || {}
  })
  const formCustomerId = computed(() => {
    return props.customerId || formModel.value.customerId
  })
  const beforeList = (type: string, data: any) => {
    if (type === 'get') {
      if (props.isComponents) {
        // 作为组件调用
        data.customerId = props.customerId
        data.userId = props.userId
      }
    }
    return data
  }
  const addEditEvent = (add: boolean, row?: { [key: string]: any }) => {
    visible.value = true
    if (add) {
      title.value = `新增${props.company || ''}跟进`
    } else {
      title.value = `编辑${row?.name}信息`
      nextTick(() => {
        formRef.value.getData({id: row.id})
      })
    }
  }
  const showCompany = computed(() => {
    return !props.isComponents
  })

  const showUserId = computed(() => {
    if (props.isComponents) {
      // 在列表页引用时，在查看下属会员客户时显示
      return props.tabsName === 'child'
    } else {
      return layoutStore.userInfo?.hasChild
    }
  })
  const searchFormModel = ref({}) // 查询表单的值
  const searchUserId = computed(() => {
    return props.userId || searchFormModel.value?.userId
  })
  const searchCustomerId = computed(() => {
    if (props.isComponents) {
      return props.customerId
    } else {
      return searchFormModel.value.customerId
    }
  })

  const columns = ref([
    {
      type: 'expand',
      prop: 'expand',
      search: false,
      width: 30
    },
    {
      prop: 'checked',
      type: 'selection',
    },
    {
      label: '所属人员',
      prop: 'userId',
      show: false,
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
        clearable: true
      }
    },
    {
      label: '客户名称',
      prop: 'customerId',
      search: {
        userId: searchUserId,
        visible: showCompany,
        render: 'component',
        component: markRaw(customerSelect)
      },
      formatter: (row: any) => {
        return row.company
      },
      show: showCompany,
      showOverflowTooltip: true
    },
    {
      label: '联系人',
      prop: 'contactId',
      width: 90,
      search: {
        userId: searchUserId,
        customerId: searchCustomerId,
        placeholder: '请输入联系人,可使用%',
        render: 'component',
        component: markRaw(contactSelect)
      },
      formatter: (row: any) => {
        return row.contactName
      }
    },
    {
      label: '跟进方式',
      prop: 'type',
      render: 'tag',
      width: 110,
      replaceValue: 'followType',
      search: {
        render: 'select',
        options: 'followType'
      }
    },
    {
      prop: 'dateTime',
      label: '创建时间',
      render: 'datetime',
      search: false,
      width: 120,
      showOverflowTooltip: true
    },
    {
      prop: 'userName',
      label: '跟进人',
      search: false,
      width: 90,
    },
    {
      prop: 'remark',
      label: '跟进内容',
      search: false,
      formatter: (row: any) => {
        const remark = row.remark
        if (remark.length > 20) {
          return row.remark?.substring(0, 20) + '...';
        } else {
          return row.remark
        }
      }
    },
    {
      show: !props.disabled,
      width: 90,
      prop: 'operate',
      label: '操作',
      render: 'buttons',
      search: false,
      buttons: [
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
    formModel.value = {}
    formRef.value.resetFields()
  }

  const beforeForm = (model: any, type: string) => {
    if (type === 'add' && props.isComponents) {
      model.customerId = props.customerId
    }
    return model
  }
  const afterForm = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      formCancelClick()
      getData()
    } else if (type === 'detail') {
    }
    return result
  }

  const formData = ref([
    {
      label: '客户名称',
      prop: 'customerId',
      attr: {},
      render: 'component',
      component: markRaw(customerSelect),
      visible: showCompany,
      formItem: {
        rules: [validate('required', '客户名称不能为空', 'change')]
      }
    },
    {
      label: '联系人',
      prop: 'contactId',
      attr: {
        placeholder: '请输入联系人,可使用%',
        customerId: formCustomerId,
        userId: searchUserId,
        onBlur: (val: number, name: string) => {
          // 同时提交联系人姓名
          formModel.value.contactName = name
        }
      },
      render: 'component',
      component: markRaw(contactSelect),
      formItem: {
        rules: [validate('required', '联系人不能为空', 'change')]
      }
    },
    {
      label: '跟进方式',
      prop: 'type',
      render: 'select',
      options: 'followType',
      formItem: {
        rules: [validate('required', '请选择跟进方式')]
      }
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
      label: '跟进内容',
      prop: 'remark',
      attr: {
        style: {width: '100%'},
        type: 'textarea',
        rows: 3
      },
      formItem: {
        rules: [validate('required', '请输入跟进内容')]
      }
    }
  ])

  const formFiledChange = (prop: string, val: any, model: any) => {
    if (prop === 'customerId' && val) {
      // 客户名称改变时，清空联系人选择。将值传给联系人
      model.contactId = undefined
    }
  }
  // 条件查询表单改变事件
  const searchFormChange = (prop: string, val: any, model: any) => {
    if (prop === 'userId' && layoutStore.userInfo?.hasChild) {
      // 清空已填写的客户名称及联系人信息
      model.customerId = null
      model.contactId = undefined
    }
    if (prop === 'customerId') {
      // 客户名称改变时，联系限制为当前客户下的
      model.contactId = undefined
    }
    searchFormModel.value = model
  }

  const getData = () => {
    tableListEl.value.getData()
  }
  defineExpose({getData})
</script>

<style scoped lang="scss">

</style>