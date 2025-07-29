<template>
  <div class="other">
    <el-tooltip
        effect="dark"
        content="展开 / 收起筛选"
        placement="top"
        v-if="showSearch&&searchIconVisible">
      <el-button
          circle
          icon="Search"
          size="small"
          @click="searchToggleClick"
      />
    </el-tooltip>
    <column-display
        :keyColumns="keyColumns"
        :columns="columns"
        @change="change"
        v-if="columnsIconVisible"/>
  </div>
</template>

<script setup lang="ts">
  import ColumnDisplay from "./columnDisplay.vue";

  const props = withDefaults(
      defineProps<{
        showSearch: boolean
        columnsIconVisible: boolean
        searchIconVisible: boolean
        columns: Array<string>
        keyColumns?: string
      }>(),
      {}
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