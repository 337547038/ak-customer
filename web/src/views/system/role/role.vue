<template>
  <ak-list
    ref="tableListEl"
    pk="id"
    :columns="columns"
    :api="{ list: 'roleList', del:'roleDelete'}"
    :control-btn="[
      {
        key:'add',
        click:()=>{
          addEditEvent({})
        }
      },
      {key:'del'}]"
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
      :api="{ add: 'roleSave', edit: 'roleEdit', detail:'roleGet' }"
      @cancel="cancelClick"
    >
      <template #idList>
        <el-form-item label="角色权限">
          <el-tree
            ref="treeRef"
            node-key="path"
            :props="{label:'title',value:'path'}"
            :data="treeData"
            show-checkbox
            @check-change="handleCheckChange"
          />
        </el-form-item>
      </template>
    </ak-form>
  </el-dialog>
</template>

<script setup lang="ts">
  import {ref, nextTick} from 'vue';
  import treeData from '@/components/layout/menuList'

  const treeRef = ref()
  const tableListEl = ref()
  const columns = ref([
    {
      prop: 'name',
      label: '角色名称'
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
      width: 150,
      buttons: [
        {
          key: 'edit',
          label: '编辑',
          attr: {
            text: true
          },
          click: (row:any) => {
            addEditEvent(row)
          }
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
  const addEditEvent = (row:any) => {
    visible.value = true
    dictDialogTitle.value = row.name ? `编辑${row.name}角色` : '新增角色'
    formModel.value = row || {}
    nextTick(() => {
      // 这里去掉几个特殊，否则当一级选中时，会选中所有子级
     treeRef.value.setCheckedKeys(row.content?.split(',')||[])
    })
  }

  // form
  const formEl = ref()
  const formModel = ref<any>({})
  const visible = ref(false);
  const dictDialogTitle = ref('新增角色')
  const cancelClick = () => {
    visible.value = false
  }
  const beforeSubmit = (data: any) => {
    return data
  }
  const afterSubmit = () => {
    cancelClick()
    tableListEl.value.getData()
  }

  const handleCheckChange = () => {
    const val = treeRef.value!.getCheckedKeys(false)
    formModel.value.content = val.join(',')
  }
  const formData = ref([
    {
      prop: 'name',
      label: '角色名称',
      formItem: {
        rules: [{
          required: true,
          message: '请输入角色名称',
          trigger: 'blur'
        }]
      }
    },
    {
      label: '状态',
      prop: 'status',
      render: 'radio',
      options: 'status',
      attr: {
        modelValue: 1
      }
    },
    {
      label: '权限菜单',
      prop: 'idList',
      render: 'scope'
    },
    {
      label: '备注',
      prop: 'remark',
      attr: {
        type: 'textarea',
        rows: 4
      }
    }
  ])
</script>

<style scoped lang="scss">

</style>