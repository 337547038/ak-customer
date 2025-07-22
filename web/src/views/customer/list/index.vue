<template>
  <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
    <el-tab-pane label="我的客户" name="first"></el-tab-pane>
    <el-tab-pane label="下属客户" name="second"></el-tab-pane>
    <el-tab-pane label="共享客户" name="third"></el-tab-pane>
    <el-tab-pane label="我的共享" name="fourth"></el-tab-pane>
  </el-tabs>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'customerList',export:'customerExport'}"
      :controlBtn="controlBtn"
      @eventClick="tableBtnEvent"
  >
  </ak-list>

  <el-drawer
      v-model="visible" size="75%"
      :title="isAddForm?'新增客户':`${formModel?.company||''}详情`"
      :before-close="cancelClick">
    <ak-form
        ref="formRef"
        pk="id"
        label-width="100"
        class="flex-form flex-form-2"
        :formProps="{disabled:true,inline:true}"
        @cancel="cancelClick"
        :after="afterSubmit"
        :data="formDataFilter"
        :before="formBefore"
        :api="{ detail: 'customerDetail',add:'customerSave', edit:'customerEdit' }"
        v-model="formModel">
      <template #files>
        <uploadFiles v-model="formModel.files" v-if="visible"></uploadFiles>
      </template>
    </ak-form>
    <DetailTable v-if="!isAddForm&&visible" :id="currentId"/>
  </el-drawer>
  <importDialog ref="importDialogRef"/>
</template>
<script setup lang="ts">
  import {ref, computed, nextTick} from 'vue'
  import importDialog from './components/import.vue'
  import uploadFiles from './components/upload.vue'
  import columns from './columns'
  import formData from './formData'
  import {Button} from "@/components/list/types";
  import DetailTable from './components/detailTabs.vue'

  //　tabs
  const activeName = ref('first')
  const handleClick = (name: string) => {
    if (name === 'first') {
      visibleTest.value = true
    } else if (name === 'second') {
      visibleTest.value = false
    }
  }
  // list btn
  const importDialogRef = ref()
  // list
  const tableListEl = ref()
  const controlBtn = ref([
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
        importDialogRef.value.openDialog(tableListEl.value.getData)
      },
      icon: 'icon-import'
    },
    {
      key: 'toUser',
      label: '移交',
      icon: 'icon-toUser'
    },
    {
      key: 'toCom',
      label: '转入公海',
      icon: 'icon-toCom'
    },
    {
      key: 'share',
      label: '共享客户',
      icon: 'icon-share'
    },
    {
      key: 'set',
      label: '批量设置',
      icon: 'icon-set'
    },
    {key: 'del'}
  ])

  const isAddForm = ref(false);
  const currentId=ref()
  // 按钮事件，
  const addEditEvent = (isAdd: boolean, row?: { [key: string]: any }) => {
    visible.value = true
    isAddForm.value = isAdd
  }
  const tableBtnEvent = (type: string, btn: Button, row: any) => {
    if (type === 'row') {
      // 列表右侧按钮事件
      switch (btn.key) {
        case 'detail':
          currentId.value = row.id
          addEditEvent(false, row)
          nextTick(() => {
            formRef.value.getData({id: row.id})
          })
          break
      }
    }
  }

  // form
  const formRef = ref()
  const visible = ref(false);
  const formBefore = (data: any, type: string) => {
    if (['add', 'update'].includes(type)) {
      if (data.area) {
        data.area = data.area.join(',')
      }
    }
    return data
  }
  const afterSubmit = (result: any, success: boolean, type: string) => {
    if (['add', 'update'].includes(type)) {
      cancelClick()
    } else if (type === 'detail') {
      // 按展示格式处理数据
      if (result.area) {
        result.area = result.area.split(',')
      }
    }
    return result
  }
  const formModel = ref({})
  const formDataFilter = computed(() => {
    if (isAddForm.value) {
      return formData.filter((item) => {
        return item.visible !== 'isDetailForm'
      })
    } else {
      return formData
    }
  })
  const cancelClick = () => {
    visible.value = false
    formModel.value = {}
  }

</script>
<style scoped lang="scss">

</style>