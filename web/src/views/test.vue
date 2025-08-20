<script setup lang="ts">

  import {ref, unref, onMounted, computed} from "vue";
  import {getRequest} from '@/api'

  const click=()=>{
    getRequest("system/user/child",{
      })
   //getRequest("customerScanCard",{"name":"1","company":"广州传媒有限公司","phone":"3","position":"4","email":"5"})
  }

/*  const click = () => {
    activeName.value = 'child'
    console.log(activeName.value)
    console.log(columnsFilter.value.length)
  }*/

</script>

<template>
  <div>
    <el-button @click="click">click</el-button>

  </div>

</template>

<style scoped lang="scss">

</style>