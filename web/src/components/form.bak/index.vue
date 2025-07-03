<template>
  <el-form v-bind="formProps" ref="formEl" :model="model">
    <template v-for="(item, index) in data" :key="index">
      <template v-if="getShow(item)">
        <div v-if="item.type === 'div'" v-bind="item.control" class="div-row">
          <template v-for="(list, listIndex) in item.list" :key="listIndex">
            <field
              :type="list.type"
              v-model="model[list.name]"
              :model="model"
              :data="list"
              @change="changeField(list.name, $event)"
            />
          </template>
        </div>
        <div
          v-else-if="item.type === 'title'"
          v-bind="item.control"
          class="form-title"
          v-html="item.title"
        >
        </div>
        <field
          v-else
          :type="item.type"
          v-model="model[item.name]"
          :model="model"
          :data="item"
          @change="changeField(item.name, $event)"
        />
      </template>
    </template>
    <slot></slot>
    <el-form-item v-if="btnText">
      <el-button type="primary" @click="onSubmit" v-if="btnText[0]"
        >{{ btnText[0] }}
      </el-button>
      <el-button v-if="btnText[1]" @click="onReset">{{ btnText[1] }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
  import { ref, onMounted, provide } from 'vue'
  import Field from './field.vue'
  import { getRequest } from '@/api'

  interface formData {
    name: string
    type: string
    formItem: any
    control: any
    config?: any
    list?: any
    component?: any
  }
  const props = withDefaults(
    defineProps<{
      data?: formData[] // 表单项数据
      formProps?: any // el表单组件props参数
      beforeSubmit?: Function // 表单提交前
      afterSubmit?: Function // 表单提交后
      btnText?: string[] | boolean // 按钮文案
      submitApi?: string // 表单提交接口api
      getApi?: string // 获取接口数据方法
      beforeRequest?: Function
      afterRequest?: Function
      hideField?: string[]
    }>(),
    {
      data: () => {
        return []
      },
      btnText: () => ['确定', '取消'],
      hideField: () => {
        return []
      }
    }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', name: string, value: any): void
    (e: 'submit', value: any): void
    (e: 'cancel'): void
  }>()
  const model = ref<any>({})
  const getModelValue = (data: formData[]) => {
    data.forEach((item: formData) => {
      if (item.name) {
        model.value[item.name] = item.control?.modelValue
      }
      if (item.type === 'div') {
        getModelValue(item.list)
      }
    })
  }
  getModelValue(props.data)
  const formEl = ref()
  const getShow = (data: formData) => {
    if (props.hideField.includes(data.name)) {
      return false // 不显示
    }
    const linkValue = data.config?.linkValue
    const linkResult = data.config?.linkResult
    if (linkValue) {
      const Fn = new Function('$', `return (${linkValue})`)
      const pass = Fn(model.value)
      if (linkResult === 'disabled') {
        // 设置为disabled后返回显示状态
        data.control.disabled = pass
        return true
      } else {
        return pass
      }
    }
    return true
  }
  const onSubmit = async () => {
    if (!formEl.value) return
    await formEl.value.validate((valid: any, fields: any) => {
      if (valid) {
        if (props.submitApi) {
          let beforeSubmitPrams = {}
          if (props.beforeSubmit && typeof props.beforeSubmit === 'function') {
            beforeSubmitPrams = props.beforeSubmit(model.value)
          }
          const prams = Object.assign({}, model.value, beforeSubmitPrams)
          getRequest(props.submitApi, prams)
            .then((res: any) => {
              if (typeof props.afterSubmit === 'function') {
                props.afterSubmit(res)
              }
              // 这里作全局提交结果处理
            })
            .catch((res: any) => {
              if (typeof props.afterSubmit === 'function') {
                props.afterSubmit(res, 'catch')
              }
              // 这里作全局异常提示处理
            })
        }
        emits('submit', model.value)
      } else {
        console.log('error submit!', fields)
      }
    })
  }
  const onReset = () => {
    formEl.value.resetFields()
    emits('cancel')
  }
  // 设置初始值
  const setValue = (obj: any) => {
    model.value = { ...model.value, ...obj }
  }
  const getValue = () => {
    return model.value
  }
  const optValue = ref()
  provide('setOptions', optValue)
  const setOptions = (obj: any) => {
    optValue.value = obj
  }
  const changeField = (name: string, val: any) => {
    emits('change', name, val)
  }
  // 修改表单时，加载初始数据
  const getData = () => {
    if (props.getApi) {
      let beforeRequestPrams = {}
      if (props.beforeRequest && typeof props.beforeRequest === 'function') {
        beforeRequestPrams = props.beforeRequest() || {}
      }
      const data = {} // 一些请求的参数
      const prams = Object.assign({}, data, beforeRequestPrams)
      getRequest(props.getApi, prams)
        .then((res: any) => {
          let result = res.data
          if (props.afterRequest && typeof props.afterRequest === 'function') {
            result = props.afterRequest(res.data)
          }
          if (result.dict) {
            optValue.value = result.dict
          }
          model.value = { ...model.value, ...result.data }
          // 这里作全局提交结果处理
        })
        .catch((res: any) => {
          // 这里作全局异常提示处理
          if (typeof props.afterRequest === 'function') {
            props.afterRequest(res, 'catch')
          }
        })
    }
  }
  onMounted(() => {
    getData()
  })
  defineExpose({ onSubmit, onReset, setValue, getValue, setOptions })
</script>
