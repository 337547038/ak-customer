<template>
  <el-icon
    class="collapse-icon"
    @click="toolClick('collapse')"
  >
    <Fold v-if="!collapse" />
    <Expand v-else />
  </el-icon>
  <el-breadcrumb
    separator="/"
    class="breadcrumb"
  >
    <el-breadcrumb-item :to="{ path: '/' }">
      首页
    </el-breadcrumb-item>
    <el-breadcrumb-item
      v-for="(item, index) in breadcrumb"
      :key="index"
      :to="{ path: item.path }"
    >
      {{ item.label }}
    </el-breadcrumb-item>
  </el-breadcrumb>
  <div class="comm-header-tool">
    <el-icon
      title="刷新页面"
      @click="toolClick('refresh')"
    >
      <RefreshRight />
    </el-icon>
    <el-icon title="全屏">
      <FullScreen @click="toolClick('fullScreen')" />
    </el-icon>
  </div>
  <div class="header-right">
    <el-dropdown>
      <div
        class="header-avatar"
        style="cursor: pointer"
      >
        <el-avatar
          class="avatar"
          size="small"
          shape="circle"
          :src="userInfo.avatar"
        />
        <span class="name">{{ userInfo.userName }}</span>
        <el-icon class="el-icon--right">
          <arrow-down />
        </el-icon>
      </div>
      <template #dropdown>
        <el-menu class="avatar-menu">
          <el-menu-item
            index="user"
            @click="userClick"
          >
            <el-icon><User /></el-icon>
            <span class="title">个人中心</span>
          </el-menu-item>
          <el-menu-item
            index="setting"
            @click="userClick"
          >
            <el-icon><Setting /></el-icon>
            <span class="title">设置</span>
          </el-menu-item>
          <el-menu-item
            index="logout"
            @click="logout"
          >
            <el-icon><CircleClose /></el-icon>
            <span class="title">退出登录</span>
          </el-menu-item>
        </el-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import { useLayoutStore } from '@/store/layout'
  import {useRouter} from 'vue-router'
  import {ArrowDown, CircleClose, Expand, Fold, FullScreen, RefreshRight, Setting, User} from "@element-plus/icons-vue";
  const router = useRouter()
  withDefaults(
    defineProps<{
      collapse: boolean
    }>(),
    {}
  )
  const store = useLayoutStore()
  const breadcrumb:any = computed(() => {
    return store?.breadcrumb
  })
  const emits = defineEmits<{
    (e: 'click', type: string): void
  }>()

  const userInfo = computed(()=>{
    return store.userInfo
  })
/*  const user = ref({
    name: 'userName',
    avatar: ''
  })*/
  const toolClick = (type: string) => {
    emits('click', type)
  }
  const logout = () => {
    store.logout(router)
  }
  const userClick = () => {
    router.push({path:'/system/userCenter'})
  }
</script>
<style lang="scss">
  .header-avatar {
    display: flex;
    align-items: center;
    .avatar,
    .name {
      align-self: center;
    }
    .avatar {
      margin-right: 8PX;
    }
    .name {
      font-weight: 500;
    }
  }
  .avatar-menu {
    width: 150PX;
    .title {
      margin-left: 10PX;
    }
    li {
      height: 35PX;
      line-height: 35PX;
    }
  }
</style>
