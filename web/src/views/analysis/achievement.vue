<template>
  <div>
    <ak-list :columns="columns"></ak-list>
  </div>
</template>

<script setup lang="ts">
  import {ref} from 'vue'

  const columns = [
    {
      label: 'ID',
      type: 'index',
    },
    {
      label: '姓名',
      prop: 'userName',
    },
    {
      label: '合同数量',
      prop: 'num',
    },
    {
      label: '合同金额',
      prop: 'money',
    },
    {
      label: '回款金额',
      prop: 'money2',
    }
  ]
</script>

<style scoped lang="scss">

</style>