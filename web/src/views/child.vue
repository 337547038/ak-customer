<template>
  <div>
    <input v-model="model.name" type="text"/>
    <input v-model="model.id" type="text"/>
  </div>
</template>

<script setup lang="ts">
  const model = defineModel({
    default: () => {
      return {
        name: '1222'
      }
    }
  });
</script>

<style scoped lang="scss">

</style>