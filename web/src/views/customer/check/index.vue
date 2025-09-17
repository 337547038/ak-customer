<template>
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
      :after="afterSubmit"
      :data="formData"
      :hide-filed="['nextTime','lastTime','address','tel','code']"
      :api="{ detail: 'customerDetail'}"
      @cancel="cancelClick"
    />
  </el-drawer>
</template>
<script setup lang="ts">
  import {ref} from 'vue'
  import formData from '../list/formData'

  const formDetailEl = ref()
  const visible = ref(false);
  const cancelClick = () => {
    visible.value = false
  }
  const afterSubmit = () => {

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
          click: (row) => {
            visible.value = true
            if (row.area) {
              row.area = row.area.split(',')
            }
            formModel.value = row
          }
        }
      ]
    }
  ])
</script>
<style scoped lang="scss">

</style>