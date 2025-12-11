<template>
  <div class="error-404">
    <img
      src="../../assets/img/404.svg"
      alt=""
      :style="{width: isMobile()?'100%':'50%'}"
    >
    <el-button
      v-if="isMobile()"
      @click="$router.push({ path: '/' })"
    >
      返回首页
    </el-button>
    <van-button
      v-else
      @click="$router.push({ path: '/' })"
    >
      返回首页
    </van-button>
  </div>
</template>
<route>
{meta:{layout:'hidden',permissions:false}}
</route>
<script setup lang="ts">
  import {isMobile} from "@/utils";
</script>

<style scoped lang="scss">
.error-404 {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100vw;
  height: 100vh;

  img {
    width: 50%;
  }
}
</style>
