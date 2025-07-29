<template>
  <div>
    <input v-model="model.name" type="text"/>

    <el-input v-model="model.name" type="text" @input="input"/>
    <el-input v-model="mValue.name" type="text"/>

  </div>
</template>

<script setup lang="ts">
  import {unref, ref} from "vue";

  const props = withDefaults(
      defineProps<{
        modelValue?: any // 表单项数据
      }>(),
      {
        modelValue: () => {
          return {}
        }
      }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
  }>()
  const mValue = ref(props.modelValue)
  const model = defineModel({
    default: { name: '1222'}
  });
  const input = (value: string): void => {
    console.log(value)
    mValue.value.name = value
  }
</script>

<style scoped lang="scss">

</style>