<template>
  <w-form
    ref="formRef"
    :disabled="formDisabled"
    :data="formData"
    :submit-url="isAdd?'contractPaymentSave':'contractPaymentEdit'"
    request-url="contractPaymentGet"
    :before="beforeSubmit"
    :after="afterSubmit"
    :hide-filed="hideFiled"
    @cancel="cancelForm"
    @field-value-change="fieldValueChange"
  >
    <template #formTop>
      <van-notice-bar
        v-if="formDisabled"
        text="审核通过，不能修改数据"
      />
    </template>
    <template #files="{rows,value}">
      <Uploader
        :item="rows"
        :model-value="value"
        @update:model-value="updateUploadFiles"
      />
    </template>
  </w-form>
</template>
<script setup lang="ts">
  import Uploader from "@/components/wForm/components/upload.vue";
  import {useLayoutStore} from '@/store/layout'
  import {nextTick, ref} from 'vue'
  import validate from "@/components/form/validate";

  const props = withDefaults(
      defineProps<{
        isAdd?: boolean
        currentUserId?: number | null// 当前记录用户id
      }>(),
      {
        isAdd: true,
        currentUserId: null
      }
  )
  const emits = defineEmits<{
    (e: 'clickEvent', key: string): void
  }>()

  const layoutStore = useLayoutStore()
  const formRef = ref()
  const formDisabled = ref(false)
  const hideFiled = ref<any>([])
  const uploadFiles = ref()
  const formData = ref([
    {
      prop: "code",
      label: '回款编号',
      rules: [validate('required', '回款编号不能为空')]
    },
    {
      prop: "contractId",
      label: '合同',
      render: 'picker',
      rules: [validate('required', '请选择所属合同')],
      picker: {
        ajax: {
          api: 'contractList',
          data: {
            userId: props.currentUserId
          }
        },
        columnsFieldNames: {
          text: 'name',
          value: 'id'
        }
      }
    },
    {
      prop: "money",
      label: '回款金额',
      rules: [validate('required', '回款金额不能为空'), validate("money")]
    },
    {
      prop: "account",
      label: '回款账号',
      render: 'picker',
      options: 'accountType',
      rules: [validate('required', '请选择回款账号')]
    },
    {
      prop: "datetime",
      label: "回款时间",
      render: 'datePicker',
      rules: [validate('required', '请输入回款时间')]
    },
    {
      label: '审核',
      prop: 'status',
      render: 'picker',
      options: [{value: 1, label: '待审核'}, {value: 2, label: '通过'}, {value: 3, label: '拒绝'}],
      picker: {
        columnsFieldNames: {
          text: 'label',
          value: 'value'
        }
      }
    },
    {
      prop: "remark",
      label: '备注',
      field: {type: 'textarea'}
    },
    {
      prop: "files",
      label: '附件',
      render: 'scope'
    }
  ])

  const cancelForm = () => {
    // 关闭开启弹层
    emits('clickEvent', 'cancel')
  }
  const beforeSubmit = (obj: { [key: string]: any }) => {
    if (uploadFiles.value) {
      obj.files = uploadFiles.value
    }
    return obj
  }
  const afterSubmit = (type: string) => {
    if (type === 'submit') {
      emits('clickEvent', 'submit')
    }
  }
  const fieldValueChange = (key: string, value: any, model: { [key: string]: any }) => {
    if (key === 'customerId') {
      model.contactId = ''
    }
  }
  const updateUploadFiles = (value: string) => {
    uploadFiles.value = value
  }

  const getData = (obj: { [key: string]: any }, key?: string) => {
    nextTick(() => {
      formRef.value.getData({id: obj.id})
    })
    hideFiled.value = key === 'check' ? [] : ['status']
    // 审核通过时，当前记录为自己时不能修改,下属的可以修改
    formDisabled.value = obj.status === 2 && obj.userId === layoutStore.userInfo.id;
  }
  const setValue = (obj:{ [key: string]: any }) => {
    console.log('obj',obj)
    formRef.value.setValue(obj)
  }
  defineExpose({getData,setValue})

</script>
<style scoped lang="scss">

</style>