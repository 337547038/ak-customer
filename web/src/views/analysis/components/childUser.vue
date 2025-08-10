<template>
  <el-form :inline="true">
    <el-form-item label="选择下属用户" v-if="hasChild">
      <el-select
          clearable
          @change="change"
          v-model="userId"
          placeholder="请选择下属用户"
          style="min-width: 180px">
        <el-option v-for="item in options" :value="item.id" :key="item.id" :label="item.userName"/>
      </el-select>
    </el-form-item>
    <slot></slot>
  </el-form>
</template>

<script setup lang="ts">
  import {ref, computed, onMounted} from 'vue'
  import {useLayoutStore} from '@/store/layout'
  import {getRequest} from "@/api"

  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()

  const layerStore = useLayoutStore()
  const hasChild = computed(() => {
    return layerStore.userInfo?.hasChild
  })
  const userId = ref()
  const options = ref([])
  const getChildUser = () => {
    if (!hasChild) {
      return
    }
    getRequest("userChildList", {})
        .then(res => {
          options.value = res.data
        })
  }
  const change = (value: any): void => {
    emits('change', value)
  }
  onMounted(() => {
    getChildUser()
  })
</script>

<style scoped lang="scss">

</style>