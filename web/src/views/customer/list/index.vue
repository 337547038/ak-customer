<template>
  <list-index v-if="!isMobile()" />
  <wap-index v-else />
</template>
<script setup lang="ts">
  import ListIndex from './list.vue'
  import WapIndex from './wap.vue'
  import {isMobile} from "@/utils";
</script>
<style scoped lang="scss">

</style>