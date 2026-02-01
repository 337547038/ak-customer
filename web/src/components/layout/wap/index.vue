<template>
  <div class="layout-container">
    <van-nav-bar
      fixed
      placeholder
      safe-area-inset-top
      :title="layoutStore.navBarTitle"
      :left-arrow="layoutStore.leftArrow"
      @click-left="onClickLeft"
    >
      <template
        #right
      >
        <div class="icon-group">
          <van-icon
            v-if="layoutStore.rightSearchArrow"
            name="search"
            size="26"
            @click="onClickRight('search')"
          />
          <van-icon
            v-if="layoutStore.rightAddArrow"
            name="add-o"
            size="26"
            @click="onClickRight('add')"
          />
          <van-icon
            name="wap-nav"
            size="28"
            @click="navClick"
          />
        </div>
      </template>
    </van-nav-bar>
    <router-view v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
    <nav-popup
      ref="navPopupRef"
      :menu-list="menuList"
    />
    <Tabbar v-model="active" />
  </div>
  <van-back-top v-if="layoutStore.backTop" />
</template>

<script setup lang="ts">
  import {ref, onMounted, watch, onUnmounted} from "vue";
  import {useLayoutStore} from '@/store/layout'
  import {emitter} from '@/utils/eventBus'
  import NavPopup from './navPopup.vue'
  import Tabbar from './tabbar.vue'
  import menuList from "../menuList"
  import {useRoute,useRouter} from "vue-router";
  import '@/assets/wap/style.scss'

  const route = useRoute();
  const router = useRouter();


  const layoutStore = useLayoutStore()
  const onClickLeft = () => {
    emitter.emit('nav-bar-left-arrow-event', {})
    router.go(-1)
  }
  const onClickRight = (type: string) => {
    emitter.emit(`nav-bar-right-${type}-event`, {})
  }
  const navPopupRef = ref()

  const navClick = () => {
    navPopupRef.value.open()
  }

  const active = ref()
  watch(() => route.path, (val: string) => {
    active.value = -1
    if (val === '/') {
      active.value = 0
    }
    if (val.indexOf("/customer") === 0) {
      active.value = 1
    }
    if (val.indexOf("/contract") === 0) {
      active.value = 2
    }
    if (val.indexOf("/system") === 0) {
      active.value = 3
    }
    menuList.forEach(item => {
      if (item.children?.length) {
        for (const obj of item.children) {
          if (obj.path === val) {
            layoutStore.setNavBarTitle(obj.title)
            break
          }
        }
      }
    })
  }, {immediate: true})
  // 获取系统字典
  const getSystemDict = () => {
    layoutStore.updateSystemDict(false)
  }
  onMounted(() => {
    getSystemDict()
    document.body.classList.add('w-body');
  })
  onUnmounted(()=>{
    document.body.classList.remove('w-body');
  })
</script>

<style scoped lang="scss">
  .icon-group {display: flex;align-items: center;

    i {margin-left: 10PX}
  }
</style>