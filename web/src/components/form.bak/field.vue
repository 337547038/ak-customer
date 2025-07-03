<template>
  <el-form-item v-bind="formItem" :rules="rules" customRules="" :prop="prop">
    <!--    <el-checkbox-group
      v-bind="control"
      v-model="fieldValue"
      v-if="type === 'checkbox'"
    >
      <el-checkbox
        v-bind="item"
        v-for="(item, index) in options"
        :key="index"
        :value="item.value || item.label"
        >{{ item.label }}</el-checkbox
      >
    </el-checkbox-group>-->
    <!--    <el-radio-group
      v-bind="control"
      v-model="fieldValue"
      v-if="type === 'radio'"
    >
      <el-radio
        v-bind="control"
        v-for="(item, index) in options"
        :key="index"
        :label="item.value"
        >{{ item.label }}</el-radio
      >
    </el-radio-group>-->
    <el-select v-bind="control" v-model="fieldValue" v-if="type === 'select'">
      <el-option
        v-for="(item, index) in options"
        :key="index"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
    <!--    <el-upload v-else-if="type === 'select'">根据具体项目做一个或多个不机样式</el-upload>-->
    <component
      :is="currentComponent"
      v-bind="control"
      v-model="fieldValue"
      v-else
    />
  </el-form-item>
</template>

<script lang="ts" setup>
  import { ref, computed, watch, onMounted, inject } from 'vue'
  import validate from './validate'
  import axios from '@/utils/request'
  const props = withDefaults(
    defineProps<{
      data: any
      model?: any // 表单所有的值
      type?:
        | 'input'
        | 'cascader'
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
      modelValue?: any
    }>(),
    {
      type: 'input',
      model: () => {
        return {}
      }
    }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()
  const formItem = computed(() => {
    return props.data.formItem || {}
  })
  const control = computed(() => {
    return props.data.control || {}
  })
  const config = computed(() => {
    return props.data.config || {}
  })
  const prop = computed(() => {
    return props.data.name
  })
  const options = ref(control.value.options)
  const watchValue = ref()
  const fieldValue = ref(props.modelValue)
  watch(
    () => props.modelValue,
    () => {
      fieldValue.value = props.modelValue
    }
  )
  watch(
    () => fieldValue.value,
    (val: any) => {
      emits('update:modelValue', val)
      emits('change', val)
    }
  )
  const currentComponent = computed(() => {
    if (props.type === 'component') {
      return props.data.component
    }
    return `el-${props.type}`
  })
  // 将自定义校验合并到rules
  const rules = computed(() => {
    if (formItem.value.customRules) {
      let obj: any = {}
      const itemRules = formItem.value.rules || []
      formItem.value.customRules.forEach((item: any) => {
        const rules: any = (validate as any)[item.type]
        switch (item.type) {
          case 'required':
            obj = {
              required: true,
              message: rules.message,
              trigger: item.trigger || 'blur'
            }
            break
          default:
            obj = {
              pattern: rules.regExp,
              message: rules.message,
              trigger: item.trigger || 'blur'
            }
            break
        }
        itemRules.push(obj)
      })
      return itemRules
    }
    return formItem.value.rules || []
  })
  // 根据url获取options选项数据
  const getOptionsKey = (url: string) => {
    const iReg = new RegExp('(?<=\\${)(.*?)(?=})', 'g')
    const key = url.match(iReg)
    return key && key[0]
  }
  const getUrlOptions = (load?: boolean) => {
    const { url, method = 'post', params = {}, afterResponse } = config.value
    if (!url) {
      return
    }
    const key = getOptionsKey(url)
    let replaceUrl = url
    let urlParams = params
    if (key) {
      watchValue.value = key
      // url包含了动态值时
      replaceUrl = url.replace('${' + key + '}', props.model[key])
      // 同时将参数提取到data
      const urlSplit = url.split('?')[1].split('&')
      let keyName = ''
      if (urlSplit) {
        for (const i in urlSplit) {
          if (urlSplit[i].includes('${' + key + '}')) {
            keyName = urlSplit[i].split('=')[0]
          }
        }
      }
      urlParams = Object.assign({}, params, { [keyName]: props.model[key] })
    }
    const storage = window.sessionStorage.getItem(prop.value)
    if (storage && !load) {
      options.value = JSON.parse(storage)
    } else {
      ;(axios as any)({
        method: method,
        url: replaceUrl,
        data: urlParams,
        params: method === 'get' ? params : {}
      })
        .then((res: any) => {
          let result = res.data.data
          if (typeof afterResponse === 'function') {
            result = afterResponse(result)
          }
          options.value = result
          window.sessionStorage.setItem(prop.value, JSON.stringify(result)) // 缓存
        })
        .catch((res: any) => {
          if (typeof afterResponse === 'function') {
            afterResponse(res, 'catch')
          }
          options.value = []
        })
    }
  }
  watch(
    () => props.model[watchValue.value],
    () => {
      getUrlOptions(true)
    }
  )
  const setOptionsValue = inject('setOptions', {}) as any
  watch(
    () => setOptionsValue.value,
    (val) => {
      if (config.value.optionsKey === false) {
        return
      }
      const key = config.value.optionsKey || prop.value // 从指定的dict里获取，没设置时使用默认的
      if (val[key]) {
        options.value = formatDict(val[key])
      }
    }
  )
  const formatDict = (obj: any) => {
    // dict支持两种格式select:{1:'选项1',2:'选项2'}或select:[{label:'选项1',value:'1'}]
    if (obj && Object.prototype.toString.call(obj) === '[object Object]') {
      // object时,转为array
      const temp = []
      for (const key in obj) {
        temp.push({
          label: obj[key],
          value: key
        })
      }
      return temp
    }
    return obj
  }
  onMounted(() => {
    getUrlOptions()
  })
</script>
