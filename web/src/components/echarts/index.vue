<template>
  <div
    ref="chartRef"
    :style="{ width }"
    class="echarts-box"
  />
</template>

<script setup lang="ts">
  import * as echarts from 'echarts';
  import {ref, watch, onMounted, onBeforeUnmount, nextTick, computed} from 'vue';
  import {isMobile} from "@/utils";

  const props = withDefaults(
      defineProps<{
        option?: object
        width?: string
      }>(),
      {
        width: '100%',
        option: () => {
          return {}
        }
      }
  )
  const emits = defineEmits<{
    (e: 'click', value: any): void
  }>()
  const chartRef = ref()
  let chartInstance:any = null
  const initChart = () => {
    chartInstance = echarts.init(chartRef.value);
    setOption(props.option)
    window.addEventListener('resize', handleResize);
    chartInstance.on('click', (params: any) => {
      chartInstance.dispatchAction({
        type: 'restore' // 执行还原操作
      });
      emits('click', params)
    })
  }
  watch(() => props.option, (newOption) => {
    setOption(newOption)
  }, {deep: true})
  const handleResize = () => {
    if (chartInstance) {
      chartInstance.resize();
    }
  }
  onMounted(() => {
    nextTick(() => {
      initChart()
    })
  });
  onBeforeUnmount(() => {
    if (chartInstance) {
      chartInstance.dispose();
    }
  });

  const setOption = (newOption: any) => {
    if (chartInstance) {
      chartInstance.setOption(newOption);
    }
  }

  defineExpose({setOption})

  const height = computed(() => {
    return isMobile() ? '250px' : '150px'
  })
</script>

<style scoped lang="scss">
.echarts-box {
  height: calc(100vh - v-bind(height)) !important;
}
</style>