<template>
  <router-view v-slot="{ Component }">
    <component :is="Component" />
  </router-view>
</template>
<script setup lang="ts">
  import {loadScript,isMobile} from './utils'
  const host = window.location.host
  let erudaInit = false;
  if (isMobile()&&(host.indexOf('192.') !== -1 || host.indexOf('localhost') !== -1)) {
    loadEruda()
  }


  async function loadEruda() {
    if (erudaInit) return;
    if (window.eruda) return eruda.init();
    await loadScript('/js/eruda.js');
    eruda.init();
    erudaInit = true;
  }
</script>