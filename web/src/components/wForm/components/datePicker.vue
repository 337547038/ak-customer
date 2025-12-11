<template>
  <field-popup
    v-model="pickerLabel"
    v-model:show-picker="showPicker"
    :item="item"
    @clear-click="clearClick"
  >
    <van-date-picker
      v-if="dateTimeType==='date'"
      :title="`选择${item.label||'日期'}`"
      :model-value="currentDate"
      v-bind="item.datePicker"
      @confirm="onConfirm"
      @cancel="onCancel"
    />
    <van-picker-group
      v-else
      :title="item.label"
      :tabs="getTabsTitle"
      @confirm="onConfirmGroup"
      @cancel="onCancel"
    >
      <van-date-picker
        :model-value="currentDate"
        v-bind="item.datePicker"
      />
      <van-date-picker
        v-if="dateTimeType==='dateRange'"
        :model-value="endDate"
        v-bind="item.datePicker"
      />
      <van-time-picker
        v-else
        :model-value="currentTime"
        :formatter="formatter"
      />
    </van-picker-group>
  </field-popup>
</template>

<script setup lang="ts">
  import FieldPopup from './fieldPopup.vue'
  import {ref, computed} from "vue";
  import {dateFormatting} from "@/utils";

  const props = withDefaults(
      defineProps<{
        modelValue: string | undefined
        item: { [key: string]: any }
      }>(),
      {}
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()
  const formatter = (type:string, option:any) => {
    if (type === 'hour') {
      option.text += '时';
    }
    if (type === 'minute') {
      option.text += '分';
    }
    return option;
  };
  // date年月日/datetime年月日时分/dateRange双年月日
  const dateTimeType = computed(() => {
    return props.item.datePicker?.type||'date'
  })
  const showPicker = ref<boolean>(false)
  const clearClick = () => {
    emits('update:modelValue', '')
    emits('change', '')
  }
  const getTabsTitle = computed(() => {
    const startPlaceholder = props.item.datePicker?.startPlaceholder
    const endPlaceholder = props.item.datePicker?.endPlaceholder
    return [startPlaceholder || '选择日期', endPlaceholder || '选择时间']
  })
  const currentTime:any = computed(() => {
    const date = props.modelValue ? new Date(props.modelValue) : new Date();
    return [date.getHours(), date.getMinutes()]
  })
  const getDateRange = (index: number) => {
    if (dateTimeType.value === 'dateRange') {
      let startEnd:any = new Date().getTime()
      if (props.modelValue) {
        const date = props.modelValue.split(',')
        if (date.length === 2) {
          startEnd = date[index]
        }
      }
      return dateFormatting(startEnd, '{y}-{m}-{d}').split('-')
    }
    return []
  }
  const currentDate = computed(() => {
    if (dateTimeType.value === 'dateRange') {
      return getDateRange(0)
    } else {
      const date = props.modelValue ? props.modelValue : new Date().getTime() + '';
      const dateTime = dateFormatting(date, '{y}-{m}-{d}')
      return dateTime.split('-')
    }
  })
  const endDate = computed(() => {
    return getDateRange(1)
  })
  const pickerLabel = computed(() => {
    if (props.modelValue) {
      if (dateTimeType.value === 'dateRange') {
        const startEnd = props.modelValue.split(',') // 两个时间使用豆号隔开
        if (startEnd.length === 2) {
          const start = dateFormatting(startEnd[0], '{y}-{m}-{d}')
          const end = dateFormatting(startEnd[1], '{y}-{m}-{d}')
          return `${start}/${end}`
        }
        return props.modelValue
      } else {
        return dateFormatting(props.modelValue, dateTimeType.value === 'date' ? '{y}-{m}-{d}' : '{y}-{m}-{d} {h}:{i}')
      }
    }
    return props.modelValue
  })

  const onConfirmGroup = (obj:any) => {
    const date = obj[0]?.selectedValues.join('-')
    const time = obj[1]?.selectedValues.join(':')
    let val = date + ' ' + time + ':00'
    if (dateTimeType.value === 'dateRange') {
      const end = obj[1]?.selectedValues.join('-')
      val = date + ',' + end
    }
    emits('update:modelValue', val)
    emits('change', val)
    showPicker.value = false
  }
  const onConfirm = ({selectedValues}:any) => {
    const val = selectedValues.join('-') + ' 00:00:00'
    emits('update:modelValue', val)
    emits('change', val)
    showPicker.value = false
  }
  const onCancel = () => {

  }
</script>

<style scoped lang="scss">

</style>