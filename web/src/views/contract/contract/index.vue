<template>
  <ak-list
      ref="tableListRef"
      pk="id"
      :columns="columns"
      :api="{list:'contractList',del:'contractDel'}"
      :control-btn="[{key:'add',click:()=>{addClick()}}]"
      @formFieldChange="searchFormChange"
      :before="beforeList">
    <template #code="{row}">
      <el-tag type="danger" v-if="getShowTag(row)">已过期</el-tag>
      {{ row.name }}
    </template>
  </ak-list>
  <el-dialog
      v-model="visible"
      width="800" :title="title"
      class="form-dialog"
      :before-close="formCancelClick">
    <el-alert title="审核通过，不能修改数据" type="success" v-if="formDisabled"/>
    <ak-form
        :disabeld="formDisabled"
        pk="id"
        ref="formRef"
        :data="formData"
        label-width="110"
        class="flex-form flex-form-2"
        :api="{ detail: 'contractGet',add:'contractSave',edit:'contractEdit' }"
        @cancel="formCancelClick"
        :after="afterForm"
        :before="beforeForm"
        @change="formFiledChange"
        v-model="formModel">
      <template #files="{rows}">
        <uploadFiles
            :rules="rows.formItem.rules"
            :prop="rows.prop"
            :label="rows.label"
            v-model="formModel.files" v-if="visible"/>
      </template>
    </ak-form>
  </el-dialog>
  <payment-form ref="paymentFormRef" @callback="paymentFormCallback"/>
</template>

<script setup lang="ts">
  import {computed, markRaw, nextTick, ref} from "vue";
  import customerSelect from "@/components/customerSelect/index.vue";
  import {useLayoutStore} from "@/store/layout";
  import validate from "@/components/form/validate";
  import selectContact from "@/views/customer/follow/components/selectContact.vue";
  import uploadFiles from '@/views/customer/list/components/upload.vue'
  import AkList from "@/components/list/index.vue";
  import {dateFormatting} from "@/utils";
  import PaymentForm from "../components/paymentForm.vue";

  const props = withDefaults(
      defineProps<{
        isComponents?: boolean
      }>(),
      {}
  )

  const layoutStore = useLayoutStore()
  const formDisabled = ref(false)
  const tableListRef = ref()
  const showUserId = computed(() => {
    if (props.isComponents) {
      // 在列表页引用时，在查看下属会员客户时显示
      return props.tabsName === 'contract'
    } else {
      return layoutStore.userInfo?.hasChild
    }
  })
  const showCompany = computed(() => {
    return !props.isComponents
  })
  const paymentFormRef = ref()
  const currentUserId = ref()
  const columns = ref([
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
      prop: 'id',
      label: 'ID',
      search: false,
      width: 50
    },
    {
      prop: 'code',
      label: '合同编号'
    },
    {
      prop: 'name',
      label: '合同名称',
    },
    {
      prop: 'customerId',
      label: '客户名称',
      width: 180,
      showOverflowTooltip: true,
      formatter: (row) => {
        return row.customerName
      },
      search: {
        render: 'component',
        userId: currentUserId,
        visible: showCompany,
        component: markRaw(customerSelect)
      }
    },
    {
      prop: 'money',
      label: '合同金额',
      search: false,
      width:110
    },
    {
      prop: 'payment',
      label: '已收款项',
      search: false,
      width:110
    },
    {
      prop: 'contactId',
      label: '公司签约人',
      search: false,
      formatter: (row) => {
        return row.contactName
      },
      width:100
    },
    {
      prop: 'startDate',
      label: '合同有效期',
      //render: 'date',
      search: {
        render: 'datePicker',
        type: "daterange",
        startPlaceholder: '开始时间',
        endPlaceholder: '结束时间',
        valueFormat: 'YYYY-MM-DD',
      },
      formatter: (row) => {
        return dateFormatting(row.startDate, '{y}-{m}-{d}') + ' / ' + dateFormatting(row.endDate, '{y}-{m}-{d}')
      },
      width: 180
    },
    {
      prop: 'creatDate',
      label: '创建时间',
      render: 'date',
      search: false,
      width:100
    },
    {
      prop: 'status',
      label: '状态',
      render: 'tag',
      replaceValue: {1: '待审核', 2: '已确认', 3: '未通过'},
      custom: {2: 'success', 3: 'danger'},
      search: {
        render: 'select',
        options: {1: '待审核', 2: '已确认', 3: '未通过'}
      },
      width: '90px'
    },
    {
      prop: 'operate',
      label: '操作',
      render: 'buttons',
      width:180,
      fixed: 'right',
      buttons: [
        {
          label: '收款',
          attr: {
            type: 'success',
            text: true
          },
          click: (row) => {
            paymentFormRef.value.open(false, row)
          }
        },
        {
          key: 'edit',
          label: '详情',
          attr: {
            text: true
          },
          click: (row: { [key: string]: any }) => {
            addClick(row)
          }
        },
        {
          key: 'del',
          label: '删除',
          attr: {
            text: true
          }
        }
      ],
      search: false
    },
  ])
  const searchFormChange = (prop: string, val: any, model: any) => {
    if (prop === 'userId' && layoutStore.userInfo?.hasChild) {
      // 清空已填写的客户名称
      model.customerId = null
      currentUserId.value = val
    }
  }
  const beforeList = (type: string, params: any) => {
    if (type === 'get') {
      if (params.startEndDate) {
        params.startDate = params.startEndDate[0]
        params.endDate = params.startEndDate[1]
        delete params.startEndDate
      }
    }
    return params

  }

  const visible = ref(false)
  const addClick = (row?: { [key: string]: any }) => {
    formDisabled.value = false
    visible.value = true
    if (row) {
      title.value = '修改合同'
      nextTick(() => {
        // 没有选择所属人员时，即查看自己时，2已确认状态不能修改
        //　选择了人员时，即查看下属的，可以修改
        if (!currentUserId.value) {
          formDisabled.value = row.status === 2 // 不能修改
        }
        formRef.value.getData({id: row.id})
      })
    }
  }
  const formRef = ref()
  const formModel = ref({})
  const title = ref('新增合同')
  const customerId = ref()
  const formData = ref([
    {
      label: '客户名称',
      prop: 'customerId',
      render: 'component',
      component: markRaw(customerSelect),
      visible: showCompany,
      formItem: {
        rules: [validate('required', '客户名称不能为空', 'change')]
      }
    },
    {
      label: '合同名称',
      prop: 'name',
      formItem: {
        rules: [validate('required', '合同名称不能为空', 'change')]
      }
    },
    {
      label: '合同编码',
      prop: 'code',
      formItem: {
        rules: [validate('required', '合同编码不能为空', 'change')]
      }
    },
    {
      label: '公司签约人',
      prop: 'contactId',
      attr: {
        placeholder: '请输入联系人,可使用%',
        customerId: customerId,
      },
      render: 'component',
      component: markRaw(selectContact),
      formItem: {
        rules: [validate('required', '公司签约人不能为空', 'change')]
      }
    },
    {
      label: '合同金额',
      prop: 'money',
      formItem: {
        rules: [validate('required', '合同金额不能为空', 'change'), validate("money")]
      }
    },
    {
      label: '合同开始时间',
      prop: 'startDate',
      render: 'datePicker',
      attr: {
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD'
      },
      formItem: {
        rules: [validate('required', '合同开始时间不能为空', 'change')]
      }
    },
    {
      label: '合同结束时间',
      prop: 'endDate',
      render: 'datePicker',
      attr: {
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD'
      },
      formItem: {
        rules: [validate('required', '合同结束时间不能为空', 'change')]
      }
    },
    {
      label: '备注',
      prop: 'remark',
      attr: {
        style: {width: '100%'},
        type: 'textarea',
        rows: 4,
      }
    },
    {
      prop: 'files',
      label: '合同附件',
      render: 'scope',
      formItem: {
        rules: [{required: true, message: '请输入合同附件'}]
      }
    },
  ])
  const formCancelClick = () => {
    visible.value = false
    formModel.value = {}
    formRef.value.resetFields()
  }
  const beforeForm = (model: any, type: string) => {
    /*if (type === 'add' && props.isComponents) {
      model.customerId = props.customerId
    }*/
    return model
  }
  const afterForm = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      formCancelClick()
      tableListRef.value.getData()
    } else if (type === 'detail') {
    }
    return result
  }
  const formFiledChange = (prop: string, val: any, model: any) => {
    if (prop === 'customerId') {
      // 客户名称改变时，清空联系人
      model.contactId = null
      customerId.value = val
    }
  }

  const paymentFormCallback = () => {
    tableListRef.value.getData()
  }

  const getShowTag = (row: any) => {
    return row.startDate < new Date() && row.endDate > new Date()
  }
</script>

<style scoped lang="scss">

</style>