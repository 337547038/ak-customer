<template>
  <el-select
      v-model="value"
      placeholder="请输入客户名称"
      filterable
      remote
      :remote-method="remoteMethod"
      :loading="loading">
    <el-option
        v-for="item in options"
        :key="item.id"
        :label="item.company"
        :value="item.id"
    />
  </el-select>
</template>

<script setup lang="ts">
  import {ref} from 'vue'
  import {getRequest} from "@/api";

  const options = ref([])
  const value = ref('')
  const loading = ref(false)
  const remoteMethod = (query: string) => {
    if (query) {
      loading.value = true
      getRequest("customerList", {company: query})
          .then(res => {
            options.value = res.data.list || []
            loading.value = false
          })
          .catch(() => {
            options.value = []
            loading.value = false
          })
    } else {
      options.value = []
    }
  }
</script>

<style scoped lang="scss">

</style>