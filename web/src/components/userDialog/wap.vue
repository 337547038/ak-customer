<template>
  <field-popup
    v-model:show-picker="showPicker"
    :model-value="showValue"
    :item="data"
    :show-field="showField"
  >
    <h3 class="h3">
      请选择{{ data.label }}
      <van-icon
        name="cross"
        size="18px"
        @click="showPicker=false"
      />
    </h3>
    <van-search
      v-model="keyword"
      show-action
      placeholder="请输入搜索关键词"
    >
      <template #action>
        <div @click="onClickButton">
          搜索
        </div>
      </template>
    </van-search>
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <van-checkbox-group v-model="checkboxChecked">
        <van-cell
          v-for="item in list"
          :key="item.id"
          :style="{color:item.id===modelValue?'red':''}"
          :title="item.userName"
          @click="cellClick(item)"
        >
          <template
            v-if="isMultiple"
            #right-icon
          >
            <van-checkbox
              :name="item.id+''"
              @click.stop
            />
          </template>
        </van-cell>
      </van-checkbox-group>
    </van-list>
    <div
      v-if="isMultiple"
      class="btn"
    >
      <van-button
        type="warning"
        size="small"
        @click="confirmClick('all')"
      >
        所有用户({{ total }})
      </van-button>
      <van-button
        type="primary"
        size="small"
        @click="confirmClick"
      >
        确定({{ checkboxChecked.length }})
      </van-button>
    </div>
  </field-popup>
</template>

<script setup lang="ts">
  import {computed, ref, watch} from "vue";
  import {getRequest} from "@/api";
  import {onBeforeRouteLeave} from 'vue-router'
  import FieldPopup from '@/components/wForm/components/fieldPopup.vue'

  const props = withDefaults(
      defineProps<{
        modelValue: string | undefined | number
        multiple?: boolean
        data: { [key: string]: any }
        showField?: boolean
      }>(),
      {
        multiple: false,
        showField: true
      }
  )
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'selectClick', val: any): void
  }>()
  const isMultiple = computed(() => {
    return props.multiple || props.data.props?.multiple
  })
  const total = ref(0)
  const showValue = ref()
  const loading = ref(false)
  const list = ref<any>([])
  const finished = ref(false)
  const showPicker = ref(false);
  const pageSize = ref(20);
  const current = ref(1);
  const keyword = ref()
  const checkboxChecked = ref<string[]>([])
  const onLoad = () => {
    const params: any = {
      extend: {
        pageSize: pageSize.value,
        pageNum: current.value
      },
      userName: keyword.value
    }
    getRequest('userList', params)
        .then(res => {
          const totalNum = res.data?.total || 0
          const resultData = res.data?.list || res.data || []
          list.value = [...list.value, ...resultData]
          loading.value = false
          if (totalNum <= list.value.length) {
            // 没有下一页了
            finished.value = true
          } else {
            // 有下一页
            finished.value = false
            current.value = current.value + 1
          }
          total.value = totalNum
        })
  }
  const onClickButton = () => {
    current.value = 1
    list.value = []
    onLoad()
  }
  const cellClick = (obj: any) => {
    if (!isMultiple.value) {
      // 单选时
      showValue.value = obj.userName
      emits('update:modelValue', obj.id)
      showPicker.value = false
      emits('selectClick', obj)
    }
  }
  const confirmClick = (type?: string) => {
    emits('update:modelValue', type === 'all' ? '0' : checkboxChecked.value.join(','))
    showPicker.value = false
    const temp:any = []
    list.value.forEach((item:any) => {
      if (checkboxChecked.value.includes(item.id+'')) {
        temp.push(item.userName)
      }
    })
    showValue.value = temp.join(',')
    emits('selectClick', type === 'all' ? '0' : checkboxChecked.value)
  }

  // 用于获取回显数据
  const getShowData = () => {
    getRequest('userListByIds', {ids: props.modelValue + ''})
        .then(res => {
          showValue.value = res.data?.map((item:any) => item.userName).join(',')
        })
    if (isMultiple.value && props.modelValue) {
      checkboxChecked.value = (props.modelValue + '').split(',')
    }
  }

  const unWatch = watch(() => props.modelValue, () => {
    if (!list.value.length && props.modelValue) {
      // 获取回显
      getShowData()
    }
  })

  const showPopup = (visible = true) => {
    showPicker.value = visible
  }

  onBeforeRouteLeave(() => {
    unWatch()
  })
  defineExpose({showPopup})
</script>

<style scoped lang="scss">
.btn {
  text-align: center;
  padding: 20px;
  font-size: 24px;
  border-top: 10px solid #e4e4e4;
  display: flex;
  justify-content: center;

  button {
    margin: 0 10px
  }
}

.h3 {
  text-align: center;
  padding: 15px 0;
  position: relative;
  font-size: 15PX;

  i {
    position: absolute;
    right: 20px
  }
}
</style>