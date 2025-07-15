<template>
  <div style="display: flex;align-items: center">
    <el-input
        v-model="userName"
        readonly
        @click="open()"
        v-if="showInput"
        :placeholder="placeholder"
        :clearable="true"/>
    <el-button text type="primary" @click="clearUserId">清空</el-button>
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
              @node-click="handleNodeClick"
              :load="loadNode"
              lazy
          />
        </div>
        <div class="table-list">
          <ak-list
              ref="tableListEl"
              pk="id"
              :searchIconVisible="false"
              :columnsIconVisible="false"
              :before="before"
              :columns="columns"
              :api="{ list: 'userList'}"
          >
          </ak-list>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import {ref, watch} from 'vue'
  import {getRequest} from "@/api";
  import {onBeforeRouteLeave} from "vue-router";

  const props = withDefaults(
      defineProps<{
        showInput?: boolean
        placeholder?: string
        modelValue?: string
      }>(),
      {
        showInput: true
      }
  )

  const model = defineModel()
  const userName = ref('')
  const visible = ref(false)

  const open = (data?: any) => {
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
  const loadNode = (node, resolve, reject) => {
    getRequest('deptList', {tid: node.data.id || 0}).then((res) => {
      const data = res.data.list
      data.forEach(item => {
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
  const columns = ref([
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
          click: (row) => {
            userName.value = row.userName
            model.value = row.id
            visible.value = false
          }
        }
      ]
    }
  ])
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
    width: 180px;
    border: 1px solid #ebeef5;
    border-radius: 3px;
    padding: 10px 0;
    margin-right: 10px;
    max-height: 500px;
    overflow-y: auto;
  }

  .table-list {
    flex: 2;
  }


  .has-select {
    width: 200px;
    margin-left: 20px;

    .total {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 32px;
      margin-bottom: 10px;
    }
  }
}
</style>