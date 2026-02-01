<template>
  <component
    :is="isMobile()?wapIndex:pcIndex"
    v-bind="$attrs"
    ref="comRef"
    @select-click="selectClick"
  />
</template>
<script setup lang="ts">
  import {ref} from 'vue'
  import pcIndex from './pc.vue'
  import wapIndex from './wap.vue'
  import {isMobile} from "@/utils";

  const emits = defineEmits<{
    (e: 'selectClick', val: any): void
  }>()
  const comRef = ref()
  const open = () => {
    comRef.value.open()
  }
  const showPopup = () => {
    comRef.value.showPopup()
  }
  const selectClick = (val: any) => {
    emits('selectClick', val)
  }
  defineExpose({open,showPopup})
</script>