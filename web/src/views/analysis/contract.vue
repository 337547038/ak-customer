<template>
  <div>
    <ak-list
      v-if="!isMobile()"
      :columns-icon-visible="false"
      :api="{list:'analysisContract'}"
      :columns="columns"
      :show-search="false"
    />
    <w-list
      v-else
      :api="{get:'analysisContract'}"
      finished-text=""
    >
      <template #item="{data}">
        <van-cell :title="data.userName">
          <template #label>
            <div class="info">
              <p>有效合同数量：{{ data.count }}</p>
              <p>合同金额：{{ data.totalMoney }}</p>
              <p>回款金额：{{ data.totalPayment }}</p>
              <p>回款率%：{{ getNum(data) }}</p>
            </div>
          </template>
        </van-cell>
      </template>
    </w-list>
  </div>
</template>

<script setup lang="ts">
  import {isMobile} from "@/utils";

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

  const getNum = (row: any) => {
    if (!row.totalPayment) {
      return 0
    } else {
      return parseFloat((row.totalPayment / row.totalMoney * 100).toFixed(2))
    }
  }
</script>

<style scoped lang="scss">
.info {display: flex;justify-content: space-between;flex-wrap: wrap;

  p {width: 50%}
}
</style>