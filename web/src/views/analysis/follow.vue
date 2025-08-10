<template>
  <ak-list
      :columnsIconVisible="false"
      :show-search="false"
      :columns="columns"
      :api="{list:'analysisFollow'}"/>
</template>

<script setup lang="ts">
  const columns = [
    {
      label: '姓名',
      prop: 'userName',
      search: false
    },
    {
      label: '跟进次数',
      prop: 'followUpCount',
      search: false
    },
    {
      label: '跟进客户数',
      prop: 'followedCustomerCount',
      search: false
    },
    {
      label: '总客户数',
      prop: 'totalCustomers',
      search: false
    },
    {
      label: '跟进率%',
      help: '有跟进记录的客户占总客户的比例',
      prop: 'abc',
      search: false,
      formatter: (row: any) => {
        if (!row.followedCustomerCount || !row.totalCustomers) {
          return 0
        } else {
          return parseFloat((row.followedCustomerCount / row.totalCustomers * 100).toFixed(2))
        }
      }
    },
    {
      label: '最后跟进时间',
      prop: 'lastFollowUpTime',
      render: 'datetime',
      search: false,
    }
  ]
</script>

<style scoped lang="scss">

</style>