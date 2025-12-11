<template>
  <div class="list-container">
    <van-popup
      v-if="searchFormData?.length"
      v-model:show="showSearchPopup"
      position="right"
      :style="{ width: '88%', height: '100%' }"
    >
      <search-form
        ref="searchFormRef"
        :data="searchFormData"
        confirm-text="查询"
        cancel-text="重置"
        @submit="searchSubmit"
        @cancel="searchCancel"
        @field-value-change="fieldValueChange"
      />
    </van-popup>
    <van-popup
      v-model:show="showAddForm"
      position="right"
      :destroy-on-close="true"
      :style="{ width: '88%', height: '100%' }"
    >
      <div class="form-title">
        {{ formTitle }}
        <van-icon
          name="cross"
          @click="showAddForm=false"
        />
      </div>
      <slot />
    </van-popup>
    <van-pull-refresh
      v-model="refreshing"
      @refresh="onRefresh"
    >
      <van-list
        v-bind="listProps"
        v-model:loading="loading"
        :finished="finished"
        :finished-text="finishedText"
        :class="listCls"
        @load="loadMore"
      >
        <template v-if="list.length">
          <template
            v-for="(item, index) in list"
            :key="item.id||index"
          >
            <slot
              name="item"
              :data="item as any"
              :index="index"
            />
          </template>
        </template>
        <div
          v-else-if="list.length === 0 && !loading"
        >
          <van-empty description="暂无数据" />
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
  import {ref, onMounted, onUnmounted, computed} from "vue";
  import SearchForm from "@/components/wForm/index.vue";
  import {emitter} from "@/utils/eventBus"
  import {getRequest} from "@/api";
  import {jsonParseStringify} from "@/utils";
  import {type ListProps, showFailToast, showSuccessToast} from 'vant';
  import type {FormData} from "@/components/wForm/types.ts";
  import {useLayoutStore} from '@/store/layout'

  const props = withDefaults(
      defineProps<{
        addIconVisible?: boolean // 项部右侧显示添加icon
        formTitle?: string //添加或编辑表单标题
        api?: { get?: string, del?: string }
        params?: { [key: string]: any }
        before?: ((params: any) => boolean) | null
        after?: ((res: any, isSuccess?: boolean) => any) | null
        finishedText?: string
        listCls?: string // 列表类名
        searchFormData?: FormData[]
        listProps?:ListProps|null
      }>(),
      {
        addIconVisible: false,
        formTitle: '',
        api: () => {
          return {get: '', del: ''}
        },
        params: () => {
          return {}
        },
        before: null,
        after: null,
        finishedText: '没有更多数据了',
        listCls: '',
        searchFormData: () => {
          return []
        },
        listProps:null
      }
  )

  const emits = defineEmits<{
    (e: 'navBarEvent', type: string): void
    (e: 'searchFormChange', key: string, value: any, model: { [key: string]: any }): void
  }>()
  const layoutStore = useLayoutStore()
  const showSearchPopup = ref(false);
  const searchFormRef = ref()

  // 查询表单的值
  const searchFormValue = computed(() => {
    return searchFormRef.value?.getValue()
  })
  const searchSubmit = () => {
    onRefresh()
    showSearchPopup.value = false
  }
  const searchCancel = () => {
    showSearchPopup.value = false
  }
  const fieldValueChange = (key: string, value: any, model: { [key: string]: any }) => {
    emits('searchFormChange',key, value, model)
  }

  const pageSize = ref(20)
  const current = ref(1)
  const list = ref<any>([])
  const refreshing = ref(false)
  const loading = ref(false)
  const finished = ref(false)
  const loadMore = () => {
    if (!props.api?.get) {
      loading.value = false
      finished.value = true
      return
    }
    if (refreshing.value) {
      list.value = [];
      refreshing.value = false;
    }
    let params: any = {
      extend: {
        pageSize: pageSize.value,
        pageNum: current.value
      },
      ...props.params,
      ...searchFormValue.value
    }
    if (typeof props.before === "function") {
      params = props.before(jsonParseStringify(params)) ?? params
    }
    if (params === false) {
      return;
    }
    getRequest(props.api.get, params)
        .then((res: any) => {
          let data = res.data
          if (typeof props.after === "function") {
            data = props.after(data, true) ?? data
          }
          const total = data.total || 0
          const resultData = data.list || data || []
          list.value = [...list.value, ...resultData]
          loading.value = false
          if (total <= list.value.length) {
            // 没有下一页了
            finished.value = true
          } else {
            // 有下一页
            finished.value = false
            current.value = current.value + 1
          }

        })
        .catch(e => {
          loading.value = false
          finished.value = true
          let data: any = null
          if (typeof props.after === "function") {
            data = props.after(e, false)
          }
          // 返回false阻止这里提示
          if (data != false) {
            showFailToast(e.msg)
          }
        })
  }
  const onRefresh = () => {
    // 清空列表数据
    finished.value = false;
    // 重新加载数据
    // 将 loading 设置为 true，表示处于加载状态
    loading.value = true;
    list.value = []
    current.value = 1
    loadMore();
  }

  const deleteById = (id: number) => {
    return new Promise((resolve, reject) => {
      if (props.api?.del && id) {
        getRequest(props.api.del, {id: id})
            .then((res: any) => {
              showSuccessToast('删除成功')
              onRefresh() // 刷新数据
              resolve(res)
            })
            .catch(e => {
              showFailToast('删除失败')
              reject(e)
            })
      } else {
        showFailToast('请设置api.del接口地址')
        reject()
      }
    })
  }

  const showAddForm = ref(false);

  // 显示关闭添加编辑表单,默认false
  const showForm = (visible?: boolean) => {
    showAddForm.value = visible || false
  }

  /**
   * 通过对list设置数据，实现树异步加载．也可使用此方法直接设置列表数据，此时tid为空
   * @param obj 当前行数据
   */
  const setTreeData = (obj: { [key: string]: any }) => {
    if (!obj.id || obj.load || !props.api.get) {
      return false
    }
    let params: any = {
      tid: obj.id
    }
    if (typeof props.before === "function") {
      params = props.before(jsonParseStringify(params)) ?? params
    }
    if (params === false) {
      return;
    }
    getRequest(props.api.get, params)
        .then((res: any) => {
          let data = res.data
          if (typeof props.after === "function") {
            data = props.after(data, true) ?? data
          }
          const resultData = data.list || data || []
          //给 resultData 的所有项添加 level: 1
          const newData = resultData.map((item:any) => ({
            ...item,       // 保留原有属性
            level: (obj.level || 0) + 1        // 用于表示层级
          }));
          const index = list.value.findIndex((item: any) => item.id === obj.id)
          list.value[index].load = true // 当前项添加已加载标识
          if (index >= 0) {
            list.value.splice(index + 1, 0, ...newData)
          }
        })
  }

  onMounted(() => {
    // 显示条件筛选icon
    if (props.searchFormData?.length) {
      layoutStore.setRightSearchArrow(true)
      emitter.on('nav-bar-right-search-event', () => {
        showSearchPopup.value = true
        emits('navBarEvent','search')
      })
    }
    // 显示添加icon
    if (props.addIconVisible) {
      layoutStore.setRightAddArrow(true)
      emitter.on('nav-bar-right-add-event', () => {
        showAddForm.value = true
        emits('navBarEvent','add')
      })
    }

  })

  onUnmounted(() => {
    if (props.searchFormData?.length) {
      emitter.off('nav-bar-right-search-event')
    }
    if (props.addIconVisible) {
      emitter.off('nav-bar-right-add-event')
    }
    // 恢复默认设置
    layoutStore.setRightSearchArrow(false)
    layoutStore.setRightAddArrow(false)
  })

  defineExpose({deleteById, onRefresh, showForm, setTreeData})
</script>

<style scoped>
.form-title {
  font-size: 28px;
  font-weight: 400;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between
}
</style>