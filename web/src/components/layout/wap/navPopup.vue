<template>
  <van-popup
    v-model:show="showNav"
    position="right"
    :style="{ width: '88%', height: '100%' }"
  >
    <div class="nav-list">
      <div
        v-for="(item,index) in menuList"
        :key="index"
        class="nav-group"
      >
        <div
          v-if="item.children && subMenuPermission(item.children)"
          class="nav-list-item"
        >
          <h3><i :class="item.icon" /> {{ item.title }}</h3>
          <template
            v-for="child in item.children"
            :key="child.path"
          >
            <van-cell
              v-if="child.type!=='btn'"
              v-permission="child.path"
              @click="navItemClick(child.path)"
            >
              <template #title>
                <i
                  :class="child.icon"
                  style="margin-right: 2px"
                />
                <span>{{ child.title }}</span>
              </template>
            </van-cell>
          </template>
        </div>
      </div>
    </div>
    <div style="display: flex;justify-content: center;padding-top: 30px">
      <van-button
        size="small"
        @click="closeClick"
      >
        关闭
      </van-button>
    </div>
  </van-popup>
</template>

<script setup lang="ts">
  import {ref} from 'vue'
  import {permission} from "@/directive/permissions"
  import {useRouter} from "vue-router";

  withDefaults(
      defineProps<{
        menuList: any
      }>(),
      {}
  )

  const router = useRouter();
  const showNav = ref(false)
  const open = () => {
    showNav.value = true
  }
  const subMenuPermission = (child: any) => {
    // 其中有一个子级有权限则返回true
    for (const key in child) {
      if (permission(child[key].path)) {
        return true
      }
    }
    return false
  }

  const navItemClick = (path: string) => {
    router.push({path: path})
    closeClick()
  }
  const closeClick = () => {
    showNav.value = false
  }
  defineExpose({open})
</script>

<style scoped lang="scss">
</style>