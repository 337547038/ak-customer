<template>
  <div>
    <el-button type="primary" @click="clearCache">清空缓存</el-button>
  </div>
</template>

<script setup lang="ts">
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";

  const clearCache = () => {
    getRequest("clearCache", {})
        .then(() => {
          ElMessage.success("清除成功")
        })
        .catch(() => {
        })
  }
</script>

<style scoped lang="scss">

</style>