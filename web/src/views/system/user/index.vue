<template>
  <user-index v-if="!isMobile()" />
  <wap-index v-else />
</template>

<script setup lang="ts">
import {isMobile} from "@/utils";
import UserIndex from './user.vue'
import WapIndex from './wap.vue'
</script>