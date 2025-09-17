<template>
  <div>
    <ak-list
      :columns-icon-visible="false"
      :api="{list:'analysisContract'}"
      :columns="columns"
      :show-search="false"
    />
  </div>
</template>

<script setup lang="ts">
  //import {ref} from 'vue'

  const columns = [
    {
      label: '姓名',
      prop: 'userName',
    },
    {
      label: '有效合同数量',
      prop: 'count',
    },
    {
      label: '合同金额',
      prop: 'totalMoney',
    },
    {
      label: '回款金额',
      prop: 'totalPayment',
    },
    {
      label: '回款率',
      prop: 't',
      formatter: (row: any) => {
        if (!row.totalPayment) {
          return 0
        } else {
          return parseFloat((row.totalPayment / row.totalMoney * 100).toFixed(2))
        }
      }
    }
  ]
</script>

<style scoped lang="scss">

</style>