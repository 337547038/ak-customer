<template>
  <div v-loading="loading">
    <div style="display: flex">
      <el-upload
        ref="uploadRef"
        v-model:file-list="fileList"
        :show-file-list="false"
        accept="image/png, image/jpeg, image/jpg"
        :http-request="httpRequest"
        multiple
      >
        <el-button
          type="primary"
          icon="upload-filled"
        >
          选择名片
        </el-button>
        <!--    <el-icon class="el-icon--upload">
                <upload-filled/>
              </el-icon>
                <div class="el-upload__text">
                拖动图片文件或 <em>点击上传</em>
              </div>-->
        <template #tip>
          <div class="el-upload__tip">
            支持jpg,png,jpeg图片格式
          </div>
        </template>
      </el-upload>
      <el-button
        type="danger"
        @click="clearClick"
      >
        清空
      </el-button>
    </div>
    <template
      v-for="(file, index) in fileList"
      :key="index"
    >
      <Card
        :file="file"
        @del-click="delClick(index)"
      />
    </template>
  </div>
</template>
<script setup lang="ts">
  import {createWorker} from 'tesseract.js';
  import {onMounted, ref} from "vue";
  import {getRequest} from "@/api";
  import Card from './components/card.vue'
  import {setStorage, getStorage, removeStorage} from "@/utils";

  const uploadRef = ref()
  const fileList = ref([])
  const worker = ref()
  const initTesseract = async () => {
    worker.value = await createWorker(['eng', 'chi_sim'], 1, {
      workerPath: "/js/tesseract/worker.min.js",
      corePath: '/js/tesseract/tesseract-core-simd-lstm.wasm.js', // 本地路径
      //logger: m => console.log(m),
    });
  }
  const loading = ref(false)
  const httpRequest = (fileOption: any) => {
    const {file, onProgress, onSuccess, onError} = fileOption
    // 找到当前文件并标记为上传中
    const currentFile = ref()
    fileList.value.forEach((f) => {
      if (f.uid === file.uid) {
        f.status = 'uploading'
        currentFile.value = f
      }
    })
    const params = new FormData();
    params.append('file', file);
    const options = {
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
        )
        currentFile.value.percentage = percentCompleted
        onProgress({percent: percentCompleted}, file)
      }
    }
    getRequest("upload", params, options)
        .then(async ({data}) => {
          currentFile.value.status = 'success'
          currentFile.value.url = data
          await getImgText(data, currentFile)
          onSuccess('上传成功', file);
          setStorage("fileList", fileList.value)
        })
        .catch(() => {
          onError('上传失败', file);
        })
  }
  const getImgText = async (data: string, currentFile: any) => {
    const ret = await worker.value.recognize(data);
    currentFile.value.workerText = ret.data.text
    currentFile.value.model = extractCardInfo(ret.data.text)
    await worker.value.terminate();
  }
  const extractCardInfo = (text: string) => {
    // 简单的正则表达式提取信息（实际应用中需要更复杂的逻辑）
    const nameMatch = text.match(/姓名[:：]\s*(\S+)/) || text.match(/^([^\d\n]+)\s*[\n\t]/);
    const titleMatch = text.match(/职位[:：]\s*(\S+)/) || text.match(/(经理|总监|工程师|设计师)/);
    const companyMatch = text.match(/公司[:：]\s*([^\n]+)/);
    const phoneMatch = text.match(/1[3-9]\d{9}/) || text.match(/\d{3,4}[- ]?\d{7,8}/);
    const emailMatch = text.match(/\w+@\w+\.\w+/);

    return {
      name: nameMatch ? nameMatch[1].trim() : '',
      position: titleMatch ? titleMatch[1].trim() : '',
      company: companyMatch ? companyMatch[1].trim() : '',
      tel: phoneMatch ? phoneMatch[0].trim() : '',
      email: emailMatch ? emailMatch[0].trim() : ''
    }
  }

  const delClick = (index: number) => {
    fileList.value.splice(index, 1)
    setStorage("fileList", fileList.value)
  }
  const clearClick = () => {
    fileList.value = []
    removeStorage("fileList")
    uploadRef.value.clearFiles()
  }
  onMounted(() => {
    initTesseract()
    // 页面刷新从本地读取，防止意见丢失
    fileList.value = getStorage("fileList") || [];
  })
</script>
<style scoped lang="scss">

</style>
