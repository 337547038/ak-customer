<template>
  <el-alert type="success">
    实现功能：<br />
    1.将常见渲染类型如switch,image,date,tag等进行可配置化，无需要自定义slot<br />
    2.集成条件筛选；<br />
    3.集成分页组件；<br />
    4.集成列表数据接口请求功能；<br />
    5.无需重复复制组件el-table-column；<br />
    6.集成横向滚动条固定在浏览器底部(鼠标移到表格区域可见滚动条固定于底部)；<br />
    7.快速设置表头帮助信息；<br />
    8.常见增删查改按钮封装；<br />
    ...<br />
    无需重复代码，快速实现表格数据渲染。更多使用配置详见
    /components/list/README.md
  </el-alert>
  <div class="common-main">
    <table-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'tableList', edit: 'tableList' }"
      :before="beforeEvent"
      :after="afterEvent"
      :controlBtn="[
        {
          key: 'add'
        },
        { key: 'edit' },
        { key: 'del' },
        { label: '导入' }
      ]"
    >
      <template #name="scope">
        {{ scope.row.name }}
      </template>
      <template #btnAppend>
        <el-button>测试</el-button>
        <el-button>测试2</el-button>
      </template>
    </table-list>
  </div>
</template>
<script setup lang="ts">
  import { ref, onMounted, markRaw } from 'vue'
  import TableList from '@/components/list/index.vue'
  import InputTest from './inputTest.vue'

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
      prop: 'diy',
      label: '自定义筛选',
      show: false,
      search: {
        type: 'component',
        component: markRaw(InputTest)
      }
    },
    {
      prop: 'name',
      label: '昵称',
      search: {
        placeholder: '请输入'
      }
    },
    {
      prop: 'name2',
      label: '列表不显示',
      show: false
    },
    {
      prop: 'sex',
      label: '性别',
      render: 'switch',
      attr: {
        inlinePrompt: true,
        activeValue: 1,
        inactiveValue: 0,
        activeText: '男',
        inactiveText: '女'
      },
      search: {
        type: 'radio',
        options: [{ value: 0, label: '男' }]
      }
    },
    {
      prop: 'src',
      label: '图片地址',
      render: 'image',
      attr: {
        width: '120px',
        height: '90px'
      },
      search: false
    },
    {
      prop: 'date',
      label: 'Date',
      width: 150,
      help: '可快速将接口的各种形式的时间值格式化',
      render: 'date',
      search: {
        type: 'datePicker'
      }
    },
    {
      prop: 'status',
      label: '状态',
      render: 'tag',
      replaceValue: { '1': '启用', '0': '禁用' },
      custom: { '1': 'success', '0': 'danger' },
      search: {
        type: 'select',
        options: [{ value: '0', label: '禁用' }]
      }
    },
    {
      prop: 'type',
      label: '类型'
    },
    {
      label: '操作',
      render: 'buttons',
      buttons: [
        {
          key: 'edit',
          click: (row) => {
            console.log('click')
          }
          /*display: (row) => {
            return row.status === 1
          },
          disabled: (row) => {
            return row.status === 1
          }*/
        },
        {
          label: '设置',
          tooltip: '设置',
          disabled: (row) => {
            return row.status === 1
          }
        },
        {
          key: 'del',
          tooltip: 'del',
          popConfirm: { confirmButtonType: 'danger' },
          display: (row) => {
            return true
          },
          disabled: (row) => {
            return row.status === 1
          },
          click: () => {
            console.log('click')
          }
        }
        /* { label: '其他' }*/
      ]
    }
  ])

  const beforeEvent = (type: string, params: any) => {
    console.log('beforeEvent', type)
    return params
  }
  const afterEvent = (type: string, res: any) => {
    console.log('afterEvent', type)
  }

  const tableListEl = ref()
  onMounted(() => {
    tableListEl.value.setFormValue({ name: '123', diy: 'diy' })
  })
</script>
<style scoped lang="scss"></style>
