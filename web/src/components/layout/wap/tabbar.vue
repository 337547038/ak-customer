<template>
  <van-tabbar
    v-model="active"
    :placeholder="true"
    @change="onChange"
  >
    <van-tabbar-item icon="home">
      首页
    </van-tabbar-item>
    <van-tabbar-item icon="customer">
      客户管理
    </van-tabbar-item>
    <van-tabbar-item icon="contract">
      合同管理
    </van-tabbar-item>
    <van-tabbar-item icon="user">
      我的
    </van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
  import {useRouter} from "vue-router";

  const router = useRouter()

  const active = defineModel<number>({default: 0})

  const onChange = (active: number) => {
    let path = ''
    switch (active) {
      case 0:
        path = '/'
        break;
      case 1:
        path = '/customer/list'
        break;
      case 2:
        path = '/contract/contract'
        break;
      case 3:
        path = '/system/userCenter'
        break
    }
    router.push({path: path})
  }
</script>

<style scoped lang="scss">
  :deep(.van-icon-home) {font-family: 'iconfont';

    &:before {content: "\e672";}
  }

  :deep(.van-icon-customer) {font-family: 'iconfont';

    &:before {content: "\e60e";}
  }

  :deep(.van-icon-contract) {font-family: 'iconfont';

    &:before { content: "\e625";}
  }

  :deep(.van-icon-user) {font-family: 'iconfont';

    &:before {content: "\e648";}
  }
</style>