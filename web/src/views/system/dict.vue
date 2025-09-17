<template>
  <div>
    <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'dictList', del:'dictDelete'}"
      :before="beforeEvent"
      :after="afterEvent"
      :control-btn="controlBtn"
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
        :after="afterSubmit"
        :data="formData"
        :api="{ add: 'dictSave', edit: 'dictEdit', detail:'dictGet' }"
        @cancel="cancelClick"
      />
    </el-dialog>
    <el-dialog
      v-model="setVisible"
      width="500"
      title="设置字典数据"
    >
      <ak-form
        v-model="formEditModel"
        pk="id"
        :before="setFormBefore"
        :after="setFormAfter"
        :data="formEditData"
        :api="{ edit: 'dictEdit' }"
        @cancel="cancelSetClick"
      >
        <el-form-item label="字典名称:">
          <div>{{ formEditModel.name }}</div>
        </el-form-item>
        <div
          v-for="(item,index) in labelList"
          :key="item.value"
          class="flex-item"
        >
          <ak-field
            v-model="item.label"
            label="标签名称:"
            prop="label"
          />
          <ak-field
            v-model="item.value"
            label="标签值:"
            prop="value"
          />
          <el-button
            text
            @click="delLabelClick(index)"
          >
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
        </div>
        <el-button @click="addLabelClick">
          添加一行
        </el-button>
      </ak-form>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
  import {ref, nextTick} from 'vue'
  import {Delete} from "@element-plus/icons-vue";
  import {useLayoutStore} from '@/store/layout'

  const layoutStore = useLayoutStore()

  const dictDialogTitle = ref("新增字典")
  const formEl = ref()
  const editEvent = (row: any) => {
    visible.value = true
    dictDialogTitle.value = '编辑字典'
    nextTick(() => {
      formEl.value.getData({id: row.id})
    })
  }
  const controlBtn = ref([
    {
      key: 'add',
      click: () => {
        visible.value = true;
        dictDialogTitle.value = '新增字典'
      }
    },
    {
      key: 'edit', click: (row) => {
        if (row?.length > 0) {
          editEvent(row[0])
        }
      }
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
      prop: 'name',
      label: '字典名称'
    },
    {
      prop: 'type',
      label: '字典标识'
    },
    {
      prop: 'status',
      label: '状态',
      render: 'tag',
      replaceValue: {'1': '启用', '0': '禁用'},
      custom: {'1': 'success', '0': 'danger'},
      search: {
        render: 'select',
        options: [{value: '0', label: '禁用'}, {value: '1', label: '启用'}]
      }
    },
    {
      prop: 'dateTime',
      label: '更新时间',
      width: 180,
      render: 'datetime',
      search: false
    },
    {
      label: '操作',
      render: 'buttons',
      buttons: [
        {
          key: 'edit',
          label: '编辑',
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
          label: '设置',
          click: (row) => {
            setVisible.value = true
            formEditModel.value.id = row.id
            formEditModel.value.name = row.name
            formEditModel.value.type = row.type // 传一个标识作为设置和修改的区别
            if (row.children) {
              labelList.value = JSON.parse(row.children)
            }
          }
        },
        {
          label: '删除',
          key: 'del',
          tooltip: 'del',
          popConfirm: {confirmButtonType: 'danger'},
          display: (row) => {
            return row.isSys === 0
          },
          disabled: (row) => {
            return row.isSys === 1
          }
        }
      ]
    }
  ])

  const beforeEvent = (type: string, params: any) => {
    return params
  }
  const afterEvent = () => {
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
      attr: {}
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
      render: 'radio',
      prop: 'status',
      label: '状态',
      options: [{label: '禁用', value: 0}, {label: '启用', value: 1}]
    },
    {
      prop: 'remark',
      label: '备注',
      attr: {
        type: 'textarea',
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
  const setFormBefore = (params: any) => {
    params.children = JSON.stringify(labelList.value)
    return params
  }
  const setFormAfter = () => {
    setVisible.value = false
    // 更新本地系统字典
    layoutStore.updateSystemDict(true);
  }
  const cancelSetClick = () => {
    setVisible.value = false
  }

  const delLabelClick = (index: number) => {
    labelList.value.splice(index, 1)
  }
  const tableListEl = ref()
  const afterSubmit = (res: any, success: boolean, type: string) => {
    if (type !== 'detail') {
      visible.value = false
      tableListEl.value.getData()
    }
  }
</script>
<style scoped lang="scss">
.flex-item {
  display: flex;
  justify-content: space-between;
}

.flex-item > div {
  margin-right: 10px;
}
</style>