<template>
  <el-tabs v-model="activeName" @tab-change="handleClick">
    <el-tab-pane label="跟进记录" name="follow"></el-tab-pane>
    <el-tab-pane label="联系人" name="contact"></el-tab-pane>
    <el-tab-pane label="操作记录" name="operate">
      <el-timeline>
        <el-timeline-item
            v-for="(activity, index) in operateList"
            :key="index"
            :timestamp="activity.userName+activity.content"
        >
          {{ dateFormatting(activity.dataTime) }}
        </el-timeline-item>
      </el-timeline>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
  import {ref} from 'vue'
  import {getRequest} from "@/api";
  import {dateFormatting} from "@/utils";

  const props = withDefaults(
      defineProps<{
        id: number // 表单项数据
      }>(),
      {}
  )

  const activeName = ref('operate')
  const handleClick = (name: string) => {
    switch (name) {
      case 'follow':
        break;
      case 'contact':
        break;
      case 'operate':
        getOperateList()
        break;
    }
  }
  // 操作记录
  const operateList = ref([])
  const getOperateList = () => {
    if (props.id && operateList.value.length === 0) {
      getRequest("customerRecords", {tid: props.id})
          .then(res => {
            operateList.value = res.data.list || []
          })
    }
  }
  //　联系人

  //　跟进记录
</script>

<style scoped lang="scss">

</style>