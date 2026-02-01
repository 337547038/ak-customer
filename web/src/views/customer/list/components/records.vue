<template>
  <el-timeline v-if="!isMobile()">
    <el-timeline-item
      v-for="(activity, index) in operateList"
      :key="index"
      :timestamp="activity.userName+activity.content"
    >
      {{ dateFormatting(activity.dataTime) }}
    </el-timeline-item>
  </el-timeline>
  <van-popup
    v-else
    v-model:show="showPicker"
    destroy-on-close
    position="right"
    teleport="body"
    :style="{ width: '88%', height: '100%' }"
    closeable
  >
    <div class="time-line">
      <div class="title">
        操作记录
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in operateList"
          :key="index"
          :timestamp="activity.userName+activity.content"
        >
          {{ dateFormatting(activity.dataTime) }}
        </el-timeline-item>
      </el-timeline>
    </div>
  </van-popup>
</template>

<script setup lang="ts">
  import {ref} from 'vue'
  import {getRequest} from "@/api";
  import {dateFormatting,isMobile} from "@/utils";

  const operateList = ref<any>([])
  const getData = (id:number) => {
    operateList.value = []
    showPicker.value = true
    if (id && operateList.value.length === 0) {
      getRequest("customerRecords", {tid: id})
          .then(res => {
            operateList.value = res.data.list || []
          })
    }
  }

  // wap
  const showPicker= ref(false)
  defineExpose({getData})
</script>

<style scoped lang="scss">
.time-line{padding:20px}
.title{font-size: 15PX;margin-bottom: 20px;font-weight: 700}
</style>