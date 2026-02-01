<template>
  <!--客户级别-->
  <userSelect @change="userChange" />
  <myEcharts :option="option" />
</template>

<script setup lang="ts">
  import myEcharts from '@/components/echarts/index.vue'
  import userSelect from './childUser.vue'
  import {ref, onMounted} from 'vue'
  import {getRequest} from "@/api";
  import {useLayoutStore} from '@/store/layout'
  import {isMobile} from "@/utils";

  const layerStore = useLayoutStore()

  const props = withDefaults(
      defineProps<{
        pageType: string
      }>(),
      {}
  )
  const userChange = (userId: number) => {
    getData(userId)
  }

  const xAxis = ref<{[key: string]: any}>([])
  const series = ref<{[key: string]: any}>([])
  const option = ref({
    tooltip: {},
    xAxis: isMobile() ? {type: 'value'} : {
      type: 'category',
      data: xAxis
    },
    yAxis: isMobile() ? {
      type: 'category',
      data: xAxis
    } : {
      type: 'value'
    },
    series: [
      {
        label: {
          show: true,
          position: 'top'
        },
        barMaxWidth: 100,
        data: series,
        type: 'bar'
      }
    ]
  })

  const formatData = (data: any) => {
    let dictKey = props.pageType
    switch (props.pageType) {
      case 'industry':
        dictKey = 'industryType'
        break
      case 'type':
        dictKey = 'cooperationType'
        break
    }
    const dict = layerStore.getSystemDict(dictKey)
    const groups:any = {}
    data.forEach((item:any) => {
      const key = dict[item[props.pageType]] || '未分类'
      // 初始化分组计数
      if (!groups[key]) {
        groups[key] = 0;
      }
      // 计数
      groups[key]++;
    })
    xAxis.value = Object.keys(groups)
    series.value = xAxis.value.map((type:any) => groups[type]);

  }
  const getData = (userId?: number) => {
    getRequest("analysisCustomer", {userId})
        .then(res => {
          formatData(res.data?.list)
        })
  }
  onMounted(() => {
    getData()
  })
</script>