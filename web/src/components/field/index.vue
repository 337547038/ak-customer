<template>
  <el-form-item v-bind="formItem" :prop="prop" :label="label">
    <template #label v-if="tooltip">
      {{ label }}
      <el-tooltip
          effect="dark"
          :content="tooltip"
          placement="top-start"
      >
        <el-icon size="18">
          <InfoFilled/>
        </el-icon>
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
        @change="change"
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
    <el-text v-else-if="render==='text'" v-bind="$attrs">{{ model }}</el-text>
    <component
        v-bind="$attrs"
        :placeholder="$attrs.placeholder || '请输入' + label"
        :is="currentComponent"
        v-model="model"
        @change="change"
        v-else
    />
  </el-form-item>
</template>
<script setup lang="ts">
  import {computed, onMounted, ref} from 'vue'
  import {useLayoutStore} from '@/store/layout'
  import {InfoFilled} from "@element-plus/icons-vue";
  import {getRequest} from "@/api";

  interface AjaxObj {
    api: string
    data?: { [key: string]: any }
    before?: <T>(params: { [key: string]: any }) => T
    after?: <T>(res: any, success: boolean) => T
    label?: string
    value?: string
  }

  defineOptions({name: 'AkField'})
  const props = withDefaults(
      defineProps<{
        prop?: string
        label?: string
        formItem?: { [key: string]: any } //绑定于el-form-item的所有属性
        options?: Array<{ label: string; value: any }> | string | { [key: string]: any }
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
        component?: any // render=component时
        tooltip?: string
        ajax?: AjaxObj
      }>(),
      {
        formItem: () => {
          return {}
        },
        render: 'input',
        ajax: () => {
          return {api: ''}
        }
      }
  )

  const layoutStore = useLayoutStore()
  const model = defineModel()
  const emits = defineEmits<{
    (e: 'change', value: any): void
  }>()

  const ajaxOptions = ref([])

  //　将object转array
  const formatObj = (obj: { [key: string]: any }) => {
    if (obj && Object.keys(obj).length) {
      const temp = []
      for (const key in obj) {
        const num = Number(key)
        const numKey = isNaN(num) ? key : num
        temp.push({label: obj[key], value: numKey})
      }
      return temp
    }
    return []
  }

  const optionsArray = computed(() => {
    if (typeof props.options === 'string') {
      // 作为字典的标识处理
      const dict = layoutStore.getSystemDict(props.options)
      return formatObj(dict)
    } else if (Object.prototype.toString.call(props.options) === '[object Object]') {
      return formatObj(props.options)
    } else if (props.ajax?.api) {
      return ajaxOptions.value
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

  const getAjaxOptions = () => {
    const {api, data, before, after, label, value} = props.ajax
    if (['checkbox', 'radio', 'select'].includes(props.render) && !props.options && api) {
      let params = data
      if (typeof before === 'function') {
        params = before(data) ?? data
      }
      getRequest(api, params)
          .then(({data}) => {
            let list = data?.list || data
            if (label && value) {
              list = list.map(item => ({
                label: item[label],
                value: item[value]
              }));
            }
            if (typeof after === 'function') {
              ajaxOptions.value = after(list, true) || list
            } else {
              ajaxOptions.value = list
            }
          })
          .catch((err) => {
            after(err, false)
          })
    }
  }
  onMounted(() => {
    getAjaxOptions()
  })
</script>
