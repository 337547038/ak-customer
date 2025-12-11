<template>
  <el-dialog
    v-if="mode==='dialog'"
    v-model="visible"
    width="300"
    title="使用微信扫一扫绑定"
  >
    <div class="login-container">
      <iframe
        :src="wechatSrc"
        frameborder="0"
        scrolling="no"
        style="margin:0 auto;display: block"
        width="210"
        height="280"
      />
    </div>
  </el-dialog>
  <transition
    v-else
    name="slide-up"
  >
    <div
      v-show="visible"
      class="scan-login"
    >
      <div class="login-container">
        <iframe
          :src="wechatSrc"
          frameborder="0"
          scrolling="no"
          style="margin:0 auto;display: block"
          width="210"
          height="280"
        />
        <p class="tips">
          使用微信扫一扫登录
        </p>
      </div>
      <div class="account">
        <el-button
          type="primary"
          text
          @click="accountLogin"
        >
          使用账号密码登录
        </el-button>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
  import {ref} from 'vue'

  const props = withDefaults(
      defineProps<{
        mode?: string
      }>(),
      {
        mode: '' //可选dialog
      }
  )

  const visible = ref(false)
  const open = () => {
    visible.value = true
    getQrcodeImg()
  }
  const accountLogin = () => {
    visible.value = false
  }
  const wechatSrc = ref('')
  const getQrcodeImg = () => {
    const appid = 'wx28c54e66df42d7d1'
    const urlPath = props.mode === 'dialog' ? '/system/userCenter' : '/login'
    const redirect_uri = encodeURIComponent(`https://xxxx.com/#/${urlPath}`)
    // const redirect_uri = 'https%3A%2F%2Fpassport.yhd.com%2Fwechat%2Fcallback.do'
    const state = Math.random().toString(36).substr(2, 10) // 先加个随机数
    let defaultStyle = 'LmltcG93ZXJCb3ggLmluZm97d2lkdGg6YXV0b30KLmltcG93ZXJCb3ggLnN0YXR1c3twYWRkaW5nOjB9Ci5pbXBvd2VyQm94IC50aXRsZSB7ZGlzcGxheTogbm9uZX0KLmltcG93ZXJCb3ggLnFyY29kZSB7d2lkdGg6IDk5JX0KI3d4X2RlZmF1bHRfdGlwe2Rpc3BsYXk6IG5vbmV9Cg=='
    const href = `data:text/css;base64,${defaultStyle}`
    wechatSrc.value = `https://open.weixin.qq.com/connect/qrconnect?appid=${appid}&redirect_uri=${redirect_uri}&response_type=code&scope=snsapi_login&state=${state}&href=${href}`
  }
  defineExpose({open})
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  flex-direction: column;
}

.tips {
  text-align: center;
  color: #666
}

.account {
  display: flex;
  justify-content: center;
  padding-top: 5PX;
}

.scan-login {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  border-radius: 5PX;
  background: #fff;
  z-index: 5;
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-up-enter-from, .slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}
</style>