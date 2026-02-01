<template>
  <van-field
    v-bind="item.field"
    :name="item.prop"
    :label="item.label"
    :rules="item.rules as any"
    :required="getRequired(item.rules)"
  >
    <template #input>
      <van-uploader
        v-model="fileList"
        :disabled="disabled"
        :after-read="afterRead"
        @delete="deleteClick"
      />
    </template>
  </van-field>
</template>

<script setup lang="ts">
  import {ref, watch} from 'vue'
  import {getRequest} from "@/api";
  import {showFailToast} from "vant";
  import {onBeforeRouteLeave} from 'vue-router'
  import {getRequired} from "./rules";

  const props = withDefaults(
      defineProps<{
        item: { [key: string]: any }
        modelValue?: string
        disabled?: boolean
      }>(),
      {
        modelValue: '',
        disabled:false
      }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
  }>()
  const fileList = ref<any>([]);
  const unWatch = watch(() => props.modelValue, () => {
    if (props.modelValue && !fileList.value.length) {
      const model = props.modelValue.split(',');
      fileList.value = []
      model.forEach(item => {
        fileList.value.push({url: item});
      })
    }
  }, {immediate: true})
  const updateValue = () => {
    const uploadList = fileList.value.map((item:any) => item.url).join(',')
    emits('update:modelValue', uploadList)
  }
  const deleteClick = () => {
    updateValue()
  }
  const afterRead = (file:any) => {
    const params = new FormData();
    params.append('file', file.file);
    file.message = '上传中...'
    file.status = 'uploading'
    getRequest("upload", params)
        .then(({data}) => {
          file.status = 'done'
          file.url = data
          updateValue()
        })
        .catch(() => {
          file.message = '上传失败'
          file.status = 'failed'
          showFailToast(`${file.file.name}上传失败`)
        })
  }
  onBeforeRouteLeave(() => {
    unWatch()
  })
</script>

<style scoped lang="scss">

</style>