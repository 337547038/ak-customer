<template>
  <div
    class="progress-container"
    :class="{'wap-container':isMobile()}"
  >
    <div
      class="img-item"
    >
      <div class="img-box">
        <el-progress
          :percentage="file.percentage || 0"
          :status="getProgressStatus(file)"
        />
        <img
          :src="file.url"
          alt=""
        >
        <div class="file-name">
          {{ file.name }}
        </div>
      </div>
      <div class="ocr-content">
        <p>识别结果：</p>
        <p>{{ workText }}</p>
      </div>
      <div class="ocr-text">
        <p>提取结果</p>
        <ak-form
          ref="formRef"
          :model-value="file.model"
          :btn-text="false"
          :rules="rules"
          label-width="90"
          :after="afterSubmit"
          :api="{add:'customerScanCard'}"
          :data="formData"
        />
      </div>
      <div class="btn">
        <el-icon
          v-if="success"
          size="26px"
          color="green"
        >
          <CircleCheck />
        </el-icon>
        <template v-else>
          <el-button
            type="danger"
            @click="emits('delClick')"
          >
            移除
          </el-button>
          <el-button
            type="primary"
            :loading="loading"
            @click="submitClick"
          >
            一键入库
          </el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import {ref, computed} from 'vue'
  import validate from '@/components/form/validate'
  import {ElMessage} from "element-plus";
  import {isMobile} from "@/utils";

  const props = withDefaults(
      defineProps<{
        file?: any
      }>(),
      {
        file:()=>{return {}}
      }
  )
  const emits = defineEmits<{
    (e: 'delClick'): void
  }>()

  const workText = computed(() => {
    const {workerText, status} = props.file;
    if (workerText) {
      return workerText
    }
    if (status === 'success') {
      return '识别中...'
    } else {
      return '图片上传中..'
    }
  })
  const success = ref(false)
  const loading = ref<boolean>(false)
  const formRef = ref()
  const getProgressStatus = (file) => {
    if (file.status === 'success') return 'success';
    if (file.status === 'fail') return 'exception';
    return '';
  }
  const rules = {
    name: [validate('required', '联系人姓名不能为空')],
    company: [validate('required', '客户名称不能为空')],
    phone: [validate('required', '联系人电话不能为空'), validate('phone')],
    email: [validate('email')]
  }
  const formData = ref([
    {prop: 'name', label: '姓名'},
    {prop: 'company', label: '公司名称'},
    {prop: 'phone', label: '手机号码'},
    {prop: 'position', label: '职务'},
    {prop: 'email', label: '邮箱'},
  ])

  const submitClick = () => {
    loading.value = true
    formRef.value.onSubmit()
        .then(({data}) => {
          loading.value = false
          success.value = true
          ElMessage.success(data)
        })
        .catch(() => {
          loading.value = false
        })
  }

  const afterSubmit = () => {
    return false // 阻止默认提交消息提示
  }
</script>

<style scoped lang="scss">
.progress-container {
  padding-top: 10PX
}

.img-item {
  display: flex;
  margin-bottom: 10PX;

  .btn {
    margin-left: 10PX;
    display: flex;
    align-items: center
  }
}

.img-box {
  position: relative;
  overflow: hidden;
  width: 360PX;
  border-radius: 5PX;

  img {
    width: 100%;
    height: 216PX;
    display: block;
  }

  .file-name {
    background: rgba(0, 0, 0, .5);
    height: 30PX;
    line-height: 30PX;
    text-align: center;
    position: absolute;
    left: 0;
    bottom: 0;
    z-index: 2;
    width: 100%;
    color: #fff;
  }

  :deep(.el-progress) {
    width: 100%;
    position: absolute;
    left: 0;
    top: 0;
  }

  :deep(.el-progress__text) {
    min-width: 20PX
  }
}

.ocr-content {
  margin: 0 10PX;
  width: 300PX;
  padding: 10PX;
  line-height: 25PX;
  overflow: hidden;
  background: #fff;
  border-radius: 5PX;
}

.ocr-text {
  padding: 5PX 10PX;
  background: #fff;
  border-radius: 5PX;
  width: 430PX;

  p {
    margin-bottom: 8PX
  }
}

:deep(.el-form-item) {
  margin-bottom: 3PX
}

.wap-container{padding:10px;background: #fff;margin-bottom: 10px;
  .img-item{ flex-wrap: wrap;}
  .img-box,.ocr-content{width: 50%;margin: 0}
}
</style>