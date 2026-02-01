<template>
  <el-popover
    :width="80"
    placement="bottom-end"
    trigger="click"
    @hide="popoverHideClick"
    @show="popoverShowClick"
  >
    <template #default>
      <el-checkbox-group v-model="columnsCheck">
        <el-checkbox
          v-for="item in columnsFilter"
          :key="item.prop || item.type"
          :value="item.prop || item.type"
        >
          {{ item.label || item.type }}
        </el-checkbox>
      </el-checkbox-group>
    </template>
    <template #reference>
      <div>
        <el-tooltip
          effect="dark"
          content="设置列显示隐藏"
          placement="top"
        >
          <el-button
            circle
            :icon="SetUp"
            size="small"
          />
        </el-tooltip>
      </div>
    </template>
  </el-popover>
</template>
<script setup lang="ts">
  import {ref, onMounted, computed} from 'vue'
  import {useFormTable} from '@/store/formTable'
  import {Columns} from '@/components/list/types'
  import {useRoute} from 'vue-router'
  import {SetUp} from '@element-plus/icons-vue'
  const props = withDefaults(
      defineProps<{
        columns?: Columns[]
        keyColumns?: string
      }>(),
      {
        columns: () => {
          return []
        },
        keyColumns: ""
      }
  )
  const emits = defineEmits<{
    (e: 'change', value: string[]): void
  }>()

  const formTableStore = useFormTable()
  const route = useRoute()
  const columnsCheck = ref<any>([])

  const keyColumnDisplay = computed(() => {
    return props.keyColumns || route.path
  })

  const columnsFilter = computed(() => {
    return props.columns.filter((item: Columns) => {
      return item.show !== false
    })
  })

  // 列显示隐藏设置收起时，这里可将设置保存于服务端或本地
  const popoverHideClick = () => {
    if (columnsCheck.value.length !== props.columns.length) {
      // 非全选状态时
      formTableStore.setColumnsCheck(keyColumnDisplay.value, columnsCheck.value)
    }
    emits('change', columnsCheck.value)
  }
  // 显示隐藏列设置
  const popoverShowClick = () => {
    const defaultValue = formTableStore.getColumnsCheck(keyColumnDisplay.value)
    if (defaultValue?.length) {
      columnsCheck.value = defaultValue
    } else if (!columnsCheck.value?.length) {
      // 为空时，则全部勾选上
      props.columns.forEach((item: any) => {
        const propType = item.prop || item.type
        if (propType) {
          columnsCheck.value.push(propType)
        }
      })
    }
  }
  onMounted(() => {
    columnsCheck.value = formTableStore.getColumnsCheck(keyColumnDisplay.value)
    emits('change', columnsCheck.value)
  })
</script>
