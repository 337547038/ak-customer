<template>
  <el-cascader :props="props" :options="options" v-model="model" placeholder="请选择区域"/>
</template>
<script setup lang="ts">
  import type {CascaderProps} from 'element-plus'
  import {ref} from 'vue'
  import ProvincesJson from '@/assets/json/provinces.json'
  import CityJson from '@/assets/json/cities.json'
  import AreaJson from '@/assets/json/areas.json'

  const model = defineModel()

  const options = ref<CascaderProps['options']>(ProvincesJson)

  const getCityArea = (level: number, code: string) => {
    if (level === 1) {
      return CityJson.filter(city => city.provinceCode === code)
    } else if (level === 2) {
      return AreaJson.filter(city => city.cityCode === code).map(city => ({...city, leaf: level >= 2}))
    }
  }

  const props: CascaderProps = {
    label: 'name',
    value: 'code',
    lazy: true,
    lazyLoad(node, resolve) {
      let {level, value} = node
      if (['11', '12', '31', '50'].includes(value)) {
        level = level + 1
        value = value + '01'
      }
      if(['81','82'].includes(value)){
        level = level + 1
      }
      resolve(getCityArea(level, value))
    },
  }
</script>
<style scoped lang="scss">

</style>