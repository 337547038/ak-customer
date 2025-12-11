<template>
  <van-field
    v-if="showField"
    v-bind="item.field"
    :model-value="modelValue"
    is-link
    readonly
    :name="item.prop"
    :label="item.label"
    :placeholder="item.placeholder?item.placeholder:`请选择${item.label}`"
    :rules="item.rules as any"
    :required="getRequired(item.rules)"
    @click-input="clickInput"
  >
    <template #right-icon>
      <van-icon
        v-if="modelValue&&item.field?.clearable"
        name="close"
        @click.stop="clearClick"
      />
    </template>
  </van-field>
  <van-popup
    v-model:show="showPicker"
    destroy-on-close
    round
    position="bottom"
    teleport="body"
  >
    <slot />
  </van-popup>
</template>

<script setup lang="ts">
  import {getRequired} from "./rules";

  withDefaults(
      defineProps<{
        modelValue: string | undefined | number
        item: { [key: string]: any }
        showField?: boolean //是否显示field组件，false时不显示可使用showPopup方法打开弹窗
      }>(),
      {
        showField: true
      }
  )
  const emits = defineEmits<{
    (e: 'clickInput'): void
    (e: 'clearClick'): void
  }>()
  const showPicker = defineModel<boolean>('showPicker', {default: false})
  const clickInput = () => {
    showPicker.value = true
    emits("clickInput")
  }
  const clearClick = () => {
    emits("clearClick")
  }
  const showPopup = (visible?: boolean) => {
    showPicker.value = !!visible
  }
  defineExpose({showPopup})
</script>

<style scoped lang="scss">

</style>