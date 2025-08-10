<template>
  <ak-list
      :columnsIconVisible="false"
      :show-search="false"
      :columns="columns"
      :api="{list:'analysisCustomerNum'}"/>
</template>

<script setup lang="ts">
  const columns = [
    {
      label: '姓名',
      prop: 'userName',
      search: false
    },
    {
      label: '客户数量',
      prop: 'customerCount',
      search: false
    },
    {
      label: '客户成交量',
      prop: 'contractCount',
      search: false
    },
    {
      label: '成交率%',
      prop: 'abc',
      search: false,
      formatter: row => {
        if (!row.contractCount) {
          return 0
        } else {
          return parseFloat((row.contractCount / row.contractCount * 100).toFixed(2))
        }
      }
    }
  ]
</script>

<style scoped lang="scss">

</style>