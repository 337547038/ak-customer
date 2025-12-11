<template>
  <field-popup
    v-model="pickerLabel"
    v-model:show-picker="showPicker"
    :item="item"
    @clear-click="clearClick"
    @click-input="clickInput"
  >
    <template v-if="multiple">
      <h3 class="title">
        {{ item.label }}
      </h3>
      <van-search
        v-if="remote"
        v-model="searchKeyword"
        show-action
        placeholder="请输入搜索关键词"
        @search="onSearch"
      >
        <template #action>
          <div @click="onSearch">
            搜索
          </div>
        </template>
      </van-search>
      <van-checkbox-group
        v-model="pickerValueArray"
        class="scroll"
      >
        <van-cell-group inset>
          <van-cell
            v-for="col in columns"
            :key="col"
            clickable
            :title="`${col[getText]}`"
          >
            <template #right-icon>
              <van-checkbox
                :name="col[getValue]+''"
                @click.stop
              />
            </template>
          </van-cell>
        </van-cell-group>
      </van-checkbox-group>
      <div
        class="btn"
        @click="checkboxConfirm"
      >
        确定
      </div>
    </template>
    <van-picker
      v-else
      v-bind="item.picker"
      v-model="pickerValueArray"
      :loading="loading"
      :title="item.label"
      :columns="columns"
      @cancel="showPicker = false"
      @confirm="onConfirm"
    >
      <template #columns-top>
        <van-search
          v-if="remote"
          v-model="searchKeyword"
          show-action
          placeholder="请输入搜索关键词"
          @search="onSearch"
        >
          <template #action>
            <div @click="onSearch">
              搜索
            </div>
          </template>
        </van-search>
      </template>
    </van-picker>
  </field-popup>
</template>

<script setup lang="ts">
  import {ref, computed, watch, onMounted} from 'vue'
  import {useLayoutStore} from "@/store/layout";
  import {getRequest} from "@/api";
  import FieldPopup from './fieldPopup.vue'

  const props = withDefaults(
      defineProps<{
        modelValue: any,
        item: { [key: string]: any }
      }>(),
      {}
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()
  const searchKeyword = ref('')
  const layoutStore = useLayoutStore()
  const loading = ref(false)
  const pickerValue = defineModel<number | string>()
  const showPicker = ref<boolean>(false)
  const pickerValueArray = ref<any>([])
  const ajaxOptions = ref([])
  watch(() => pickerValue.value, (val: any) => {
    if (val !== undefined) {
      pickerValueArray.value = (pickerValue.value + '').split(',')
    }
  }, {immediate: true})
  // 是否多选
  const multiple = computed(() => {
    return props.item.picker?.multiple
  })
  // 显示搜索远程获取
  const remote = computed(() => {
    const {picker = {}} = props.item || {};
    const {ajax = {}, remote} = picker;
    return remote && ajax
  })
  // 数据加载模式,点击弹出选择框时重新加载
  const openLoad = computed(() => {
    return props.item.picker?.openLoad
  })
  const checkboxConfirm = () => {
    onConfirm({selectedValues: pickerValueArray.value})
  }
  const getText = computed(() => {
    const {picker = {}} = props.item
    const {
      columnsFieldNames: {text} = {}
    } = picker
    return text || 'text'
  })
  const getValue = computed(() => {
    const {picker = {}} = props.item
    const {
      columnsFieldNames: {value} = {}
    } = picker
    return value || 'value'
  })
  const columns = computed(() => {
    const {options, picker = {}} = props.item
    if (typeof options === 'string') {
      // 使用字典
      const dict = layoutStore.getSystemDict(options)
      return formatObj(dict)
    } else if (Object.prototype.toString.call(options) === '[object Object]') {
      // 使用object
      return formatObj(options)
    } else if (picker.ajax?.api) {
      // 通过接口获取
      return ajaxOptions.value
    } else {
      return options
    }
  })
  const pickerLabel = computed(() => {
    if (pickerValue.value !== undefined && pickerValue.value !== '' && columns.value.length) {
      if (multiple.value) {
        const temp:any = []
        columns.value.forEach((col:any) => {
          if ((pickerValue.value + '').split(',').includes(col[getValue.value] + '')) {
            temp.push(col[getText.value])
          }
        })
        return temp.join(',') || pickerValue.value
      } else {
        const item = columns.value.find((item:any) => item[getValue.value] + '' === pickerValue.value + '');
        return item ? item[getText.value] : pickerValue.value + '';
      }
    } else {
      return pickerValue.value
    }
  })
  const clearClick = () => {
    pickerValue.value = ''
    emits('update:modelValue', '')
    emits('change', '')
  }
  const onConfirm = ({selectedValues}:{selectedValues:any}) => {
    showPicker.value = false
    let val = selectedValues[0]
    if (multiple.value) {
      // 多选时使用,隔开
      val = selectedValues?.join(',')
    } else {
      pickerValueArray.value = selectedValues
    }
    emits('update:modelValue', val)
    emits('change', val)
  }
  //将object转array
  const formatObj = (obj: { [key: string]: any }) => {
    if (obj && Object.keys(obj).length) {
      const temp = []
      for (const key in obj) {
        const num = Number(key)
        const numKey = isNaN(num) ? key : num
        temp.push({[getText.value]: obj[key], [getValue.value]: numKey})
      }
      return temp
    }
    return []
  }
  const getAjax = () => {
    const {picker = {}} = props.item || {};
    const {ajax = {}, keyword = 'keyword'} = picker;
    const {api, data, after, before} = ajax;
    if (api) {
      let params = Object.assign({}, data, {[keyword]: searchKeyword.value})
      if (typeof before === 'function') {
        params = before(params) ?? data
      }
      getRequest(api, params)
          .then((res: any) => {
            if (typeof after === 'function') {
              ajaxOptions.value = after(res.data.list || res.data)
            } else {
              ajaxOptions.value = res.data.list || res.data
            }
          })
    }
  }
  const clickInput = () => {
    if (openLoad.value) {
      getAjax()
    }
  }
  const onSearch = () => {
    getAjax()
  }
  onMounted(() => {
    getAjax()
  })
  defineExpose({})
</script>

<style scoped lang="scss">
.title {
  text-align: center;
  padding: 20px;
  font-size: 24px
}

.btn {
  text-align: center;
  padding: 20px;
  font-size: 24px;
  border-top: 10px solid #e4e4e4;
}

.scroll {
  max-height: 40vh;
  overflow-x: hidden;
  overflow-y: auto
}
</style>