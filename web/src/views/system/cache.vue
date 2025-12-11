<template>
  <div :style="styleRef">
    <el-button
      type="primary"
      @click="clearCache"
    >
      清空缓存
    </el-button>
  </div>
</template>

<script setup lang="ts">
  import {computed} from 'vue'
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus"
  import {showSuccessToast} from "vant"
  import {isMobile} from "@/utils";

  const clearCache = () => {
    getRequest("clearCache", {})
        .then(() => {
          isMobile() ? showSuccessToast("清除成功") : ElMessage.success("清除成功")
        })
        .catch(() => {
        })
  }

  const styleRef = computed(() => {
    if (isMobile()) {
      return {
        padding: '20px',
        display: 'flex',
        'justify-content': 'center'
      }
    }
    return {}
  })

</script>

<style scoped lang="scss">

</style>