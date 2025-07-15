<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
    <el-tab-pane label="我的客户" name="first"></el-tab-pane>
    <el-tab-pane label="下属客户" name="second"></el-tab-pane>
    <el-tab-pane label="共享客户" name="third"></el-tab-pane>
    <el-tab-pane label="我的共享" name="fourth"></el-tab-pane>
  </el-tabs>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'customerList',export:'customerExport'}"
      :controlBtn="controlBtn"
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
  <importDialog ref="importDialogRef"/>
</template>
<script setup lang="ts">
  import {ref, markRaw} from 'vue'
  import ProvincesCity from '@/components/provincesCity/index.vue'
  import getCityByCode from '@/utils/getCityByCode'
  import importDialog from './components/import.vue'

  //　tabs
  const activeName = ref('first')
  const handleClick = (name: string) => {
    if (name === 'first') {
      visibleTest.value = true
    } else if (name === 'second') {
      visibleTest.value = false
    }
  }
  // list btn
  const importDialogRef = ref()
  // list
  const tableListEl = ref()
  const controlBtn = ref([
    {
      key: 'add',
      click: () => {
      }
    },
    {
      key: 'export',
      icon:'icon-export'
    },
    {
      key: 'import',
      label: '导入',
      click: () => {
        console.log(importDialogRef.value)
        importDialogRef.value.openDialog(tableListEl.value.getData)
      },
      icon:'icon-import'
    },
    {
      key: 'toUser',
      label: '移交',
      icon:'icon-toUser'
    },
    {
      key: 'toCom',
      label: '转入公海',
      icon:'icon-toCom'
    },
    {
      key: 'share',
      label: '共享客户',
      icon:'icon-share'
    },
    {
      key: 'set',
      label: '批量设置',
      icon:'icon-set'
    },
    {key: 'del'}
  ])
  const columns = ref<any>([
    {
      type: 'selection'
    },
    {
      label: '序号',
      type: 'index',
      width: 80
    },
    {
      prop: 'company',
      label: '客户名称'
    },
    {
      prop: 'userName',
      label: '所属人员',
      width: 90,
      search: false
    },
    {
      prop: 'type',
      label: '合作类型',
      width: 120,
      render: 'tag',
      replaceValue: 'cooperationType',
      search: {
        type: 'select',
        options: 'cooperationType'
      }
    },
    {
      prop: 'area',
      label: '所属区域',
      search: {
        type: 'component',
        component: markRaw(ProvincesCity)
      },
      formatter: (row: any, column: any, cellValue: any) => {
        return getCityByCode(cellValue)
      }
    },
    {
      prop: 'industry',
      label: '行业分类',
      width: 120,
      render: 'tag',
      replaceValue: 'industryType',
      search: {
        type: 'select',
        options: 'industryType'
      }
    },
    {
      prop: 'source',
      label: '来源',
      width: 100,
      render: 'tag',
      replaceValue: 'source',
      search: {
        type: 'select',
        options: 'source'
      }
    },
    {
      prop: 'lastContactTime',
      label: '最近联系时间',
      width: 160,
      render: 'datetime',
      search: false
    },
    {
      prop: 'nextContactTime',
      label: '下次联系时间',
      width: 160,
      render: 'datetime',
      search: false
    },
    {
      prop: 'creatTime',
      label: '创建时间',
      width: 160,
      render: 'datetime',
      search: false
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      width: 160,
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
          icon: '',
          attr: {
            text: true
          },
          click: (row) => {
            visible.value = true
          }
          /*display: (row) => {
            return row.status === 1
          },
          disabled: (row) => {
            return row.status === 1
          }*/
        },
        {
          key: 'follow',
          label: '跟进',
          attr: {
            text: true
          }
        },
        {
          key: 'del',
          label: '删除',
          icon: '',
          attr: {
            text: true
          }
        }
      ]
    }
  ])

  // form
  const formDetailEl = ref()
  const visible = ref(false);
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


</script>
<style scoped lang="scss">

</style>