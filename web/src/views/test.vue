<script setup lang="ts">

  import {ref, unref, onMounted} from "vue";
  import {getRequest} from '@/api'

  const click=()=>{
    getRequest("cache/clearAll",{
      id:50
    })
   //getRequest("customerScanCard",{"name":"1","company":"广州传媒有限公司","phone":"3","position":"4","email":"5"})
  }
  const model=ref({})
  const data = ref([
    {prop: 'name', label: '文本'}
  ])
</script>

<template>
  <div>
       <el-button @click="click">click</el-button>
<!--    <ak-form :data="data" v-model="model"></ak-form>-->
  </div>

</template>

<style scoped lang="scss">

</style>