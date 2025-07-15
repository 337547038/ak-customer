<template>
  <el-form-item v-bind="formItem" :prop="prop" :label="label">
    <template #label v-if="tooltip">
      {{ label }}
      <el-tooltip
          effect="dark"
          :content="tooltip"
          placement="top-start"
      >
        <el-icon size="18"><InfoFilled /></el-icon>
      </el-tooltip>
    </template>
    <el-checkbox-group
        v-bind="$attrs"
        options=""
        v-model="model"
        @change="change"
        v-if="render === 'checkbox' && optionsArray?.length"
    >
      <el-checkbox
          v-for="(item, index) in optionsArray"
          :key="index"
          :value="item.value"
      >{{ item.label }}
      </el-checkbox
      >
    </el-checkbox-group>
    <el-radio-group
        v-bind="$attrs"
        v-model="model"
        options=""
        @change="change"
        v-else-if="render === 'radio' && optionsArray?.length"
    >
      <el-radio
          v-for="(item, index) in optionsArray"
          :key="index"
          :value="item.value"
      >{{ item.label }}
      </el-radio
      >
    </el-radio-group>
    <el-select
        v-bind="$attrs"
        options=""
        v-model="model"
        :placeholder="$attrs.placeholder || '请选择' + label"
        v-else-if="render === 'select' && optionsArray?.length"
        style="min-width: 120px"
    >
      <el-option
          v-for="(item, index) in optionsArray"
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
  import {computed} from 'vue'
  import {useLayoutStore} from '@/store/layout'
  import {InfoFilled} from "@element-plus/icons-vue";

  defineOptions({name: 'field'})
  const props = withDefaults(
      defineProps<{
        prop?: string
        label?: string
        formItem?: { [key: string]: any } //绑定于el-form-item的所有属性
        options?: Array<{ label: string; value: any }> | string
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
            | 'tree-select'
        component?: any, // render=component时
        tooltip?: string
      }>(),
      {
        formItem: () => {
          return {}
        },
        render: 'input'
      }
  )

  const layoutStore = useLayoutStore()
  const model = defineModel()
  const emits = defineEmits<{
    (e: 'change', value: any): void
  }>()

  const optionsArray = computed(() => {
    if (typeof props.options === 'string') {
      // 作为字典的标识处理
      const dict = layoutStore.getSystemDict(props.options)
      if (dict && Object.keys(dict).length) {
        const temp = []
        for (const key in dict) {
          const num = Number(key)
          const numKey = isNaN(num) ? key : num
          temp.push({label: dict[key], value: numKey})
        }
        return temp
      }
      return []
    } else {
      return props.options
    }
  })
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
