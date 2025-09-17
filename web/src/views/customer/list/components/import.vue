<template>
  <el-dialog
    v-model="visible"
    width="500"
    title="导入客户"
    :before-close="cancelClick"
  >
    <el-form v-loading="loading">
      <el-form-item label="请选择xlsx文件">
        <el-upload
          ref="uploadRef"
          v-model:file-list="fileList"
          accept=".xlsx"
          :auto-upload="false"
          :http-request="httpRequest"
        >
          <template #trigger>
            <el-button type="primary">
              选择文件
            </el-button>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item class="form-item-flex">
        <el-button
          type="primary"
          @click="submitUpload"
        >
          确定
        </el-button>
        <el-button @click="cancelClick">
          取消
        </el-button>
      </el-form-item>
      <el-form-item class="form-item-flex">
        <a href="/template.xlsx">下载导入模板</a>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
  import {ref} from 'vue'
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";

  const loading = ref(false)
  const visible = ref(false)
  const uploadRef = ref()
  const callbackFn = ref()
  const fileList = ref([])

  const httpRequest = (file: any) => {
    const params = new FormData();
    params.append('file', file.file);
    loading.value = true;
    getRequest("customerImport", params)
        .then(() => {
          loading.value = false
          callbackFn.value && callbackFn.value()
          cancelClick()
          ElMessage.success("导入成功")
        })
        .catch(() => {
          loading.value = false
          ElMessage.error("导入失败")
        })
  }
  const cancelClick = () => {
    uploadRef.value.clearFiles()
    visible.value = false
    callbackFn.value = ''
  }

  const submitUpload = () => {
    uploadRef.value!.submit()
  }

  const openDialog = (callback: any) => {
    visible.value = true
    callbackFn.value = callback
  }

  defineExpose({openDialog})
</script>


<style scoped lang="scss">

</style>