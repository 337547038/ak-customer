<template>
  <div>
    <div
      v-if="showInput"
      style="display: flex;align-items: center"
    >
      <el-input
        v-model="userName"
        readonly
        :placeholder="placeholder"
        :clearable="true"
        @click="open()"
      />
      <el-button
        text
        type="primary"
        @click="clearUserId"
      >
        清空
      </el-button>
    </div>
    <el-dialog
      v-model="visible"
      title="用户选择"
      :append-to-body="true"
      width="800px"
    >
      <div class="expand-user-dialog">
        <div class="sidebar-tree">
          <el-tree
            ref="treeEl"
            node-key="id"
            :props="{ label: 'name', value: 'id', children:'children',isLeaf:'hasChildren'}"
            highlight-current
            :load="loadNode"
            lazy
            @node-click="handleNodeClick"
          />
        </div>
        <div class="table-list">
          <ak-list
            ref="tableListEl"
            pk="id"
            :search-icon-visible="false"
            :columns-icon-visible="false"
            :before="before"
            :columns="columns"
            :api="{ list: 'userList'}"
            @selection-change="selectionChange"
          />
          <div
            v-if="multiple"
            style="display: flex;justify-content: center"
          >
            <el-button
              v-if="selectAll"
              type="danger"
              @click="confirmClick('all')"
            >
              选择全部
            </el-button>
            <el-button
              type="primary"
              :disabled="!tableSelectRows.length"
              @click="confirmClick"
            >
              确定
            </el-button>
            <el-button @click="cancelClick">
              取消
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
  import {ref, watch, computed, nextTick} from 'vue'
  import {getRequest} from "@/api";
  import {onBeforeRouteLeave} from "vue-router";
  import {ElMessage} from "element-plus";

  const props = withDefaults(
      defineProps<{
        showInput?: boolean
        placeholder?: string
        modelValue?: string
        multiple?: boolean
        selectAll?: boolean
      }>(),
      {
        placeholder:'',
        modelValue:'',
        showInput: true,
        multiple: false,
        selectAll: true
      }
  )
  const emits = defineEmits<{
    (e: 'selectClick', val: any): void
  }>()
  const model = defineModel<any>()
  const userName = ref('')
  const visible = ref(false)

  const open = () => {
    visible.value = true

  }
  const unWatch = watch(() => props.modelValue, () => {
    userName.value = ''
    // 根据id回显用户名
    if (model.value && !userName.value) {
      getRequest('userGet', {id: model.value}).then(res => {
        userName.value = res.data.userName
      })
    }
  })


  const clearUserId = () => {
    model.value = ''
    userName.value = ''
  }

  // tree
  const deptId = ref()
  const handleNodeClick = (row: any) => {
    deptId.value = row.id
    tableListEl.value.getData()
  }
  const loadNode = (node:any, resolve:any) => {
    getRequest('deptList', {tid: node.data.id || 0}).then((res) => {
      const data = res.data.list
      data.forEach((item:any) => {
        item.hasChildren = !item.hasChildren
      })
      resolve(data || [])
    })
  }

  // table
  const tableListEl = ref()
  const before = (type: string, data: any) => {
    if (deptId.value) {
      data.departmentId = deptId.value
      data.status = 1
    }
    return data
  }
  const showCheckbox = computed(() => {
    return props.multiple
  })

  const columns = ref([
    {
      prop: 'selection',
      type: 'selection',
      visible: showCheckbox
    },
    {
      prop: 'userName',
      label: '用户名称'
    },
    {
      label: '操作',
      render: 'buttons',
      buttons: [
        {
          attr: {
            type: 'primary',
            text: true
          },
          key: 'select',
          label: '选择',
          click: (row:any) => {
            userName.value = row.userName
            model.value = row.id
            visible.value = false
            emits('selectClick', row)
          }
        }
      ]
    }
  ])

  // 多选时
  const tableSelectRows = ref([])
  const confirmClick = (type?: string) => {
    if (type === 'all') {
      // 选择所有
      emits('selectClick', type)
      cancelClick()
      return false
    }
    const rows = tableListEl.value.getSelectionRows()
    if (rows.length > 0) {
      emits('selectClick', rows)
      cancelClick()
    } else {
      ElMessage.error('请选择勾选用户')
    }

  }
  const cancelClick = () => {
    visible.value = false
    nextTick(() => {
      tableListEl.value.table?.clearSelection()
    })
  }
  const selectionChange = (row:any) => {
    tableSelectRows.value = row
  }

  onBeforeRouteLeave(() => {
    unWatch()
  })
  defineExpose({open})

</script>

<style lang="scss">
.expand-user-dialog {
  width: 100%;
  display: flex;
  align-items: flex-start;

  .sidebar-tree {
    width: 180PX;
    border: 1PX solid #ebeef5;
    border-radius: 3PX;
    padding: 10PX 0;
    margin-right: 10PX;
    max-height: 500PX;
    overflow-y: auto;
  }

  .table-list {
    flex: 2;
  }


  .has-select {
    width: 200PX;
    margin-left: 20PX;

    .total {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 32PX;
      margin-bottom: 10PX;
    }
  }
}
</style>