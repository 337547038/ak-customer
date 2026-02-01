<template>
  <template
    v-for="(item, index) in data as any"
    :key="index"
  >
    <el-sub-menu
      v-if="item.children && subMenuPermission(item.children)"
      :index="item.path || item.menuIndex"
    >
      <template #title>
        <ak-icon :name="item.icon" />
        <span>{{ item.title }}</span>
      </template>
      <menu-item :data="item.children" />
    </el-sub-menu>
    <el-menu-item
      v-else-if="item.type!=='btn'"
      v-permission="item.path"
      :index="item.path || item.menuIndex"
    >
      <ak-icon :name="item.icon" />
      <span>{{ item.title }}</span>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
  import {permission} from "@/directive/permissions";

  withDefaults(
      defineProps<{
        data?: string[]
      }>(),
      {
        data: () => {
          return []
        }
      }
  )

  const subMenuPermission = (child:any) => {
    // 其中有一个子级有权限则返回true
    for (const key in child) {
      if (permission(child[key].path)) {
        return true
      }
    }
    return false
  }
</script>
