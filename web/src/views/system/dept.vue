<template>
  <ak-list
    ref="tableListEl"
    pk="id"
    :columns="columns"
    :api="{ list: 'deptList', del:'deptDel'}"
    :control-btn="[{key:'add',click:()=>{openAddEditDialog(true)}}]"
    :before="beforeGetList"
    :table-prop="{rowKey:'id',load:lazyLoad,lazy:true,
                  treeProps:{ children: 'children', hasChildren: 'hasChildren' }}"
  />
  <el-dialog
    v-model="visible"
    width="500"
    :title="dictDialogTitle"
    class="form-dialog"
  >
    <ak-form
      ref="formEl"
      v-model="formModel"
      label-width="100px"
      pk="id"
      :before="beforeSubmit"
      :after="afterSubmit"
      :data="formData"
      :api="{ add: 'deptSave', edit: 'deptEdit', detail:'deptGet' }"
      @cancel="cancelClick"
    />
  </el-dialog>
</template>

<script setup lang="ts">
  import {ref, markRaw, nextTick} from 'vue'
  import {getRequest} from "@/api";
  import userSelect from '@/components/userDialog/index.vue'

  const tableListEl = ref()
  const columns = ref([
    {
      prop: 'name',
      label: '部门名称',
    },
    {
      prop: 'userName',
      label: '负责人',
      search: false
    },
    {
      prop: 'status',
      label: '状态',
      render: 'tag',
      replaceValue: 'status',
      custom: {'1': 'success', '0': 'danger'},
      search: {
        render: 'select',
        options: 'status'
      }
    },
    {
      label: '操作',
      render: 'buttons',
      buttons: [
        {
          key: 'add',
          label: '新增',
          icon: 'plus',
          click: (row) => {
            openAddEditDialog(true, row)
          }
        },
        {
          key: 'edit',
          label: '编辑',
          click: (row) => {
            openAddEditDialog(false, row)
          }
        },
        {
          label: '删除',
          key: 'del',
          tooltip: 'del',
          popConfirm: {confirmButtonType: 'danger'},
          /*display: (row) => {
            return row.isSys === 0
          },*/
          disabled: (row) => {
            return row.hasChildren === 1
          }
        }
      ]
    }
  ])

  const lazyLoad = (row: any, treeNode: any, resolve: any) => {
    getRequest('deptList', {tid: row.id}).then((res) => {
      resolve(res.data?.list || [])
    })
  }

  const beforeGetList = (type: string, data: any) => {
    if (type === 'get' && !data.tid) {
      data.tid = 0
    }
    return data
  }
  // form
  const formEl = ref()
  const visible = ref(false);
  const dictDialogTitle = ref('新增部门')
  const formModel = ref({status: 1})
  const openAddEditDialog = (add: boolean, row: any) => {
    visible.value = true
    if (add) {
      // 只传个tid即可
      formModel.value.tid = row?.id || ''
      dictDialogTitle.value = '新增部门'
    } else {
      if (row?.tid === 0) {
        row.tid = ''
      }
      nextTick(() => {
        formModel.value = row || {}
      })
      dictDialogTitle.value = '修改部门'
    }
  }
  const cancelClick = () => {
    visible.value = false
  }
  const afterSubmit = () => {
    cancelClick()
    tableListEl.value.getData()

  }
  const beforeSubmit = (data: any) => {
    if (data?.tid) {
      data.tid = 0
    }
    return data
  }
  const lazyLoadSelect = (node, resolve) => {
    getRequest('deptList', {tid: node.data.id || 0}).then((res) => {
      const list = res.data?.list || []
      list.forEach(item => {
        item.hasChildren = !item.hasChildren
      })
      resolve(list)
    })
  }
  const formData = ref([
    {
      prop: 'tid',
      label: '上级部门',
      render: 'tree-select',
      attr: {
        placeholder: '请选择上级部门(不选则为顶级)',
        load: lazyLoadSelect,
        lazy: true,
        checkStrictly: true,
        props: {children: 'children', label: 'name', value: 'id', isLeaf: 'hasChildren'}
      }
    },
    {
      label: '部门名称',
      prop: 'name',
      formItem: {
        rules: [{
          required: true,
          message: '请输入部门名称',
          trigger: 'blur'
        }]
      }
    },
    {
      label: '负责人',
      prop: 'userId',
      render: 'component',
      component: markRaw(userSelect),
    },
    {
      label: '状态',
      prop: 'status',
      render: 'radio',
      options: 'status'
    }
  ])

</script>

<style scoped lang="scss">

</style>