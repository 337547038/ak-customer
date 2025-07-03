<!-- Created by 337547038 on 2022/7/14. -->
<template>
  <div>
    <el-form-item label="自定义组件">
      <el-input :placeholder="placeholder" />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
  const props = withDefaults(
    defineProps<{
      placeholder?: string
    }>(),
    {}
  )
</script>
