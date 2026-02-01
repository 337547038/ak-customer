<template>
  <w-list
    ref="listRef"
    :api="{get:'deptList',del:'deptDel'}"
    :before="before"
    :add-icon-visible="true"
    :search-form-data="searchFormData"
    :form-title="isAdd?'新增部门':'编辑部门'"
    @nav-bar-event="navBarEvent"
  >
    <template #item="{data}">
      <van-swipe-cell>
        <van-cell :clickable="!!data.hasChildren">
          <template #title>
            <span :style="{paddingLeft:data.level*15+'px'}">{{ data.name }}</span>
            <span v-if="data.userName"> ({{ data.userName }})</span>
            <van-tag
              v-if="data.status===0"
              type="danger"
            >
              禁用
            </van-tag>
          </template>
          <template #right-icon>
            <van-icon
              v-if="!!data.hasChildren"
              name="arrow"
              @click="cellClick(data)"
            />
          </template>
        </van-cell>
        <template #right>
          <van-button
            square
            type="primary"
            icon="add-o"
            @click="addBtnClick(data)"
          />
          <van-button
            square
            icon="records-o"
            @click="editBtnClick(data)"
          />
          <van-button
            square
            icon="delete-o"
            type="danger"
            @click="delBtnClick(data.id)"
          />
        </template>
      </van-swipe-cell>
    </template>
    <w-form
      ref="formRef"
      :data="formData"
      :submit-url="isAdd?'deptSave':'deptEdit'"
      :before="beforeSubmit"
      :after="afterSubmit"
      @cancel="cancelForm"
    />
  </w-list>
</template>

<script setup lang="ts">
  import {markRaw, nextTick, ref} from 'vue';
  import userSelect from '@/components/userDialog/index.vue'

  const listRef = ref()
  const formRef = ref()
  const before = (params: { [key: string]: any }) => {
    // name有值时为条件查询状态
    if (!params.tid && !params.name) {
      params.tid = 0
    }
    return params
  }

  const cellClick = (obj: { [key: string]: any }) => {
    listRef.value.setTreeData(obj)
  }

  const searchFormData = ref([
    {prop: 'name', label: '部门名称'},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
  ])

  // 表单
  const isAdd = ref(true)
  const tid = ref(0)
  const formData = ref([
    {prop: 'tidName', label: '上级部门', field: {disabled: true}, modelValue: '顶级'},
    {prop: 'name', label: '部门名称', rules: [{required: true, message: '请填写部门名称'}]},
    {prop: 'userId', label: '负责人', render: 'component', component: markRaw(userSelect)},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
  ])
  const beforeSubmit = (params: { [key: string]: any }) => {
    params.tid = tid.value
    delete params.tidName
    return params
  }
  const afterSubmit = () => {
    cancelForm(false)
    listRef.value.onRefresh()
  }
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }

  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value  = true
    }
  }
  const addBtnClick = (obj: { [key: string]: any }) => {
    listRef.value.showForm(true)
    nextTick(() => {
      formRef.value.setValue({tidName: obj.name})
      tid.value = obj.id
      isAdd.value = true
    })
  }
  const editBtnClick = (obj: { [key: string]: any }) => {
    listRef.value.showForm(true)
    nextTick(() => {
      formRef.value.setValue({...obj, tidName: obj.name})
      tid.value = obj.tid
      isAdd.value = false
    })
  }
  const delBtnClick = (id: number) => {
    listRef.value.deleteById(id)
  }
</script>

<style scoped lang="scss">

</style>