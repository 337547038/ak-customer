<template>
  <el-form-item style="width: 100%">
    <el-upload
        v-model:file-list="fileList"
        :http-request="httpRequest"
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove"
    >
      <el-icon>
        <Plus/>
      </el-icon>
    </el-upload>

    <el-dialog v-model="dialogVisible">
      <img w-full :src="dialogImageUrl" alt="Preview Image"/>
    </el-dialog>
  </el-form-item>
</template>

<script setup lang="ts">
  import {ref, watch, nextTick} from 'vue'
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";
  import {onBeforeRouteLeave} from 'vue-router'

  const props = withDefaults(
      defineProps<{
        modelValue?: string
      }>(),
      {
        modelValue: ''
      }
  )

  const emits = defineEmits<{
    (e: 'update:modelValue', value: string): void
  }>()

  const fileList = ref([])
  const dialogImageUrl = ref('')
  const dialogVisible = ref(false)
  const unWatch = watch(() => props.modelValue, () => {
    if (props.modelValue) {
      const model = props.modelValue.split(',');
      fileList.value = []
      model.forEach(item => {
        fileList.value.push({url: item});
      })
    }
  })

  const updateModeValue = (): void => {
    nextTick(() => {
      const urlString = fileList.value.length ? fileList.value.map(item => item.url).join(',') : '';
      emits("update:modelValue", urlString);
    })
  }
  //const delFilePath = ref([])
  const handleRemove = (file) => {
    // 这里直接删除后，如果没有提交保存，只是将图片删除并没从数据库中删除
    getRequest("uploadDel", {path: file.url})
    updateModeValue()
    // 发送删除请求
  }
  const handlePictureCardPreview = (uploadFile) => {
    dialogImageUrl.value = uploadFile.url!
    dialogVisible.value = true
  }
  const httpRequest = (file: any) => {
    const params = new FormData();
    params.append('file', file.file);
    getRequest("upload", params)
        .then(({data}) => {
          fileList.value.forEach((item) => {
            if (item.uid === file.file?.uid) {
              item.url = data
            }
          })
          updateModeValue()
        })
        .catch(() => {
          fileList.value.forEach((item, index) => {
            if (item.uid === file.file?.uid) {
              fileList.value.splice(index, 1)
            }
          })
          updateModeValue()
          ElMessage.error(`${file.file.name}上传失败`)
        })
  }

  onBeforeRouteLeave(() => {
    unWatch() //销毁监听器
  })
</script>

<style scoped lang="scss">

</style>