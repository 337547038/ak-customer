<template>
  <ak-list
    ref="tableListEl"
    :show-search="false"
    :columns="columns"
    :api="{ list: 'logList'}"
  />
</template>
<script setup lang="ts">
import {ref} from 'vue'

const columns = ref([
  {
    prop: 'userName',
    label: '登录用户名',
  },
  {
    prop: 'loginIp',
    label: '登录ip'
  },
  {
    prop: 'dateTime',
    render:'datetime',
    label:'登录时间'
  },
  {
    prop: 'status',
    label: '状态',
    render: 'tag',
    replaceValue: {'1': '成功', '0': '失败'},
    custom: {'1': 'success', '0': 'danger'},
  },
  {
    prop: 'remark',
    label:'备注'
  },
])
</script>
<style scoped lang="scss">

</style>