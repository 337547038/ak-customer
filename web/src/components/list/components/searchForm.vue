<template>
  <el-form
    ref="formEl"
    :inline="true"
    class="search-form"
    :model="searchForm"
  >
    <template
      v-for="item in columnsFilter"
      :key="item.prop||item.type"
    >
      <field
        v-bind="item.search"
        v-model="searchForm[item.prop]"
        :render="item.search?.render"
        :prop="item.prop"
        :label="item.label"
        @change="changeField(item, $event)"
      />
    </template>
    <el-form-item>
      <el-button @click="clearClick">
        清空
      </el-button>
      <el-button
        type="primary"
        @click="searchClick"
      >
        查询
      </el-button>
    </el-form-item>
  </el-form>
</template>
<script setup lang="ts">
  import {ref, computed} from 'vue'
  import Field from '@/components/field/index.vue'
  import {Columns} from '@/components/list/types'

  const props = withDefaults(
      defineProps<{
        columns: Columns[]
      }>(),
      {}
  )
  const emits = defineEmits<{
    (e: 'onsubmit', value: any): void
    (e: 'fieldChange', prop: string, value: any, model: any): void
  }>()
  const columnsFilter = computed(() => {
    return props.columns.filter((item: Columns) => {
      let visible = item.search?.visible
      if (typeof item.search?.visible === 'function') {
        visible = item.search.visible()
      }
      return (
          visible !== false &&
          item.search !== false &&
          !['selection', 'index'].includes(<string>item.type) &&
          item.prop
      )
    })
  })
  const formEl = ref()
  const searchForm = ref<any>({})
  const changeField = (obj: any, val: any) => {
    if (obj.search?.changeRefresh) {
      searchClick() // 刷新列表
    }
    emits('fieldChange', obj.prop, val, searchForm.value)
  }
  const clearClick = () => {
    //formEl.value.resetFields()
    searchForm.value = {}
    searchClick()
  }
  const searchClick = () => {
    emits('onsubmit', searchForm.value)
  }
  const setValue = (val: any) => {
    searchForm.value = Object.assign({}, searchForm.value, val)
  }
  defineExpose({setValue})
</script>
