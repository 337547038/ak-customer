<template>
  <el-form :inline="true" class="search-form" :model="searchForm" ref="formEl">
    <template v-for="(item, index) in columnsFilter" :key="index">
      <field
          v-bind="item.search"
          :render="item.search?.render"
          :prop="item.prop"
          :label="item.label"
          v-model="searchForm[item.prop]"
          @change="changeField(item.prop, $event)"
      />
    </template>
    <el-form-item>
      <el-button @click="clearClick">清空</el-button>
      <el-button type="primary" @click="searchClick">查询</el-button>
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
      return (
          item.search?.visible !== false &&
          item.search !== false &&
          !['selection', 'index'].includes(item.type) &&
          item.prop
      )
    })
  })
  const formEl = ref()
  const searchForm = ref({})
  const changeField = (prop: string, val: any) => {
    emits('fieldChange', prop, val, searchForm.value)
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
