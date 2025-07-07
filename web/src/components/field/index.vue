<template>
  <el-form-item v-bind="formItem" :prop="prop" :label="label">
    <el-checkbox-group
      v-bind="$attrs"
      options=""
      v-model="model"
      @change="change"
      v-if="render === 'checkbox' && options?.length"
    >
      <el-checkbox
        v-for="(item, index) in options"
        :key="index"
        :value="item.value"
        >{{ item.label }}</el-checkbox
      >
    </el-checkbox-group>
    <el-radio-group
      v-bind="$attrs"
      v-model="model"
      options=""
      @change="change"
      v-else-if="render === 'radio' && options?.length"
    >
      <el-radio
        v-for="(item, index) in options"
        :key="index"
        :value="item.value"
        >{{ item.label }}</el-radio
      >
    </el-radio-group>
    <el-select
      v-bind="$attrs"
      options=""
      v-model="model"
      :placeholder="$attrs.placeholder || '请选择' + label"
      v-else-if="render === 'select' && options?.length"
      style="min-width: 120px"
    >
      <el-option
        v-for="(item, index) in options"
        :key="index"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
    <component
      v-bind="$attrs"
      :placeholder="$attrs.placeholder || '请输入' + label"
      :is="currentComponent"
      v-model="model"
      label=""
      v-else
    />
  </el-form-item>
</template>
<script setup lang="ts">
  import { computed } from 'vue'
  defineOptions({ name: 'field' })
  const props = withDefaults(
    defineProps<{
      prop?: string
      label?: string
      formItem?: { [key: string]: any } //绑定于el-form-item的所有属性
      options?: { label: string; value: any }[]
      render?:
        | 'input'
        | 'cascader'
        | 'radio'
        | 'checkbox'
        | 'datePicker'
        | 'inputNumber'
        | 'select'
        | 'switch'
        | 'timePicker'
        | 'timeSelect'
        | 'upload'
        | 'slider'
        | 'component'
      component?: any // render=component时
    }>(),
    {
      formItem: () => {
        return {}
      },
      render: 'input'
    }
  )
  const model = defineModel()
  const emits = defineEmits<{
    (e: 'change', value: any): void
  }>()

  const change = (val: any) => {
    emits('change', val)
  }

  const currentComponent = computed(() => {
    if (props.render === 'component') {
      return props.component
    }
    return `el-${props.render}`
  })
</script>
