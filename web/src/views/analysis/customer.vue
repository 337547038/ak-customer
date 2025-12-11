<template>
  <ak-list
    v-if="!isMobile()"
    :columns-icon-visible="false"
    :show-search="false"
    :columns="columns"
    :api="{list:'analysisCustomerNum'}"
  />
  <w-list
    v-else
    :api="{get:'analysisCustomerNum'}"
    finished-text=""
  >
    <template #item="{data}">
      <van-cell :title="data.userName">
        <template #label>
          <p>客户数量：{{ data.customerCount }}</p>
          <p>客户成交量：{{ data.contractCount }}</p>
          <p>成交率：{{ getNum(data) }}</p>
        </template>
      </van-cell>
    </template>
  </w-list>
</template>

<script setup lang="ts">
  import {isMobile} from "@/utils";

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
      help:'合同有效期内合作客户占总客户量比例',
      search: false,
      formatter: (row:any) => {
        if (!row.contractCount) {
          return 0
        } else {
          return parseFloat((row.contractCount / row.customerCount * 100).toFixed(2))
        }
      }
    }
  ]

  const getNum = (row: any) => {
    if (!row.contractCount) {
      return 0
    } else {
      return parseFloat((row.contractCount / row.customerCount * 100).toFixed(2))
    }
  }
</script>

<style scoped lang="scss">

</style>