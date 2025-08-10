<template>
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
        :after="formAfter"
        :api="{ detail: 'contractPaymentGet',add:'contractPaymentSave',edit:'contractPaymentEdit' }"
        @cancel="formCancelClick"
        v-model="formModel">
    </ak-form>
  </el-dialog>
</template>

<script setup lang="ts">
  import {ref, nextTick, computed} from "vue";
  import validate from "@/components/form/validate";

  const props = withDefaults(
      defineProps<{
        userId?: number // 查看下属时
      }>(),
      {}
  )

  const emits = defineEmits<{
    (e: 'callback'): void
  }>()

  const visible = ref(false);
  const title = ref("");
  const formDisabled = computed(() => {
    return !props.userId && formModel.value.status === 1 // 已确认不能修改
  });
  const formRef = ref<HTMLFormElement>();
  const formModel = ref({})
  const contractDisabled = ref(false)
  const formCancelClick = () => {
    visible.value = false
    formModel.value = {}
    formRef.value.resetFields()
  }
  const formData = ref([
    {
      prop: "code",
      label: '回款编号',
      formItem: {
        rules: [validate('required', '回款编号不能为空', 'change')]
      }
    },
    {
      prop: "contractId",
      label: '合同',
      render: 'select',
      formItem: {
        rules: [validate('required', '请选择所属合同', 'change')]
      },
      ajax: {
        api: 'contractList',
        data: {},
        label: 'name',
        value: 'id'
      },
      attr: {
        disabled: contractDisabled,
        clearable: true
      }
    },
    {
      prop: "money",
      label: '回款金额',
      formItem: {
        rules: [validate('required', '回款金额不能为空', 'change'), validate("money")]
      }
    },
    {
      prop: "account",
      label: '回款账号',
      render: 'select',
      options: 'accountType',
      formItem: {
        rules: [validate('required', '请选择回款账号', 'change')]
      }
    },
    {
      prop: "datetime",
      label: "回款时间",
      render: 'datePicker',
      attr: {
        valueFormat: 'YYYY-MM-DD HH:mm:ss',
        format: 'YYYY-MM-DD'
      },
      formItem: {
        rules: [validate('required', '请输入回款时间', 'change')]
      }
    },
    {
      prop: "remark",
      label: '备注',
      attr: {
        style: {
          width: '100%'
        },
        type: 'textarea',
        rows: 5
      }
    }
  ])

  const open = (edit: boolean, row?: { [key: string]: any }) => {
    visible.value = true
    title.value = edit ? '修改合同回款' : '新增合同回款'
    if (edit && row) {
      nextTick(() => {
        formRef.value.getData({id: row.id})
      })
    }
    contractDisabled.value = false
    // 合同页收款时
    if (!edit && row) {
      formModel.value.contractId = row.id
      contractDisabled.value = true
    }
  }
  const formAfter = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      formCancelClick()
      emits("callback")
    }
  }
  defineExpose({open})
</script>

<style scoped lang="scss">

</style>