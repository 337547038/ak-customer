<template>
  <el-form :inline="true">
    <el-form-item
      v-if="hasChild"
      label="选择下属用户"
    >
      <el-select
        v-model="userId"
        clearable
        placeholder="请选择下属用户"
        style="min-width: 180px"
        @change="change"
      >
        <el-option
          v-for="item in options"
          :key="item.id"
          :value="item.id"
          :label="item.userName"
        />
      </el-select>
    </el-form-item>
    <slot />
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
    if (!hasChild.value) {
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