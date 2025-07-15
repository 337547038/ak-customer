<template>
  <el-menu
      :collapse="collapse"
      active-text-color="#ffd04b"
      background-color="rgb(48 65 86)"
      text-color="#fff"
      @select="select"
      :default-openeds="['customer']"
  >
    <MenuItem :data="navList"/>
  </el-menu>
</template>

<script setup lang="ts">
  import MenuItem from './menuItem.vue'
  import {ref, onMounted, watch} from 'vue'
  import {useRouter, useRoute} from 'vue-router'
  import {useLayoutStore} from '@/store/layout'

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
  const navList = ref([
    {
      title: '首页',
      path: '/',
      icon: 'HomeFilled'
    },
    {
      title: '客户管理',
      icon: 'user',
      path: "customer",
      children: [
        {
          title: '客户查重',
          path: '/customer/check',
          icon: "Search"
        },
        {
          title: '客户列表',
          path: '/customer/list',
          icon: "List"
        },
        {
          title: '公海客户',
          path: '/customer/list-comm',
          icon: "Baseball"
        },
        {
          title: '无效客户',
          path: '/customer/list-invalid',
          icon: "Filter"
        },
        {
          title: '跟进记录',
          path: '/customer/follow',
          icon: "Tickets"
        },
        {
          title: '名片识别',
          path: '/customer/ocr',
          icon: "Aim"
        }
      ]
    },
    {
      title: '系统管理',
      icon: 'setting',
      path: "system",
      children: [
        {
          title: '用户管理',
          path: '/system/user',
          icon: "UserFilled"
        },
        {
          title: '角色管理',
          path: '/system/role',
          icon: "CircleCheck"
        },
        {
          title: '部门管理',
          path: '/system/dept',
          icon: "OfficeBuilding"
        },
        {
          title: '登录日志',
          path: '/system/log',
          icon: "Notebook"
        },
        {
          title: '字典管理',
          path: '/system/dict',
          icon: "Memo"
        }
      ]
    }
  ])
  const select = (index: string) => {
    router.push({path: index})
  }

  watch(
      () => route.path,
      () => {
        // 根据path从navList里取title，多级时可在当前页面中修改changeBreadcrumb
        navList.value.forEach((item) => {
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
