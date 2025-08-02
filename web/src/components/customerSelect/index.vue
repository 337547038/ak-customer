<template>
  <el-select
      v-model="value"
      placeholder="请输入客户名称"
      filterable
      remote
      :remote-method="remoteMethod"
      :loading="loading"
      @change="change">
    <el-option
        v-for="item in options"
        :key="item.id"
        :label="item.company"
        :value="item.id"
    />
  </el-select>
</template>

<script setup lang="ts">
  import {ref, watch} from 'vue'
  import {getRequest} from "@/api";
  import {onBeforeRouteLeave} from 'vue-router'

  const props = withDefaults(
      defineProps<{
        modelValue?: number
        userId?: number // 查询指定会员下的客户
      }>(),
      {}
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()
  const unWatch = watch(() => props.modelValue, (val: any): void => {
    value.value = val
    if (val && options.value.length === 0) {
      remoteMethod("", val)
    }
  })
  const options = ref([])
  const value = ref('')
  const loading = ref(false)
  const remoteMethod = (query: string, id?: number) => {
    if (query || id) {
      loading.value = true
      let params = {company: query, userId: props.userId}
      if (id) {
        params = {id: id}
      }
      getRequest("customerList", params)
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
  const change = () => {
    emits('update:modelValue', value.value)
    emits('change', value.value)
  }

  onBeforeRouteLeave(() => {
    unWatch()
  })
</script>

<style scoped lang="scss">

</style>