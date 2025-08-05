<template>
  <el-form ref="formEl" :model="model" :rules="rules" v-loading="loading">
    <template v-if="dataFilter?.length">
      <template v-for="(item, index) in dataFilter" :key="item.prop||index">
        <el-text
            v-if="item.render === 'title'"
            class="form-title1"
            size="large"
            v-bind="item"
        ><span v-html="item.title"></span
        ></el-text>
        <slot :name="item.prop" v-else-if="item.render==='scope'&&item.prop"/>
        <field
            v-else
            v-bind="item.attr"
            :prop="item.prop"
            :label="item.label"
            :tooltip="item.tooltip"
            :formItem="item.formItem"
            :options="item.options"
            :ajax="item.ajax"
            :render="item.render"
            :component="item.component"
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
      <el-button v-if="btnText[1]" @click="resetFields(true)">{{ btnText[1] }}</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
  import {ref, onMounted, computed, watch} from 'vue'
  import Field from '../field/index.vue'
  import {getRequest} from '@/api'
  import {ElMessage} from "element-plus";
  import {onBeforeRouteLeave} from "vue-router";

  defineOptions({name: 'AkForm'})

  interface formData {
    prop: string
    render: string
    formItem: any
    attr: any
    component?: any
    modelValue?: any //初始值
  }

  const props = withDefaults(
      defineProps<{
        data?: formData[] // 表单项数据
        //formProps?: any // el表单组件props参数
        btnText?: string[] | boolean // 按钮文案
        api?: { add: string, edit: string, detail: string }
        before?: (params: any, type: string) => boolean
        after?: (res: any, isSuccess?: boolean, type: string) => any
        params?: any // 附件参数
        rules?: any
        pk?: string | number // 主键，当提交的数据里包含了主键时，则提交修改接口
        hideFiled?: string[] // 不显示的字段
        modelValue?: any
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
        },
        hideFiled: () => {
          return []
        },
        modelValue: () => {
          return {}
        }
      }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', name: string, value: any, model: any): void
    (e: 'submit', value: any): void
    (e: 'cancel'): void
  }>()
  // 使用defineModel时，当引用时没有使用v-model会报错model.xxx
  // 使用defineModel({default:})时会导致输入框没法输入
  //const model = defineModel();
  const model = ref(props.modelValue)
  const unWatch = watch(() => props.modelValue, () => {
    model.value = props.modelValue
  }, {deep: true})
  const unWatch2 = watch(() => model.value, () => {
    emits("update:modelValue", model.value)
  }, {deep: true})

  const loading = ref(false)

  const dataFilter = computed(() => {
    return props.data.filter(item => item.visible !== false && !props.hideFiled.includes(item.prop))
  })
  //获取初始值
  const getModelValue = (data: formData[]) => {
    data.forEach((item: formData) => {
      if (item.prop && item.attr?.modelValue != undefined) {
        model.value[item.prop] = item.attr?.modelValue
      }
    })
  }

  const formEl = ref()
  const onSubmit = async () => {
    return new Promise((resolve, reject) => {
      if (!formEl.value) return
      formEl.value.validate((valid: any, fields: any) => {
        if (valid) {
          let type = 'add'
          let api = props.api?.add
          if (props.pk && model.value[props.pk]) {
            api = props.api?.edit
            type = 'update'
          }
          if (api) {
            let params = model.value
            if (props.before && typeof props.before === 'function') {
              params = props.before(JSON.parse(JSON.stringify(model.value)), type)
            }
            if (params === false) {
              return false
            }
            emits("submit", params)
            loading.value = true
            getRequest(api, params)
                .then((res: any) => {
                  let hasReturnAfter = null
                  if (typeof props.after === 'function') {
                    hasReturnAfter = props.after(res.data || res, true, type)
                  }
                  // after有return=false时，则阻止消息提示
                  if (hasReturnAfter !== false) {
                    ElMessage.success(res.message || '提交成功')
                  }
                  loading.value = false
                  resolve(res)
                  // 这里作全局提交结果处理
                })
                .catch((res: any) => {
                  if (typeof props.after === 'function') {
                    props.after(res, false, type)
                  }
                  loading.value = false
                  reject(res)
                  // 这里作全局异常提示处理
                })
          } else {
            const t = '请配置props.api地址'
            reject({config: true, message: t})
            console.error(t)
          }
        } else {
          reject({validate: true, ...fields})
          console.log('error submit!', fields)
        }
      })
    })
  }
  const resetFields = (type?: boolean) => {
    formEl.value.resetFields()
    if (type) { // 内部取消按钮时
      emits('cancel')
    }
    //　外部调用时不需emits
  }
  // 设置初始值
  const setValue = (obj: any) => {
    model.value = {...model.value, ...obj}
  }
  const getValue = () => {
    return model.value
  }
  const changeField = (prop: string, val: any) => {
    model.value[prop] = val
    emits('change', prop, val, model.value)
  }
  // 修改表单时，加载初始数据
  const getData = (data?: any) => {
    return new Promise((resolve, reject) => {
      const api = props.api?.detail
      if (api) {
        let params = Object.assign({}, props.params, data || {})
        if (props.before && typeof props.before === 'function') {
          params = props.before(params, 'detail') ?? params
        }
        if (params === false) {
          return false
        }
        loading.value = true
        getRequest(api, params)
            .then((res: any) => {
              let result = res.data
              if (props.after && typeof props.after === 'function') {
                result = props.after(result, true, 'detail') || result
              }
              model.value = result
              loading.value = false
              resolve(res)
              // 这里作全局提交结果处理
            })
            .catch((res: any) => {
              loading.value = false
              // 这里作全局异常提示处理
              if (typeof props.after === 'function') {
                props.after(res, false, 'detail')
              }
              reject(res)
            })
      } else {
        const t = "请设置api.detail"
        reject(t)
        console.error(t)
      }
    })
  }
  onMounted(() => {
    getModelValue(props.data)
  })
  onBeforeRouteLeave(() => {
    unWatch()
    unWatch2()
  })
  defineExpose({onSubmit, resetFields, setValue, getValue, getData})
</script>
<style>
.submit-btn-group {
  width: 100% !important;
  margin: 0 !important;
}

.submit-btn-group .el-form-item__content {
  display: flex;
  justify-content: center;
  margin: 0 !important;
}
</style>