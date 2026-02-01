<template>
  <div class="form-container">
    <van-form
      ref="formRef"
      v-bind="formProps"
      :disabled="disabled"
      @submit="onSubmitClick"
    >
      <slot name="formTop" />
      <van-cell-group>
        <template
          v-for="item in dataFilter"
          :key="item.prop"
        >
          <slot
            v-if="item.render==='scope'&&item.prop"
            :name="item.prop"
            :rows="item"
            :value="model[item.prop]"
          />
          <van-field
            v-else-if="item.render==='switch'"
            :name="item.prop"
            :label="item.label"
            :required="getRequired(item.rules)"
          >
            <template #input>
              <van-switch
                v-model="model[item.prop]"
                :active-value="1"
                :inactive-value="0"
                v-bind="item.field"
                size="20px"
                @change="fieldValueChange(item,$event)"
              />
            </template>
          </van-field>
          <popup-select
            v-else-if="item.render==='picker'"
            v-model="model[item.prop]"
            :item="item"
            @change="fieldValueChange(item,$event)"
          />
          <cascader-picker
            v-else-if="item.render==='cascader'"
            v-model="model[item.prop]"
            :item="item"
            :lazy-load="item.cascader.lazyLoad"
            @change="fieldValueChange(item,$event)"
          />
          <date-picker
            v-else-if="item.render==='datePicker'"
            v-model="model[item.prop]"
            :item="item"
            @change="fieldValueChange(item,$event)"
          />
          <component
            :is="item.component"
            v-else-if="item.render==='component'"
            v-model="model[item.prop]"
            :data="item"
            @update:model-value="fieldValueChange(item,$event)"
          />
          <van-field
            v-else
            v-bind="item.field"
            v-model="model[item.prop]"
            :readonly="item.render==='text'"
            :label="item.label"
            :placeholder="getPlaceholder(item)"
            :rules="item.rules as any"
            :required="getRequired(item.rules)"
            :name="item.prop"
            @update:model-value="fieldValueChange(item,$event)"
          />
        </template>
      </van-cell-group>
      <slot name="formBottom" />
      <div
        v-show="cancelText||confirmText"
        class="submit-btn"
      >
        <van-button
          v-if="cancelText!==false"
          round
          block
          @click="cancelClick"
        >
          {{ cancelText }}
        </van-button>
        <van-button
          v-if="confirmText!==false"
          round
          block
          type="primary"
          native-type="submit"
          :disabled="disabled"
        >
          {{ confirmText }}
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
  import {ref, computed, onMounted} from 'vue'
  import {type FormProps, showSuccessToast, showFailToast} from 'vant';
  import type {FormData} from "./types";
  import {getRequest} from "@/api";
  import PopupSelect from './components/select.vue'
  import cascaderPicker from './components/cascader.vue'
  import DatePicker from './components/datePicker.vue'
  import {getRequired} from './components/rules'

  const props = withDefaults(
      defineProps<{
        formProps?: FormProps | null
        data: FormData[]
        confirmText?: string | boolean  //boolean时仅支持false表示不显示
        cancelText?: string | boolean
        hideFiled?: string[] // 不显示的字段
        submitUrl?: string // 表单提交url
        requestUrl?: string // 获取表单数据url
        before?: ((params: { [key: string]: any }, type: string) => any) | null
        after?: ((type: string, res: any, isSuccess: boolean) => any) | null
        disabled?:boolean
      }>(),
      {
        confirmText: '提交',
        cancelText: '取消',
        hideFiled: () => {
          return []
        },
        submitUrl: '',
        requestUrl: '',
        before: null,
        after: null,
        formProps: null,
        disabled:false
      }
  )
  const emits = defineEmits<{
    (e: 'fieldValueChange', key: string, value: any, model: { [key: string]: any }): void
    (e: 'submit', model: { [key: string]: any }): void
    (e: 'cancel'): void
  }>()

  const formRef = ref<HTMLFormElement>()
  const model = ref<any>({})
  const dataFilter:any = computed(() => {
    return props.data.filter(item => {
      let visible = item.visible
      if (typeof item.visible === 'function') {
        visible = item.visible()
      }
      return visible !== false && !props.hideFiled.includes(item.prop)
    })
  })
  const getIsPopup = (item: FormData) => {
    return ['select', 'cascader'].includes(item.render+'')
  }
  const getPlaceholder = (item: FormData) => {
    const label = getIsPopup(item) ? '请选择' : '请输入'
    return item.placeholder ? item.placeholder : `${label}${item.label}`
  }
  // 获取初始设置的值
  const getModelValue = () => {
    props.data?.forEach((item: FormData) => {
      if (item.prop && item.modelValue != undefined) {
        model.value[item.prop] = item.modelValue
      }
    })
  }
  const onSubmitClick = () => {
    emits('submit', model.value)
    submitRequest('submit').then().catch(() => {
    })
  }

  /**
   * 发送请求
   * @param type get和submit两种
   * @param data 提交的参数，submit时会合并表单数据
   */
  const submitRequest = (type: string, data?: { [key: string]: any }) => {
    return new Promise((resolve, reject) => {
      const url = type === 'get' ? props.requestUrl : props.submitUrl
      if (!url) {
        return reject('请设置submitUrl或requestUrl')
      }
      let params: any = type === 'get' ? data : Object.assign({}, model.value, data)
      if (props.before && typeof props.before === 'function') {
        params = props.before(JSON.parse(JSON.stringify(params)), type)
      }
      if (params === false) {
        return reject('中止操作')
      }
      getRequest(url, params)
          .then((res: any) => {
            let hasReturnAfter = null
            if (typeof props.after === 'function') {
              hasReturnAfter = props.after(type, res.data || res, true)
            }
            // after有return=false时，则阻止消息提示。请求数据时不提示
            if (hasReturnAfter !== false && type === 'submit') {
              showSuccessToast(res.message || '提交成功');
            }
            if (type === 'get') {
              model.value = hasReturnAfter || res.data
            }
            resolve(res)
          })
          .catch((err: any) => {
            if (typeof props.after === 'function') {
              props.after(type, err, false)
            }
            // 统一提示
            showFailToast(err.message || '请求异常')
            reject(err)
          })
    })
  }

  // 提交外部方法
  const onSubmit = () => {
    return new Promise((resolve, reject) => {
      submitRequest('submit')
          .then((res: any) => {
            resolve(res)
          })
          .catch((err: any) => {
            reject(err)
          })
    })
  }
  const getData = (data?: { [key: string]: any }) => {
    return new Promise((resolve, reject) => {
      submitRequest('get', data)
          .then((res: any) => {
            resolve(res)
          })
          .catch((err: any) => {
            reject(err)
          })
    })
  }
  const cancelClick = () => {
    formRef.value?.resetValidation()
    emits('cancel')
    model.value = {}
  }
  // 设置表单的值
  const setValue = (value: { [key: string]: any }) => {
    model.value = value
  }
  // 获取表单的值
  const getValue = () => {
    return model.value
  }

  // 表单值发生改变生
  const fieldValueChange = (obj: FormData, value: string) => {
    emits('fieldValueChange', obj.prop, value, model.value)
  }

  onMounted(() => {
    getModelValue()
  })
  defineExpose({setValue, getValue, onSubmit, getData})
</script>

<style scoped lang="scss">
.submit-btn {
  margin: 16px;
  display: flex;
  justify-content: space-between;

  button {
    margin: 0 10px
  }
}
</style>