<template>
  <el-select
    v-model="value"
    filterable
    remote
    :remote-method="remoteMethod"
    :loading="loading"
    @blur="blur"
    @change="change"
  >
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="item.name"
      :value="item.id"
    />
  </el-select>
</template>

<script setup lang="ts">
  import {ref, watch, nextTick} from 'vue'
  import {getRequest} from "@/api";
  import {onBeforeRouteLeave} from 'vue-router'

  const props = withDefaults(
      defineProps<{
        //placeholder?: string
        modelValue?: number | undefined
        customerId?: number|null // 客户id
        userId?: number|null // 查询指定下属会员
      }>(),
      {
        modelValue:undefined,
        customerId:null,
        userId:null,
      }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'blur', value: any, name: string): void
  }>()
  const unWatch = watch(() => props.modelValue, (val: any): void => {
    value.value = val
    if (val) {
      nextTick(() => {
        remoteMethod("", val)
      })
    }
  })
  const options = ref<any>([])
  const value = ref('')
  const loading = ref(false)
  const remoteMethod = (query: string, id?: number) => {
    if (query || id) {
      loading.value = true
      let params:any = {name: query.replace('%', ''), tid: props.customerId, userId:props.userId}
      if (id) {
        params = {id: id, userId:props.userId}
      }
      getRequest("contactList", params)
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
  }
  const blur = () => {
    nextTick(() => {
      if (value.value && options.value.length > 0) {
        const item = options.value.find((obj:any) => obj.id === value.value)
        emits('blur', value.value, item.name)
      }
    })
  }

  onBeforeRouteLeave(() => {
    unWatch()
  })
</script>

<style scoped lang="scss">

</style>