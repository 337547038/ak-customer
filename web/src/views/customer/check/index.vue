<template>
  <div v-if="!isMobile()">
    <ak-list
      ref="tableListEl"
      pk="id"
      :auto-load="false"
      :columns="columns"
      :columns-icon-visible="false"
      :search-icon-visible="false"
      :api="{ list: 'customerCheck'}"
    />
    <el-drawer
      v-model="visible"
      size="75%"
      title="详情"
    >
      <ak-form
        ref="formDetailEl"
        v-model="formModel"
        :disabled="true"
        label-width="100"
        class="flex-form flex-form-2"
        :btn-text="false"
        :data="formData"
        :hide-filed="hideField"
        :api="{ detail: 'customerDetail'}"
        @cancel="cancelClick"
      />
    </el-drawer>
  </div>
  <div v-else>
    <van-search
      v-model="keyword"
      show-action
      placeholder="请输入客户名称，品牌名称，客户电话，社会信用代码关键字"
    >
      <template #action>
        <div @click="onClickButton">
          搜索
        </div>
      </template>
    </van-search>
    <w-list
      ref="listRef"
      :api="{get:'customerCheck'}"
      :list-props="{immediateCheck:false}"
      :before="beforeGet"
      form-title="详情"
    >
      <template #item="{data}">
        <van-cell
          is-link
          class="cell"
          :title="data.company"
          :value="data.userName"
          :label="`创建:${dateFormatting(data.creatTime)} 更新:${dateFormatting(data.updateTime)}`"
          @click="cellClick(data)"
        />
      </template>
      <w-form
        ref="formRef"
        :confirm-text="false"
        cancel-text="关闭"
        :data="formDataWap"
        :disabled="true"
        @cancel="cancelForm"
      />
    </w-list>
  </div>
</template>
<script setup lang="ts">
  import {computed, ref, nextTick} from 'vue'
  import formData from '../list/formData'
  import {isMobile, dateFormatting} from "@/utils";

  const formDetailEl = ref()
  const hideField = ['nextTime', 'lastTime', 'address', 'tel', 'code', 'files']
  const visible = ref(false);
  const cancelClick = () => {
    visible.value = false
  }
  const formatTime = (obj:{[key:string]: string}) => {
    if (obj.creatTime) {
      obj.creatTime = dateFormatting(obj.creatTime)
    }
    if (obj.updateTime) {
      obj.updateTime = dateFormatting(obj.updateTime)
    }
    return obj;
  }
  const formModel = ref({})

  const columns = ref<any>([
    {
      prop: 'keywords',
      label: '查询关键词',
      search: {
        placeholder: '请输入客户名称，品牌名称，客户电话，社会信用代码关键字',
        style: {width: '500px'},
      },
      visible: false
    },
    {
      label: '序号',
      type: 'index',
      width: 80
    },
    {
      prop: 'company',
      label: '客户名称',
      search: false
    },
    {
      prop: 'userName',
      label: '所属人员',
      width: 150,
      search: false
    },
    {
      prop: 'creatTime',
      label: '创建时间',
      width: 180,
      render: 'datetime',
      search: false
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      width: 180,
      render: 'datetime',
      search: false
    },
    {
      prop: 'operation',
      label: '操作',
      render: 'buttons',
      width: 150,
      search: false,
      buttons: [
        {
          key: 'detail',
          label: '详情',
          click: (row:any) => {
            visible.value = true
            if (row.area) {
              row.area = row.area.split(',')
            }
            formModel.value = formatTime(row)
          }
        }
      ]
    }
  ])

  // wap
  const listRef = ref()
  const keyword = ref()
  const formRef = ref()
  const formDataWap = computed(() => {
    return formData.filter(item=>!hideField.includes(item.prop)).map(item=>{
      const newItem:any = Object.assign({}, item);
      if (newItem.formItem) {
        delete newItem.formItem
      }
      if (newItem.attr) {
        delete newItem.attr
      }
      if (newItem.visible) {
        delete newItem.visible
      }
      if (newItem.render === 'select') {
        newItem.render = 'picker'
      }
      return newItem;
    })
  })
  const onClickButton = () => {
    listRef.value.onRefresh()
  }
  const beforeGet = (params: { [key: string]: any }) => {
    params.keywords = keyword.value
    return params
  }
  const cellClick = (obj: { [key: string]: any }) => {
    listRef.value.showForm(true)
    nextTick(() => {
      formRef.value.setValue(formatTime(obj))
    })
  }
  const cancelForm = () => {
    listRef.value.showForm(false)
  }
</script>
<style scoped lang="scss">
.cell :deep(.van-cell__title) {
  flex: 2
}

.cell :deep(.van-cell__value) {
  flex: none
}
</style>