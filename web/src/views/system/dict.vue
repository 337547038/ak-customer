<template>
  <div>
    <ak-list
        ref="tableListEl"
        pk="id"
        :columns="columns"
        :api="{ list: 'dictList', edit: 'tableList' }"
        :before="beforeEvent"
        :after="afterEvent"
        :controlBtn="[
        {
          key: 'add',
          click:()=>{
            visible=true;
          }
        },
        { key: 'edit' },
        { key: 'del' }
      ]"
    >
    </ak-list>
    <el-dialog v-model="visible" width="500" title="新增字典">
      <ak-form
          @cancel="cancelClick"
          :after="afterSubmit"
          :data="formData"
          :api="{ add: 'dictSave', edit: 'dictEdit' }"
          v-model="formModel">
      </ak-form>
    </el-dialog>
    <el-dialog v-model="setVisible" width="500" title="设置字典数据">
      <ak-form
          :data="formEditData"
          :api="{ edit: 'dictEdit' }"
          v-model="formEditModel">
        <div v-for="(item,index) in labelList" :key="item.value">
          <ak-field label="标签名称" v-model="item.label" prop="label"/>
          <ak-field label="标签值" v-model="item.field" prop="value"/>
          <el-button @click="delLabelClick(index)"></el-button>
        </div>
        <el-button @click="addLabelClick">添加一行</el-button>
      </ak-form>
    </el-dialog>

  </div>
</template>
<script setup lang="ts">
import {ref} from 'vue'

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
    prop: 'name',
    label: '字典名称'
  },
  {
    prop: 'name2',
    label: '字典标识'
  },
  {
    prop: 'status',
    label: '状态',
    render: 'tag',
    replaceValue: {'1': '启用', '0': '禁用'},
    custom: {'1': 'success', '0': 'danger'},
    search: {
      type: 'select',
      options: [{value: '0', label: '禁用'}]
    }
  },
  {
    prop: 'date',
    label: '更新时间',
    width: 150,
    render: 'date',
    search: false
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
        popConfirm: {confirmButtonType: 'danger'},
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

// 表单相关
const visible = ref(false)
const cancelClick = () => {
  visible.value = false
}
const formModel = ref({status: 1})
const formData = ref([
  {
    prop: 'name',
    label: '字典名称',
    formItem: {
      rules: [
        {
          required: true,
          message: '请输入字典名称',
          trigger: 'blur'
        }
      ]
    },
    attr:{
    }
  },
  {
    prop: 'type',
    label: '字典标识',
    formItem: {
      rules: [
        {
          required: true,
          message: '请输入字典标识',
          trigger: 'blur'
        }
      ]
    }
  },
  {
    type: 'radio',
    prop: 'status',
    label: '状态',
    options: [{label: '禁用', value: 0}, {label: '启用', value: 1}]
  },
  {
    prop: 'remark',
    label: '备注',
    attr: {
      type:'textarea',
      rows: 3
    }
  }
])
const formEditData = ref([])
const formEditModel = ref({})
const labelList = ref([])
const setVisible = ref(false)
const addLabelClick = () => {
  labelList.value.push({label: '', value: ''})
}
const delLabelClick = (index: number) => {
  labelList.value.splice(index, 1)
}
const afterSubmit = () => {
  visible.value = false
}
</script>
<style scoped lang="scss">

</style>