<template>
  <div>
    <template v-for="(btn, index) in buttons" :key="index">
      <el-popconfirm
        v-bind="btn.popConfirm"
        v-if="btn.render === 'confirm'"
        @confirm="btnClick(btn)"
      >
        <template #reference>
          <div>
            <table-button :btn="btn" :row="row" :position="position" />
          </div>
        </template>
      </el-popconfirm>
      <table-button
        :btn="btn"
        :row="row"
        @click="btnClick(btn)"
        :position="position"
        v-else
      />
    </template>
  </div>
</template>
<script setup lang="ts">
  import type { Button } from '../types'
  import TableButton from './button.vue'

  const props = withDefaults(
    defineProps<{
      row: any // 表格右侧是当前行数据，表格上面按钮时为勾选中的行
      buttons: Button[]
      position?: string //位置
    }>(),
    {
      row: () => {
        return {}
      }
    }
  )
  const emits = defineEmits<{
    (e: 'click', key: string): void
  }>()

  //按钮事件规则，当设置了自定义click事件时，当return false时可阻止默认事件
  const btnClick = (btn: Button) => {
    let clickEventResult: any = true
    if (btn.click && typeof btn.click === 'function') {
      clickEventResult = btn.click(props.row)
    }
    //只有系统预设的add,edit,detail,del才有默认事件
    if (clickEventResult === false) {
      return false
    }
    if (btn.key && ['add', 'del', 'edit', 'detail'].includes(btn.key)) {
      emits('click', btn)
    }
  }

  //操作按钮处理结束
</script>
<style scoped lang="scss">
  .btn-group {
    :deep(button) {
      padding: 4px 5px;
      height: auto;
    }
  }
</style>
