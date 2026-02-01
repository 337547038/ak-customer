<template>
  <pc-layout v-if="!isMobile()" />
  <wap-layout v-else />
</template>

<script setup lang="ts">
import PcLayout from './index.vue'
import WapLayout  from './wap/index.vue'
import {isMobile} from "@/utils"
</script>