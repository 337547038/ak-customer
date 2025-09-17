<template>
  <div class="other">
    <el-tooltip
      v-if="showSearch&&searchIconVisible"
      effect="dark"
      content="展开 / 收起筛选"
      placement="top"
    >
      <el-button
        circle
        icon="Search"
        size="small"
        @click="searchToggleClick"
      />
    </el-tooltip>
    <column-display
      v-if="columnsIconVisible"
      :key-columns="keyColumns"
      :columns="columns"
      @change="change"
    />
  </div>
</template>

<script setup lang="ts">
  import ColumnDisplay from "./columnDisplay.vue";

   withDefaults(
      defineProps<{
        showSearch: boolean
        columnsIconVisible: boolean
        searchIconVisible: boolean
        columns: Array<string>
        keyColumns?: string
      }>(),
      {
        keyColumns:''
      }
  )

  const emits = defineEmits<{
    (e: 'searchToggleClick'): void
    (e: 'update:modelValue', val: string[]): void
  }>()

  const change = (val: string[]) => {
    emits("update:modelValue", val)
  }

  const searchToggleClick = () => {
    emits("searchToggleClick")
  }
</script>