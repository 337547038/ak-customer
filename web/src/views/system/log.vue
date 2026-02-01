<template>
  <ak-list
    v-if="!isMobile()"
    ref="tableListEl"
    :show-search="false"
    :columns="columns"
    :api="{ list: 'logList'}"
  />
  <w-list
    v-else
    ref="tableListEl"
    :api="{ get: 'logList'}"
    :add-icon-visible="false"
  >
    <template #item="{data}">
      <van-cell
        :title="data.userName"
        :value="dateFormatting(data.dateTime)"
        :label="`IP:${data.loginIp} ${data.remark||''} ${data.id}`"
      >
        <template #title>
          <span class="custom-title">{{ data.userName }}</span>
          <van-tag
            v-if="!data.status"
            type="danger"
          >
            失败
          </van-tag>
        </template>
      </van-cell>
    </template>
  </w-list>
</template>
<script setup lang="ts">
  import {ref} from 'vue'
  import {isMobile,dateFormatting} from "@/utils";

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
      render: 'datetime',
      label: '登录时间'
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
      label: '备注'
    },
  ])
</script>
<style scoped lang="scss">

</style>