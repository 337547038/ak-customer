<template>
  <el-cascader
    v-if="!isMobile()"
    v-model="model"
    :props="cascaderProps"
    :options="options"
    placeholder="请选择区域"
  />
  <field-popup
    v-else
    v-model="pickerLabel"
    v-model:show-picker="showPicker"
    :item="data"
    @clear-click="clearClick"
  >
    <van-cascader
      v-model="cascaderValue"
      title="请选择所在地区"
      :field-names="{ text: 'name', value: 'code', children: 'children' }"
      :options="options"
      @close="showPicker = false"
      @finish="onFinish"
      @change="onChange"
    />
  </field-popup>
</template>
<script setup lang="ts">
  import type {CascaderProps} from 'element-plus'
  import {ref, watch} from 'vue'
  import {onBeforeRouteLeave} from 'vue-router'
  import ProvincesJson from '@/assets/json/provinces.json'
  import CityJson from '@/assets/json/cities.json'
  import AreaJson from '@/assets/json/areas.json'
  import {isMobile} from "@/utils";
  import FieldPopup from "@/components/wForm/components/fieldPopup.vue";

  withDefaults(
      defineProps<{
        data?: { [key: string]: any }
      }>(),
      {
        data: () => {
          return {}
        }
      }
  )

  const model = defineModel<any>()

  const options = ref<any>(ProvincesJson)

  const getCityArea = (level: number, code: string) => {
    if (level === 1) {
      return CityJson.filter(city => city.provinceCode === code)
    } else if (level === 2) {
      return AreaJson.filter(city => city.cityCode === code).map(city => ({...city, leaf: level >= 2}))
    }
  }

  const cascaderProps: CascaderProps = {
    label: 'name',
    value: 'code',
    lazy: true,
    lazyLoad(node, resolve) {
      let {level, value} = node
      if (['11', '12', '31', '50'].includes(value+'')) {
        level = level + 1
        value = value + '01'
      }
      if (['81', '82'].includes(value+'')) {
        level = level + 1
      }
      resolve(getCityArea(level, value+''))
    },
  }

  // wap
  const pickerLabel = ref()
  const showPicker = ref(false)
  const clearClick = () => {
  }
  const cascaderValue = ref('')
  const onFinish = ({selectedOptions}:any) => {
    showPicker.value = false;
    pickerLabel.value = selectedOptions.map((option:any) => option.name).join('/');
    model.value = selectedOptions.map((option:any) => option.code).join(',');
  }
  const onChange = ({value, selectedOptions}:any) => {
    const currentOpt = selectedOptions.slice(-1)[0]
    let level = currentOpt.level
    if (['11', '12', '31', '50'].includes(value)) {
      // 北京天津上海重庆
      value = value + '01'
      level = 1
    }
    // 香港澳门
    if (['81', '82'].includes(value)) {
      level = 1
    }
    if (!level) {
      // 点击项为一级
      currentOpt.children = CityJson.filter(city => city.provinceCode === value)
          .map((city:any) => {
            city.level = 1
            return city
          })
    } else if (level === 1) {
      currentOpt.children = AreaJson.filter(city => city.cityCode === value)
          .map((city:any) => {
            city.level = 2
            return city
          })
    }
  }
  const unWatch = watch(() => model.value, () => {
    if (model.value && isMobile() && !pickerLabel.value) {
      cascaderValue.value = model.value.split(',').slice(-1)[0]
      getDefaultLabel()
    }
  }, {immediate: true})
  // 处理回显
  const getDefaultLabel = () => {
    const val = model.value.split(',')
    const temp = []
    const p = ProvincesJson.find(item => item.code === val[0])
    temp.push(p?.name)
    if (val?.length === 3) {
      const c = CityJson.find(item => item.code === val[1])
      temp.push(c?.name)
    }
    const a = AreaJson.find(item => item.code === val[val.length - 1])
    temp.push(a?.name)
    pickerLabel.value = temp.join('/')
  }
  onBeforeRouteLeave(() => {
    unWatch()
  })
</script>
<style scoped lang="scss">

</style>