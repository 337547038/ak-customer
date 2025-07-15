<template>
  <ak-list
      ref="tableListEl"
      pk="id"
      :autoLoad="false"
      :columns="columns"
      :api="{ list: 'customerCheck'}"
  >
  </ak-list>
  <el-drawer v-model="visible" size="75%" title="详情">
    <ak-form
        ref="formDetailEl"
        :btnText="false"
        :formProps="{disabled:true,inline:true}"
        @cancel="cancelClick"
        :after="afterSubmit"
        :data="formData"
        :api="{ detail: 'customerDetail'}"
        v-model="formModel">
    </ak-form>
  </el-drawer>
</template>
<script setup lang="ts">
import {ref} from 'vue'

const formDetailEl=ref()
const visible=ref(false);
const cancelClick = () => {
  visible.value = false
}
const afterSubmit = () => {

}
const formModel = ref({})
const formData = ref([
  {
    prop: 'company',
    label: '客户名称',
  },
  {
    prop: 'brandName',
    label: '品牌名称',
  },
  {
    prop: 'industry',
    label: '行业分类',
  },
  {
    prop: 'source',
    label: '客户来源',
  },
  {
    prop: 'area',
    label: '所属区域',
  },
  {
    prop: 'createTime',
    label: '创建时间',
  },
  {
    prop: 'updateTime',
    label: '更新时间',
  },
])

const columns = ref<any>([
  {
    prop: 'keywords',
    label: '查询关键词',
    search: {
      placeholder: '请输入客户名称，品牌名称，客户电话，社会信用代码关键字',
      style:{width: '500px'},
    },
    show: false
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
    prop:'operation',
    label: '操作',
    render: 'buttons',
    width: 150,
    search: false,
    buttons: [
      {
        key: 'detail',
        label:'详情',
        click: (row) => {
          visible.value = true
        }
        /*display: (row) => {
          return row.status === 1
        },
        disabled: (row) => {
          return row.status === 1
        }*/
      }
    ]
  }
])
</script>
<style scoped lang="scss">

</style>