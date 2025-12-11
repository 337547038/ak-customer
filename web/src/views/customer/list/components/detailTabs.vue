<template>
  <el-drawer
    v-model="visible"
    size="88%"
    :title="title"
    :before-close="cancelClick"
  >
    <el-tabs
      v-model="activeName"
      @tab-change="handleClick"
    >
      <el-tab-pane
        label="客户详情"
        name="detail"
      >
        <ak-form
          ref="formRef"
          v-model="formModel"
          pk="id"
          label-width="100"
          class="flex-form flex-form-2"
          :after="afterSubmit"
          :data="formDataFilter"
          :before="formBefore"
          :disabled="disabled||tabsNameIsShare"
          :api="{ detail: 'customerDetail',add:'customerSave', edit:'customerEdit' }"
          @cancel="cancelClick"
        >
          <template #files="{rows}">
            <uploadFiles
              v-if="visible"
              v-model="formModel.files"
              :prop="rows.prop"
              :label="rows.label"
            />
          </template>
        </ak-form>
      </el-tab-pane>
      <el-tab-pane
        v-if="!isAddForm"
        label="跟进记录"
        name="follow"
      >
        <Follow
          ref="followRef"
          key-columns="customerFollow"
          :company="drawerObj.company"
        />
      </el-tab-pane>
      <el-tab-pane
        v-if="!isAddForm"
        label="联系人"
        name="contact"
      >
        <Contact
          ref="contactRef"
          key-columns="customerContact"
          :company="drawerObj.company"
        />
      </el-tab-pane>
      <el-tab-pane
        v-if="!isAddForm"
        label="合同"
        name="contract"
      >
        <Contract
          ref="contractRef"
          key-columns="customerContract"
        />
      </el-tab-pane>
      <el-tab-pane
        v-if="!isAddForm"
        label="操作记录"
        name="operate"
      >
        <records ref="recordsRef" />
      </el-tab-pane>
      <el-tab-pane
        v-if="formModel?.userId===userId&&formModel.shareUserId"
        label="分享详情"
        name="share"
      >
        <ShareInfo
          ref="shareInfoRef"
          v-model:user-ids="formModel.shareUserId"
          :disabled="disabled||tabsNameIsShare"
          :customer-id="drawerObj.id"
          @change="shareChange"
        />
      </el-tab-pane>
    </el-tabs>
  </el-drawer>
</template>

<script setup lang="ts">
  import {computed, ref, nextTick, provide} from 'vue'
  import {dateFormatting} from "@/utils";
  import Contact from "../../contact/index.vue";
  import formData from "../formData";
  import uploadFiles from './upload.vue'
  import Follow from "../../follow/index.vue";
  import ShareInfo from "./shareInfo.vue";
  import Contract from "@/views/contract/contract/index.vue";
  import {useLayoutStore} from '@/store/layout'
  import Records from "./records.vue";

  const props = withDefaults(
      defineProps<{
        disabled?: boolean // 公海无效客户引用时为true
        tabsType?: string
      }>(),
      {
        tabsType:''
      }
  )
  const layoutStore = useLayoutStore();
  const userId = computed(() => {
    return layoutStore.userInfo?.id
  })
  const tabsNameIsShare = computed(() => {
    return props.tabsType === 'shareWithMe'
  })
  const formModel = ref<any>({})
  const formRef = ref()
  const activeName = ref('detail')
  // drawer
  const visible = ref(false)
  const drawerObj = ref<any>({})
  const isAddForm = ref(true)
  const title = ref()
  const submitCallback = ref()
  const cancelClick = () => {
    visible.value = false
    submitCallback.value && submitCallback.value()
    nextTick(() => {
      formModel.value = {}
      activeName.value = 'detail'
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

  const detailTabsProps = computed(() => {
    return {
      userId: drawerObj.value.userId,
      customerId: drawerObj.value.id,
      isComponents: true,
      tabsName: props.tabsType,
      disabled: props.disabled || tabsNameIsShare.value,
    }
  })
  provide("detailTabsProps", detailTabsProps)
  //tabs
  const recordsRef = ref()
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
        recordsRef.value.getData()
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
    /*if (type === 'detail') {
    }*/
    return data
  }
  const afterSubmit = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      cancelClick()
    } else if (type === 'detail') {
      // 按展示格式处理数据
      //从列表里取两个时间到详情里
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
  //联系人

  //跟进记录

  // 取消所有分享时
  const shareChange = () => {
    activeName.value = 'detail'
  }

  defineExpose({openDrawer})
</script>

<style scoped lang="scss">

</style>