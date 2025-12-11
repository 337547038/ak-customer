<template>
  <div :class="{'wap':isMobile()}">
    <h3 v-if="isMobile()">
      分享详情
    </h3>
    <div v-if="userIds==='0'">
      当前客户已共享给所有人
    </div>
    <div v-else-if="userIdsArray">
      <p style="padding-bottom: 15px">
        当前客户已共享给:
      </p>
      <el-tag
        v-for="(tag,index) in userIdsArray"
        :key="tag.id"
        :closable="!disabled"
        style="margin-right: 5px"
        @close="closeTag(tag,index)"
      >
        {{ tag.userName }}
      </el-tag>
    </div>
    <div
      v-if="!disabled"
      style="padding-top: 15px;"
    >
      <el-button
        type="danger"
        @click="updateShare('','cancelShare')"
      >
        取消分享
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
  import {ref} from 'vue'
  import {getRequest} from "@/api";
  import {ElMessage} from 'element-plus';
  import {isMobile} from "@/utils";
  import {showSuccessToast} from 'vant'

  const props = withDefaults(
      defineProps<{
        userIds: string
        customerId: string | number
        disabled?: boolean
      }>(),
      {}
  )

  const emits = defineEmits<{
    (e: 'update:userIds', value: string): void
    (e: 'change'): void
  }>()

  const userIdsArray = ref<any>([])
  const getData = () => {
    if (props.userIds) {
      getRequest("userListByIds", {ids: props.userIds})
          .then(res => {
            userIdsArray.value = res.data
          })
    }
  }
  const closeTag = (obj: any, index: number) => {
    userIdsArray.value.splice(index, 1)
    const userIds = userIdsArray.value.map((item:any) => item.id).join(',')
    updateShare(userIds, userIds ? 'myShare' : 'cancelShare')
  }

  const updateShare = (userIds: string, type: string) => {
    getRequest('customerShare', {ids: props.customerId, userId: userIds, type: type})
        .then(() => {
          if (isMobile()) {
            showSuccessToast('操作成功')
          } else {
            ElMessage.success("操作成功")
          }
          emits('update:userIds', userIds)
          if (userIds === '') {
            emits('change')
          }
        })
  }

  defineExpose({getData})
</script>

<style scoped lang="scss">
.wap {
  padding: 20px;
  font-size: 14PX;

  h3 {
    font-size: 15PX;
    padding: 10px 0
  }
}
</style>