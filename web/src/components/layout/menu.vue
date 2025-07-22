<template>
  <el-menu
      :collapse="collapse"
      active-text-color="#ffd04b"
      background-color="rgb(48 65 86)"
      text-color="#fff"
      @select="select"
      :default-openeds="['customer']"
  >
    <MenuItem :data="menuList"/>
  </el-menu>
</template>

<script setup lang="ts">
  import MenuItem from './menuItem.vue'
  import {ref, onMounted, watch} from 'vue'
  import {useRouter, useRoute} from 'vue-router'
  import {useLayoutStore} from '@/store/layout'
  import menuList from '@/components/layout/menuList'

  const router = useRouter()
  const route = useRoute()
  const store = useLayoutStore()
  // store.commit('changeBreadcrumb', [{ label: '表单页面' }])

  const activeIndex = ref('3')

  withDefaults(
      defineProps<{
        collapse: boolean
      }>(),
      {}
  )
  const emits = defineEmits<{
    (e: 'getMenuList', val: any): void
  }>()
  const select = (index: string) => {
    router.push({path: index})
  }

  watch(
      () => route.path,
      () => {
        // 根据path从navList里取title，多级时可在当前页面中修改changeBreadcrumb
        menuList.forEach((item) => {
          if (item.path === route.path) {
            store.changeBreadcrumb([{label: item.title}])
          } else if (item.children) {
            for (const key in item.children) {
              const obj = item.children[key]
              if (obj.path === route.path) {
                store.changeBreadcrumb([{label: obj.title}])
                break
              }
            }
          }
        })
      }
  )
  onMounted(() => {
    // 将导航信息传给tagViews，根据path匹配出显示的title
    // emits('getMenuList', navList.value)
  })
</script>
