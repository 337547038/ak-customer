<script setup lang="ts">

  import {ref, unref, onMounted, computed} from "vue";
  import {getRequest} from '@/api'
  import columns, {columnUser} from "@/views/customer/list/columns";

  const click=()=>{
    getRequest("contract/list",{
      customerId:6
    })
   //getRequest("customerScanCard",{"name":"1","company":"广州传媒有限公司","phone":"3","position":"4","email":"5"})
  }


  const columns2 = ref(columns)
  const activeName = ref()

  const columnsFilter = computed(() => {
    if (activeName.value === 'child') {
      columns2.value.splice(3, 0, columnUser)
    }
    return columns2.value
  })
/*  const click = () => {
    activeName.value = 'child'
    console.log(activeName.value)
    console.log(columnsFilter.value.length)
  }*/

</script>

<template>
  <div>
    <el-button @click="click">click</el-button>
    <!--    <ak-form :data="data" v-model="model"></ak-form>-->
    <div v-for="(item,index) in columnsFilter" :key="item.prop+index.toString()">{{ item.label }}</div>
  </div>

</template>

<style scoped lang="scss">

</style>