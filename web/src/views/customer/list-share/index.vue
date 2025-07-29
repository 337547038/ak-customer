<template>
<div>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'customerList'}"
      :controlBtn="controlBtn"
      :before="beforeList"
  >
  </ak-list>
</div>
</template>
<route>
{meta:{permissions:"none"}}
</route>
<script setup lang="ts">

import {markRaw} from "vue";
import ProvincesCity from "@/components/provincesCity/index.vue";
import getCityByCode from "@/utils/getCityByCode";

const columns=[
  {
    prop:"company",
    label:'客户名称',
  },
  {
    prop: 'userName',
    label: '所属人员',
    width: 90,
    search: false
  },
  {
    prop: 'area',
    label: '所属区域',
    search: {
      render: 'component',
      component: markRaw(ProvincesCity)
    },
    formatter: (row: any, column: any, cellValue: any) => {
      return getCityByCode(cellValue)
    },
    width: 160,
    showOverflowTooltip: true
  },
  {
    prop: 'industry',
    label: '行业分类',
    width: 120,
    render: 'tag',
    replaceValue: 'industryType',
    search: {
      render: 'select',
      options: 'industryType'
    }
  },
  {
    prop: 'operation',
    label: '操作',
    render: 'buttons',
    width: 70,
    search: false,
    fixed:'right',
    buttons: [
      {
        key: 'detail',
        label: '详情',
        icon: '',
        attr: {
          text: true
        }
      }
    ]
  }
]
const controlBtn=[]
const beforeList = () => {

}
</script>

<style scoped lang="scss">

</style>