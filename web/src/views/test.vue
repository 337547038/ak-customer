<script setup lang="ts">

import {ref} from "vue";
import {getRequest} from '@/api'

const click=()=>{

  getRequest("roleGetByIds", {id:'2,1'})
      .then((res: any) => {

        console.log('then')
      })
      .catch((err:any) => {
        console.log('catch')
      })
}
</script>

<template>
  <el-button @click="click">click</el-button>
</template>

<style scoped lang="scss">

</style>