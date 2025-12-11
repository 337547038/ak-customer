<template>
  <w-list
    ref="listRef"
    :api="{get:'roleList',del:'roleDelete'}"
    :before="before"
    :add-icon-visible="true"
    :search-form-data="searchFormData"
    :form-title="isAdd?'新增角色':'编辑角色'"
    @nav-bar-event="navBarEvent"
  >
    <template #item="{data}">
      <van-cell>
        <template #title>
          <span>{{ data.name }}</span>
          <van-tag
            v-if="data.status===0"
            type="danger"
          >
            禁用
          </van-tag>
        </template>
        <template #right-icon>
          <van-button
            icon="records-o"
            type="primary"
            size="small"
            @click="editClick(data)"
          />
          <van-button
            style="margin-left: 5px"
            icon="delete-o"
            type="danger"
            size="small"
            @click="delClick(data.id)"
          />
        </template>
      </van-cell>
    </template>
    <w-form
      ref="formRef"
      :data="formData"
      :submit-url="isAdd?'roleSave':'roleEdit'"
      :before="beforeSubmit"
      :after="afterSubmit"
      @cancel="cancelForm"
    >
      <template #idList>
        <van-field
          label="菜单权限"
        >
          <template #input>
            <el-tree
              ref="treeRef"
              node-key="path"
              :props="{label:'title',value:'path'}"
              :data="treeData"
              show-checkbox
              @check-change="handleCheckChange"
            />
          </template>
        </van-field>
      </template>
    </w-form>
  </w-list>
</template>

<script setup lang="ts">
  import {nextTick, ref} from 'vue'
  import treeData from "@/components/layout/menuList";

  const listRef = ref()
  const formRef = ref()
  const isAdd = ref(true)
  const searchFormData = ref([
    {prop: 'name', label: '角色名称'},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
  ])
  const treeCheck = ref('')
  const before = (params: { [key: string]: string }) => {
    return params
  }
  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }
  const editClick = (obj: { [key: string]: any }) => {
    listRef.value.showForm(true)
    nextTick(() => {
      formRef.value.setValue(obj)
      isAdd.value = false
      nextTick(() => {
        treeRef.value.setCheckedKeys(obj.content?.split(',') || [])
        treeCheck.value = obj.content
      })
    })
  }
  const delClick = (id: number) => {
    listRef.value.deleteById(id)
  }
  const formData = ref([
    {prop: 'name', label: '角色名称', rules: [{required: true, message: '请填写角色名称'}]},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
    {prop: 'idList', render: 'scope'},
    {prop: 'remark', label: '备注'}
  ])
  const beforeSubmit = (obj: { [key: string]: any }) => {
    obj.content = treeCheck.value
    return obj
  }
  const afterSubmit = () => {
    cancelForm(false)
    listRef.value.onRefresh()
  }
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }
  const treeRef = ref()
  const handleCheckChange = () => {
    const val = treeRef.value!.getCheckedKeys(false)
    treeCheck.value = val.join(',')
  }
</script>

<style scoped lang="scss">

</style>