<template>
  <ak-list
    v-if="!isMobile()"
    :columns-icon-visible="false"
    :show-search="false"
    :columns="columns"
    :api="{list:'analysisFollow'}"
  />
  <w-list
    v-else
    :api="{get:'analysisFollow'}"
    finished-text=""
  >
    <template #item="{data}">
      <van-cell :title="data.userName">
        <template #label>
          <div class="info">
            <p>跟进次数：{{ data.followUpCount }}</p>
            <p>跟进客户数：{{ data.followedCustomerCount }}</p>
            <p>总客户数：{{ data.totalCustomers }}</p>
            <p>跟进率%：{{ getNum(data) }}</p>
            <p class="last">
              最后跟进时间：{{ dateFormatting(data.lastFollowUpTime) }}
            </p>
          </div>
        </template>
      </van-cell>
    </template>
  </w-list>
</template>

<script setup lang="ts">
  import {dateFormatting} from "@/utils";
  import {isMobile} from "@/utils";

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

  const getNum = (row: any) => {
    if (!row.followedCustomerCount || !row.totalCustomers) {
      return 0
    } else {
      return parseFloat((row.followedCustomerCount / row.totalCustomers * 100).toFixed(2))
    }
  }
</script>

<style scoped lang="scss">
.info {display: flex;justify-content: space-between;flex-wrap: wrap;

  p {width: 50%}

  .last {width: 100%}
}
</style>