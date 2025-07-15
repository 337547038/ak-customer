<template>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'userList', del:'userDelete'}"
      :controlBtn="[
          {
            key:'add',
            click:()=>{
              visible=true
              dictDialogTitle='新增用户'
            }
          },
          {key:'del'}]"
  >
  </ak-list>
  <el-dialog v-model="visible" width="800" :title="dictDialogTitle" class="form-dialog">
    <ak-form
        class="flex-form flex-form-2"
        pk="id"
        ref="formEl"
        label-width="120"
        @cancel="cancelClick"
        :after="afterSubmit"
        :data="formData"
        :api="{ add: 'userSave', edit: 'userEdit', detail:'userGet' }"
        v-model="formModel">
    </ak-form>
  </el-dialog>
</template>

<script setup lang="ts">
  import {ref,provide} from 'vue'

  provide('listForm', ref({}))

  const columns = ref([
    {
      type: 'selection'
    },
    {
      label: '序号',
      type: 'index',
      width: 80
    },
    {
      prop: 'userName',
      label: '用户名称'
    },
    {
      prop: 'phone',
      label: '手机'
    },
    {
      prop: 'weixin',
      label: '微信'
    },
    {
      prop: 'sex',
      label: '性别',
      render: 'tag',
      replaceValue: 'sex',
      custom: {'1': 'success', '0': 'danger'},
      search: {
        type: 'select',
        options: 'sex'
      }
    },
    {
      prop: 'status',
      label: '状态',
      render: 'tag',
      replaceValue: 'status',
      custom: {'1': 'success', '0': 'danger'},
      search: {
        type: 'select',
        options: 'status'
      }
    },
    /*    {
          prop: 'leader',
          label: '直属上级',
          search: false
        },*/
    {
      prop: 'loginTimer',
      label: '登录次数',
      search: false
    },
    {
      prop: 'lastLogin',
      label: '最后登录时间',
      width: 180,
      render: 'datetime',
      search: false
    },
    {
      prop: 'ip',
      label: '登录ip',
      width: 180,
      search: false
    },
    {
      prop: 'createTime',
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
      label: '操作',
      render: 'buttons',
      width: 150,
      buttons: [
        {
          key: 'edit',
          label: '编辑',
          attr: {
            text: true
          },
          click: (row) => {
            editEvent(row)
          }
          /*display: (row) => {
            return row.status === 1
          },
          disabled: (row) => {
            return row.status === 1
          }*/
        },
        {
          label: '删除',
          key: 'del',
          attr: {
            text: true
          },
          popConfirm: {confirmButtonType: 'danger'},
          /*display: (row) => {
            return row.isSys === 0
          },
          disabled: (row) => {
            return row.isSys === 1
          }*/
        }
      ]
    }
  ])

  // form
  const dictDialogTitle = ref('新增用户')
  const visible = ref(false)
  const formEl = ref()
  const formModel = ref({})
  const cancelClick = () => {

  }
  const afterSubmit = () => {

  }
  const formData = ref([
    {
      prop: 'userName',
      label: '用户名称'
    },
    {
      prop: 'password',
      label: '登录密码',
      attr:{
        type:'password',
      }
    },
    {
      prop: 'status',
      label: '状态',
      render: 'radio',
      options: 'status',
      attr:{
      }
    },
    {
      prop: 'phone',
      label: '手机号码'
    },
    {
      prop: 'weixin',
      label: '微信号码'
    },
    {
      prop: 'qq',
      label: 'qq号码'
    },
    {
      prop: 'sex',
      label: '性别',
      render: 'radio',
      options: 'sex'
    },
    {
      prop: 'tid',
      label: '直属上级',
      tooltip:'默认为所属部门负责人'
    },
    {
      prop: 'departmentId',
      label: '所属部门'
    },
    {
      prop: 'roleId',
      label: '角色权限'
    },
    {
      prop: 'creatTime',
      label: '创建时间'
    },
    {
      prop: 'updateTime',
      label: '更新时间'
    },
    {
      prop: 'lastLogin',
      label: '最后登录时间'
    },
    {
      prop: 'loginTimer',
      label: '登录次数'
    },
    {
      prop: 'ip',
      label: '最后登录ip'
    },
    {
      prop: 'remark',
      label: '备注'
    }
  ])

</script>

<style scoped lang="scss">

</style>