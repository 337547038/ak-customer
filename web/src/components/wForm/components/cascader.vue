<template>
  <field-popup
    v-model="pickerLabel"
    v-model:show-picker="showPicker"
    :item="item"
    @clear-click="clearClick"
  >
    <van-cascader
      v-bind="item.cascader"
      v-model="cascaderValue"
      :options="options"
      @close="showPicker = false"
      @change="onChange"
      @finish="onFinish"
    >
      <template #title>
        <div class="title">
          <span>请选择{{ item.label }}</span>
          <span
            v-if="confirmVisible"
            class="btn"
            @click="confirmClick"
          >确定</span>
        </div>
      </template>
    </van-cascader>
  </field-popup>
</template>
<script setup lang="ts">
  import {ref, onMounted, computed, watch} from 'vue';
  import {closeToast, showLoadingToast} from 'vant';
  import {onBeforeRouteLeave} from 'vue-router'
  import FieldPopup from './fieldPopup.vue'

  const props = withDefaults(
      defineProps<{
        item: { [key: string]: any }
        lazyLoad?: ((node: { [key: string]: any }, callback: (result: string) => void) => void) | null
      }>(),
      {
        lazyLoad: null
      }
  )
  const clearClick = () => {
    cascaderValue.value = ''
    pickerLabel.value = ''
  }
  const emits = defineEmits<{
    (e: 'update:modelValue', value: any): void
    (e: 'change', value: any): void
  }>()

  const cascaderValue = defineModel<string | number>()
  const pickerLabel = ref()
  const confirmVisible = computed(() => {
    return props.item.cascader?.confirmVisible
  })
  const showPicker = ref(false);
  const options = ref<any>([]);
  const currentValue = ref()
  const currentOptions = ref([])
  const onChange = ({value, selectedOptions}:any) => {
    getLazyLoad(selectedOptions.slice(-1)[0]) // 取最后一项
    // 保存当前选中的值，用于点确定时引用
    currentValue.value = value
    currentOptions.value = selectedOptions
  };
  const onFinish = ({selectedOptions}:any) => {
    showPicker.value = false;
    const key = props.item.cascader?.fieldNames?.text || 'text'
    pickerLabel.value = selectedOptions.map((option:any) => option[key]).join('/');
  }
  const confirmClick = () => {
    emits('change', cascaderValue.value)
    emits('update:modelValue', cascaderValue.value)
    onFinish({selectedOptions: currentOptions.value})
  }
  const getLazyLoad = (node: { [key: string]: any }) => {
    if (props.lazyLoad) {
      showLoadingToast('加载中...');
      props.lazyLoad(node || {}, (data) => {
        if (node?.children) {
          node.children = data
        } else {
          options.value = data
        }
        closeToast();
      })
    }
  }

  // 编辑初始时，先直接显示值
  const unWatch2 = watch(() => cascaderValue.value, (newValue) => {
    if (!pickerLabel.value) {
      pickerLabel.value = newValue;
    }
  }, {immediate: true})
  // 如果设置了options，则初始时从options提取编辑时初始值
  const unWatch = watch(() => props.item.cascader?.options, (opt: { [key: string]: any }[] | string) => {
    if (typeof opt === 'string') {
      pickerLabel.value = opt
    } else if (typeof opt === 'object' && opt) {
      pickerLabel.value = extractNames(opt)
    }
  })
  const extractNames = (data = {}) => {
    let names:any = [];
    const key = props.item.cascader?.fieldNames?.text || 'text'

    function traverse(items:any) {
      items.forEach((item:any) => {
        if (item[key]) {
          names.push(item[key]);
        }
        if (item.children && item.children.length > 0) {
          traverse(item.children);
        }
      });
    }

    traverse(data);
    return names.join('/');
  }
  onBeforeRouteLeave(() => {
    unWatch2()
    unWatch()
  })

  // 根据id从数据里取出完整的路径
  /*const findNamePathByIdIterative = (data, targetId) => {
    const stack = [{items: data, path: ""}];

    while (stack.length > 0) {
      const {items, path} = stack.pop();

      for (const item of items) {
        const newPath = path ? `${path}/${item.name}` : item.name;

        if (item.id === targetId) {
          return newPath;
        }

        if (item.children) {
          stack.push({items: item.children, path: newPath});
        }
      }
    }

    return null;
  }
  const pickerLabel = computed(() => {
    return findNamePathByIdIterative(options.value, cascaderValue.value)
  })*/

  onMounted(() => {
    getLazyLoad({})
  })

</script>


<style scoped lang="scss">
:deep(.van-cascader__title) {
  width: 90%
}

.title {
  display: flex;
  justify-content: space-between
}

.btn {
  font-weight: 400;
  color: #0077aa;
  cursor: pointer;
}
</style>