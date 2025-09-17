<template>
  <div>
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

  const userIdsArray = ref([])
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
    const userIds = userIdsArray.value.map(item => item.id).join(',')
    updateShare(userIds, 'share')
  }

  const updateShare = (userIds: string, type: string) => {
    getRequest('customerShare', {ids: props.customerId, userId: userIds, type: type})
        .then(() => {
          ElMessage.success("操作成功")
          emits('update:userIds', userIds)
          if (userIds === '') {
            emits('change')
          }
        })
  }

  defineExpose({getData})
</script>

<style scoped lang="scss">

</style>