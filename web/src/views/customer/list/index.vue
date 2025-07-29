<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
    <el-tab-pane label="我的客户" name="customer"></el-tab-pane>
    <el-tab-pane label="下属客户" name="second"></el-tab-pane>
    <el-tab-pane label="共享客户" name="share2"></el-tab-pane>
    <el-tab-pane label="我共享的" name="share"></el-tab-pane>
  </el-tabs>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
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
  import columns from './columns'
  import {Button} from "@/components/list/types";
  import DetailTable from './components/detailTabs.vue'
  import UserDialog from '@/components/userDialog/index.vue'
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";

  const userDialogRef = ref()
  const userMultiple = ref(false)
  //　tabs
  const activeName = ref('customer')
  const handleClick = (name: string) => {
    getData()
  }
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
    if (activeName.value === 'share2') {
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
            return activeName.value === 'customer'
          },
          click: (row: { [key: string]: any }[]) => {
            userMultiple.value = true
            tableSelectRows.value = row
            currentBtn.value = 'share'
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
            return activeName.value === 'share'
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
    if (currentBtn.value === 'share' || currentBtn.value === 'cancelShare') {
      let userIds = ''
      if (currentBtn.value === 'share') {
        userIds = selectUser === 'all' ? '0' : selectUser.map(item => item.id).join(',')
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
      getRequest('customerMove', {ids: ids, userId: user.id, type: currentBtn.value})
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
      switch (activeName.value) {
        case 'share':
        case 'share2':
          // 添加分享查询参数
          data.extend.type = activeName.value
          break
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