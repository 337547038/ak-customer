<template>
  <ak-list
    ref="tableListRef"
    :columns="columns"
    pk="id"
    :before="beforeList"
    :api="{list:'contractPaymentList',del:'contractPaymentDel'}"
    :control-btn="[
      {
        key:'add',
        click:()=>{addEditClick(false)}
      }
    ]"
  />
  <payment-form
    ref="paymentFormRef"
    :user-id="currentUserId"
    @callback="formCallback"
  />
</template>

<script setup lang="ts">
  import {computed, markRaw, ref, watch} from "vue";
  import customerSelect from "@/components/customerSelect/index.vue";
  import {useLayoutStore} from "@/store/layout";
  import PaymentForm from "../components/paymentForm.vue";
  import {onBeforeRouteLeave, useRoute} from "vue-router";

  const route = useRoute();
  const currentUserId = ref() // 有id表示当前为查看下属
  const layoutStore = useLayoutStore()
  const tableListRef = ref()
  const paymentFormRef = ref()
  const showUserId = computed(() => {
    return layoutStore.userInfo?.hasChild
  })
  const columns = ref([
    {
      label: '下属人员',
      prop: 'userId',
      visible: false,
      search: {
        changeRefresh: true,
        style: {width: '230px'},
        visible: showUserId.value,
        render: 'select',
        ajax: {
          api: 'userChildList',
          data: {},
          label: 'userName',
          value: 'id'
        },
        clearable: true,
        onChange: (val: any) => {
          currentUserId.value = val
          console.log(val)
        }
      }
    },
    {
      prop: "code",
      label: "回款编号",
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
        component: markRaw(customerSelect)
      }
    },
    {
      prop: "contractCode",
      label: "合同编号",
      search: false,
    },
    {
      prop: "contractName",
      label: "合同名称",
      search: false
    },
    {
      prop: "money",
      label: "回款金额",
      search: false,
      width: 100
    },
    {
      prop: "datetime",
      label: "回款时间",
      render: 'datetime',
      width: 120,
      showOverflowTooltip: true,
      search: false
    },
    {
      prop: "account",
      label: "回款账户",
      render: 'tag',
      replaceValue: 'accountType',
//      custom:{},
      search: false,
      width: 100
    },
    {
      prop: "status",
      label: "审核状态",
      render: 'tag',
      replaceValue: {1: '待审核', 2: '通过', 3: '拒绝'},
      custom: {1: 'primary', 2: 'success', 3: 'danger'},
      search: {
        render: 'select',
        options: {1: '待审核', 2: '通过', 3: '拒绝'}
      },
      width: 100
    },
    {
      prop: "creatDate",
      label: "创建时间",
      render: 'datetime',
      width: 120,
      showOverflowTooltip: true,
      search: false
    },
    {
      prop: 'operate',
      label: '操作',
      width: 200,
      render: 'buttons',
      buttons: [
        {
          label: '审核',
          attr: {
            type: 'danger',
            text: true
          },
          display: (row: any) => {
            return layoutStore.userInfo?.hasChild && row.userId !== layoutStore.userInfo?.id && row.status === 1
          },
          click: (row: any) => {
            addEditClick(true, row, true)
          }
        },
        {
          key: 'edit',
          label: '详情',
          attr: {
            text: true
          },
          click: (row: { [key: string]: any }) => {
            addEditClick(true, row)
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

  const addEditClick = (edit: boolean, row?: { [key: string]: any }, check?: boolean) => {
    paymentFormRef.value.open(edit, row, check)
  }
  const formCallback = () => {
    tableListRef.value.getData()
  }

  const beforeList = (type: string, params: any) => {
    if (type === 'get') {
      if (route.query.search === 'todo') {
        // 查看需审核的合同
        params.extend.search = 'child'
      }
    }
    return params

  }

  const unWatch = watch(() => route.query, () => {
    tableListRef.value.getData()
  })
  onBeforeRouteLeave(() => {
    unWatch()
  })
</script>

<style scoped lang="scss">

</style>