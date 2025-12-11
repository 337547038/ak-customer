<template>
  <role-index v-if="!isMobile()" />
  <wap-index v-else />
</template>

<script setup lang="ts">
import {isMobile} from "@/utils";
import RoleIndex from './role.vue'
import WapIndex from './wap.vue'
</script>