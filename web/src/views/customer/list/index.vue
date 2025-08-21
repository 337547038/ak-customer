<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
    <el-tab-pane label="我的客户" name="default"></el-tab-pane>
    <el-tab-pane label="下属客户" name="child" v-if="hasChild"></el-tab-pane>
    <el-tab-pane label="共享客户" name="shareWithMe"></el-tab-pane>
    <el-tab-pane label="我共享的" name="myShare"></el-tab-pane>
  </el-tabs>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columnsFilter"
      :api="{ list: 'customerList',export:'customerExport'}"
      :controlBtn="controlBtn"
      :before="beforeList"
      @eventClick="tableBtnEvent"
  >
  </ak-list>

  <DetailTable ref="detailTableRef" :tabsType="activeName"/>
  <importDialog ref="importDialogRef"/>
  <UserDialog
      ref="userDialogRef"
      :showInput="false"
      :multiple="userMultiple"
      @selectClick="userSelectClick"/>

</template>
<script setup lang="ts">
  import {ref, computed} from 'vue'
  import importDialog from './components/import.vue'
  import columns, {columnUser} from './columns'
  import {Button} from "@/components/list/types";
  import DetailTable from './components/detailTabs.vue'
  import UserDialog from '@/components/userDialog/index.vue'
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";
  import {useLayoutStore} from "@/store/layout";

  const layoutStore = useLayoutStore()

  const userDialogRef = ref()
  const userMultiple = ref(false)
  //　tabs
  const activeName = ref('default')

  const hasChild = computed(() => {
    return layoutStore.userInfo?.hasChild
  })
  const handleClick = (name: string) => {
    getData()
  }

  const columnsFilter = computed(() => {
    if (activeName.value === 'child') {
      return [
        ...columns.slice(0, 3),
        columnUser,
        ...columns.slice(3),
      ]
    }
    return columns
  })

  // list btn
  const importDialogRef = ref()
  // list
  const tableListEl = ref()
  const tableSelectRows = ref([])
  const currentBtn = ref()

  const getData = () => {
    tableListEl.value.getData()
  }
  const controlBtn = computed(() => {
    if (activeName.value === 'shareWithMe') {
      return []
    } else {
      return [
        {
          key: 'add',
          click: () => {
            addEditEvent(true)
          }
        },
        {
          key: 'import',
          label: '导入',
          click: () => {
            importDialogRef.value.openDialog(getData)
          },
          icon: 'icon-import'
        },
        {
          key: 'toUser',
          label: '移交',
          icon: 'icon-toUser',
          click: (row: { [key: string]: any }[]) => {
            userMultiple.value = false
            userDialogRef.value.open()
            tableSelectRows.value = row
            currentBtn.value = 'toUser'
          },
          disabled: (row: { [key: string]: any }) => {
            return !row?.length
          }
        },
        {
          key: 'toCom',
          label: '转入公海',
          icon: 'icon-toCom',
          disabled: (row: { [key: string]: any }) => {
            return !row?.length
          },
          click: (row: { [key: string]: any }[]) => {
            tableSelectRows.value = row
            currentBtn.value = 'toCom'
            userSelectClick({})
          },
          render: 'confirm',
          popConfirm: {
            title: '确认将所选客户转入公海吗？',
            width: 300
          },
        },
        {
          key: 'share',
          label: '共享客户',
          icon: 'icon-share',
          disabled: (row: { [key: string]: any }) => {
            return !row?.length
          },
          display: () => {
            return activeName.value === 'default'
          },
          click: (row: { [key: string]: any }[]) => {
            userMultiple.value = true
            tableSelectRows.value = row
            currentBtn.value = 'myShare'
            userDialogRef.value.open()
          }
        },
        {
          key: 'cancelShare',
          label: '取消共享',
          icon: 'icon-share',
          disabled: (row: { [key: string]: any }) => {
            return !row?.length
          },
          display: () => {
            return activeName.value === 'myShare'
          },
          click: (row: { [key: string]: any }[]) => {
            tableSelectRows.value = row
            currentBtn.value = 'cancelShare'
            userSelectClick({})
          }
        },
        {
          key: 'toInvalid',
          label: '设为无效客户',
          icon: 'icon-invalid',
          render: 'confirm',
          popConfirm: {
            title: '确认将所选客户设置为无效客户吗？',
            width: 300
          },
          disabled: (row: { [key: string]: any }) => {
            return !row?.length
          },
          click: (row: { [key: string]: any }[]) => {
            tableSelectRows.value = row
            currentBtn.value = 'toInvalid'
            userSelectClick({})
          }
        },
        /*{key: 'del'}*/
      ]
    }
  })

  const userSelectClick = (selectUser: any) => {
    // 提取所选行id
    const ids = tableSelectRows.value?.map(item => item.id).join(',')
    if (currentBtn.value === 'myShare' || currentBtn.value === 'cancelShare') {
      let userIds = ''
      if (currentBtn.value === 'myShare') {
        userIds = selectUser === 'all' ? '0' : selectUser?.map(item => item.id).join(',')
      }
      getRequest('customerShare', {ids: ids, userId: userIds, type: currentBtn.value})
          .then(res => {
            ElMessage.success("操作成功")
            // 取消选中的
            tableListEl.value.table?.clearSelection()
            if (currentBtn.value === 'cancelShare') {
              getData()
            }
          })
    } else {
      getRequest('customerMove', {ids: ids, userId: selectUser.id, type: currentBtn.value})
          .then(res => {
            ElMessage.success("操作成功")
            getData()
          })
          .catch(() => {
          })
    }
  }

  const detailTableRef = ref()

  const beforeList = (type: string, data: any) => {
    if (type === 'get') {
      // 添加参数
      data.extend.columns = 'contact'
      if (activeName.value !== "default") {
        data.extend.search = activeName.value
      }
    }
    return data
  }

  // 按钮事件，
  const addEditEvent = (isAdd: boolean, row?: { [key: string]: any }) => {
    detailTableRef.value.openDrawer(isAdd, row || {}, getData)
  }
  const tableBtnEvent = (type: string, btn: Button, row: any) => {
    if (type === 'row') {
      // 列表右侧按钮事件
      switch (btn.key) {
        case 'detail':
          addEditEvent(false, row)
          break
      }
    }
  }
</script>
<style scoped lang="scss">

</style>