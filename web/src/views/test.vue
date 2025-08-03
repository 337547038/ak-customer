<script setup lang="ts">

  import {ref, unref} from "vue";
  import {getRequest} from '@/api'

  const click=()=>{
    getRequest("customer/contact/get",{
      id:50
    })
  }
</script>

<template>
  <div>
   <el-button @click="click">click</el-button>
  </div>

</template>

<style scoped lang="scss">

</style>