<script setup lang="ts">

  import {ref, unref} from "vue";
  import {getRequest} from '@/api'

  const click=()=>{
    getRequest("followSave",{
      "customerId":'123',
      "contactId":'12',
      type:1,
      adc:'ab',
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