<template>
  <el-form
    v-if="!isMobile()"
    :inline="true"
  >
    <el-form-item
      v-if="hasChild"
      label="选择下属用户"
    >
      <el-select
        v-model="userId"
        clearable
        placeholder="请选择下属用户"
        style="min-width: 180px"
        @change="change"
      >
        <el-option
          v-for="item in options"
          :key="item.id"
          :value="item.id"
          :label="item.userName"
        />
      </el-select>
    </el-form-item>
    <slot />
  </el-form>
  <div v-else>
    <van-cell-group class="w-container">
      <van-field
        v-if="hasChild"
        v-model="userName"
        name="username"
        label="选择下属用户"
        placeholder="请选择下属用户"
        is-link
        readonly
        @click="showPicker = true"
      />
    </van-cell-group>
    <van-popup
      v-model:show="showPicker"
      destroy-on-close
      round
      position="bottom"
    >
      <van-picker
        v-model="userId"
        :loading="loading"
        title="选择下属用户"
        :columns-field-names="{ text: 'userName', value: 'id'}"
        :columns="options"
        @cancel="showPicker = false"
        @confirm="change"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
  import {ref, computed, onMounted} from 'vue'
  import {useLayoutStore} from '@/store/layout'
  import {getRequest} from "@/api"
  import {isMobile} from "@/utils";

  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()

  const layerStore = useLayoutStore()
  const hasChild = computed(() => {
    return layerStore.userInfo?.hasChild
  })
  const userId = ref()
  const options = ref<any>([])
  const getChildUser = () => {
    if (!hasChild.value) {
      return
    }
    loading.value = true
    getRequest("userChildList", {})
        .then(res => {
          options.value = res.data
          loading.value = false
        })
  }
  const change = (obj: any): void => {
    if (isMobile()) {
      if (obj.selectedOptions && obj.selectedValues) {
        userName.value = obj.selectedOptions[0]?.userName
        userId.value = obj.selectedValues[0]
        emits('change', userId.value)
      }
      showPicker.value = false
    } else {
      emits('change', obj)
    }
  }

  // wap start
  const loading = ref(false)
  const showPicker = ref(false)
  const userName = ref()

  onMounted(() => {
    getChildUser()
  })
</script>

<style scoped lang="scss">

</style>