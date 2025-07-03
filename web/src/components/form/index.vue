<template>
  <el-form v-bind="formProps" ref="formEl" :model="model" :rules="rules">
    <template v-if="data?.length">
      <template v-for="(item, index) in data" :key="index">
        <el-text
          v-if="item.type === 'text'"
          class="form-title1"
          size="large"
          v-bind="item"
          ><span v-html="item.title"></span
        ></el-text>
        <field
          v-else
          v-bind="item.attr"
          :prop="item.prop"
          :label="item.label"
          :formItem="item.formItem"
          :options="item.attr?.options"
          :type="item.type"
          v-model="model[item.prop]"
          @change="changeField(item.prop, $event)"
        />
      </template>
    </template>
    <slot></slot>
    <el-form-item v-if="btnText" class="submit-btn-group">
      <el-button type="primary" @click="onSubmit" v-if="btnText[0]"
        >{{ btnText[0] }}
      </el-button>
      <el-button v-if="btnText[1]" @click="onReset">{{ btnText[1] }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
  import { ref, onMounted, provide } from 'vue'
  import Field from '../field/index.vue'
  import { getRequest } from '@/api'

  interface formData {
    prop: string
    type: string
    formItem: any
    attr: any
    component?: any
    modelValue?: any //初始值
  }
  const props = withDefaults(
    defineProps<{
      data?: formData[] // 表单项数据
      formProps?: any // el表单组件props参数
      btnText?: string[] | boolean // 按钮文案
      api: { add: string; edit: string }
      before?: (type: string, params: any) => boolean
      after?: (type: string, res: any, isSuccess?: boolean) => any
      params?: any // 附件参数
      rules?: any
      pk?: string | number // 主键，当提交的数据里包含了主键时，则提交修改接口
    }>(),
    {
      data: () => {
        return []
      },
      btnText: () => ['确定', '取消'],
      api: () => {
        return {
          add: '', // 列表数据api接口
          edit: '', //
          detail: ''
        }
      }
    }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', name: string, value: any): void
    (e: 'submit', value: any): void
    (e: 'cancel'): void
  }>()
  const model = defineModel()
  //获取初始值
  const getModelValue = (data: formData[]) => {
    data.forEach((item: formData) => {
      if (item.prop) {
        model.value[item.prop] = item.attr?.modelValue
      }
    })
  }
  getModelValue(props.data)

  const formEl = ref()
  const onSubmit = async () => {
    if (!formEl.value) return
    await formEl.value.validate((valid: any, fields: any) => {
      if (valid) {
        let type = 'submit'
        let api = props.api?.add
        if (props.pk && model.value[props.pk]) {
          api = props.api?.edit
          type = 'editSubmit'
        }
        if (api) {
          let params = model.value
          if (props.before && typeof props.before === 'function') {
            params = props.before(type, model.value)
          }
          if (params === false) {
            return false
          }
          getRequest(api, params)
            .then((res: any) => {
              if (typeof props.after === 'function') {
                props.after(type, res)
              }
              // 这里作全局提交结果处理
            })
            .catch((res: any) => {
              if (typeof props.after === 'function') {
                props.after(type, res)
              }
              // 这里作全局异常提示处理
            })
        }
      } else {
        console.log('error submit!', fields)
      }
    })
  }
  const onReset = () => {
    formEl.value.resetFields()
    model.value = {}
    emits('cancel')
  }
  // 设置初始值
  const setValue = (obj: any) => {
    model.value = { ...model.value, ...obj }
  }
  const getValue = () => {
    return model.value
  }
  const changeField = (prop: string, val: any) => {
    emits('change', prop, val)
  }
  // 修改表单时，加载初始数据
  const getData = (data?: any) => {
    const api = props.api?.detail
    if (api) {
      let params = Object.assign({}, props.params, data || {})
      if (props.before && typeof props.before === 'function') {
        params = props.before('detail', params) || {}
      }
      if (params === false) {
        return false
      }
      getRequest(api, params)
        .then((res: any) => {
          let result = res.data
          if (props.after && typeof props.aftert === 'function') {
            result = props.after('detail', es.data)
          }
          model.value = result.data
          // 这里作全局提交结果处理
        })
        .catch((res: any) => {
          // 这里作全局异常提示处理
          if (typeof props.after === 'function') {
            props.after('detail', res)
          }
        })
    }
  }
  onMounted(() => {
    //getData()
  })
  defineExpose({ onSubmit, onReset, setValue, getValue, getData })
</script>
