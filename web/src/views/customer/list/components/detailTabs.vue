<template>
  <el-drawer
      v-model="visible" size="88%"
      :title="title"
      :before-close="cancelClick">
    <el-tabs v-model="activeName" @tab-change="handleClick">
      <el-tab-pane label="客户详情" name="detail">
        <ak-form
            ref="formRef"
            pk="id"
            label-width="100"
            class="flex-form flex-form-2"
            @cancel="cancelClick"
            :after="afterSubmit"
            :data="formDataFilter"
            :before="formBefore"
            :disabled="disabled||tabsNameIsShare"
            :api="{ detail: 'customerDetail',add:'customerSave', edit:'customerEdit' }"
            v-model="formModel">
          <template #files="{rows}">
            <uploadFiles
                :prop="rows.prop"
                :label="rows.label"
                v-model="formModel.files"
                v-if="visible"/>
          </template>
        </ak-form>
      </el-tab-pane>
      <el-tab-pane label="跟进记录" name="follow" v-if="!isAddForm">
        <Follow
            :disabled="disabled"
            :is-components="true"
            ref="followRef"
            keyColumns="customerFollow"
            :customerId="drawerObj.id"
            :userId="drawerObj.userId"
            :company="drawerObj.company"/>
      </el-tab-pane>
      <el-tab-pane label="联系人" name="contact" v-if="!isAddForm">
        <Contact
            keyColumns="customerContact"
            :disabled="disabled||tabsNameIsShare"
            :isComponents="true"
            :tid="drawerObj.id"
            :company="drawerObj.company"
            ref="contactRef"/>
      </el-tab-pane>
      <el-tab-pane label="合同" name="contract" v-if="!isAddForm">
        <Contract
            :isComponents="true"
            :cId="drawerObj.id"
            ref="contractRef"/>
      </el-tab-pane>
      <el-tab-pane label="操作记录" name="operate" v-if="!isAddForm">
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
      <el-tab-pane label="分享详情" name="share" v-if="formModel.shareUserId">
        <ShareInfo
            :disabled="disabled||tabsNameIsShare"
            ref="shareInfoRef"
            v-model:userIds="formModel.shareUserId"
            :customer-id="drawerObj.id"
            @change="shareChange"/>
      </el-tab-pane>
    </el-tabs>
  </el-drawer>
</template>

<script setup lang="ts">
  import {computed, ref, nextTick} from 'vue'
  import {getRequest} from "@/api";
  import {dateFormatting} from "@/utils";
  import Contact from "../../contact/index.vue";
  import formData from "../formData";
  import uploadFiles from './upload.vue'
  import Follow from "../../follow/index.vue";
  import ShareInfo from "./shareInfo.vue";
  import Contract from "@/views/contract/contract/index.vue";

  const props = withDefaults(
      defineProps<{
        disabled?: boolean
        tabsType?: string
      }>(),
      {}
  )
  const tabsNameIsShare = computed(() => {
    return props.tabsType === 'share2'
  })
  const formModel = ref({})
  const formRef = ref()
  const activeName = ref('detail')
  // drawer
  const visible = ref(false)
  const drawerObj = ref({})
  const isAddForm = ref(true)
  const title = ref()
  const submitCallback = ref()
  const cancelClick = () => {
    visible.value = false
    submitCallback.value && submitCallback.value()
    nextTick(() => {
      formModel.value = {}
      activeName.value = 'detail'
      operateList.value = []
      submitCallback.value = undefined
    })
  }
  const openDrawer = (isAdd: boolean, row: { [key: string]: any }, callback: any) => {
    visible.value = true
    isAddForm.value = isAdd
    drawerObj.value = row
    submitCallback.value = callback
    if (isAdd) {
      title.value = '新增客户'
    } else {
      title.value = row.company + '详情'
      nextTick(() => {
        formRef.value.getData({id: row.id})
      })
    }
  }
  //tabs
  const contactRef = ref()
  const contractRef = ref()
  const followRef = ref()
  const shareInfoRef = ref()
  const handleClick = (name: string) => {
    switch (name) {
      case 'follow':
        followRef.value.getData()
        break;
      case 'contact':
        contactRef.value.getData()
        break;
      case 'contract':
        contractRef.value.getData()
        break;
      case 'operate':
        getOperateList()
        break;
      case 'share':
        shareInfoRef.value.getData()
        break
    }
  }
  // 详情
  const formBefore = (data: any, type: string) => {
    if (['add', 'update'].includes(type)) {
      if (data.area) {
        data.area = data.area.join(',')
      }
    }
    if (type === 'detail') {
    }
    return data
  }
  const afterSubmit = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      cancelClick()
    } else if (type === 'detail') {
      // 按展示格式处理数据
      //　从列表里取两个时间到详情里
      result.lastTime = dateFormatting(drawerObj.value.lastTime)
      result.nextTime = dateFormatting(drawerObj.value.nextTime)
      if (result.area) {
        result.area = result.area.split(',')
      }
    }
    return result
  }
  const formDataFilter = computed(() => {
    if (isAddForm.value) {
      return formData.filter((item) => {
        return item.visible !== 'isDetailForm'
      })
    } else {
      return formData
    }
  })
  // 操作记录
  const operateList = ref([])
  const getOperateList = () => {
    if (drawerObj.value.id && operateList.value.length === 0) {
      getRequest("customerRecords", {tid: drawerObj.value.id})
          .then(res => {
            operateList.value = res.data.list || []
          })
    }
  }
  //　联系人

  //　跟进记录

  // 取消所有分享时
  const shareChange = () => {
    activeName.value = 'detail'
  }

  defineExpose({openDrawer})
</script>

<style scoped lang="scss">

</style>