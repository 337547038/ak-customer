<template>
  <ak-list
    ref="tableListEl"
    pk="id"
    :columns="columns"
    :api="{ list: 'userList', del:'userDelete'}"
    :control-btn="[
      {
        key:'add',
        click:()=>{
          addEditEvent(false)
        }
      },
      {key:'del'}]"
  />
  <el-dialog
    v-model="visible"
    width="800"
    :title="dialogTitle"
    class="form-dialog"
    :before-close="cancelClick"
  >
    <ak-form
      ref="formEl"
      v-model="formModel"
      class="flex-form flex-form-2"
      pk="id"
      label-width="120"
      :after="afterForm"
      :before="beforeSubmit"
      :data="formData"
      :api="{ add: 'userSave', edit: 'userEdit', detail:'userGet' }"
      @cancel="cancelClick"
    />
  </el-dialog>
</template>

<script setup lang="ts">
  import {ref, markRaw, nextTick} from 'vue'
  import userSelect from '@/components/userDialog/index.vue'
  import {getRequest} from "@/api";
  import validate from '@/components/form/validate'
  import {dateFormatting} from '@/utils'
  import {ElMessage} from "element-plus";

  const lazyLoadSelect = (node, resolve) => {
    getRequest('deptList', {tid: node.data.id || 0}).then((res) => {
      const list = res.data?.list || []
      list.forEach(item => {
        item.hasChildren = !item.hasChildren
      })
      resolve(list)
    })
  }
  const tableListEl = ref()
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
        render: 'select',
        options: 'sex',
        clearable: true
      }
    },
    {
      prop: 'status',
      label: '状态',
      render: 'tag',
      replaceValue: 'status',
      custom: {'1': 'success', '0': 'danger'},
      search: {
        render: 'select',
        options: 'status',
        clearable: true
      }
    },
    {
      prop: 'departmentId',
      label: '所属部门',
      formatter: row => row.departmentName,
      search: {
        clearable: true,
        style: {width: '260px'},
        render: 'tree-select',
        placeholder: '请选择所属部门',
        load: lazyLoadSelect,
        lazy: true,
        checkStrictly: true,
        props: {children: 'children', label: 'name', value: 'id', isLeaf: 'hasChildren'}
      }
    },
    {
      prop: 'roleId',
      label: '角色权限',
      search: {
        render: 'select',
        ajax: {
          api: 'roleList',
          data: {},
          label: 'name',
          value: 'id'
        },
        clearable: true
      },
      visible: false
    },
    {
      prop: 'loginTimer',
      label: '登录次数',
      search: false
    },
    {
      prop: 'lastLogin',
      label: '最后登录时间',
      width: 120,
      showOverflowTooltip: true,
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
      prop: 'creatTime',
      label: '创建时间',
      width: 120,
      showOverflowTooltip: true,
      render: 'datetime',
      search: false
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      width: 120,
      showOverflowTooltip: true,
      render: 'datetime',
      search: false
    },
    {
      fixed: 'right',
      label: '操作',
      render: 'buttons',
      width: 150,
      buttons: [
        {
          key: 'edit',
          label: '查看',
          attr: {
            text: true
          },
          click: (row) => {
            addEditEvent(true, row)
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
          },*/
          disabled: (row) => {
            return row.id === 1
          }
        }
      ]
    }
  ])

  // form
  const departmentTree = ref([])
  const dialogTitle = ref('新增用户')
  const isDetailForm = ref(true);
  const visible = ref(false)
  const formEl = ref()
  const formModel = ref({})
  const addEditEvent = (detail: boolean, row?: any) => {
    visible.value = true
    isDetailForm.value = detail
    formModel.value = {status: 1, sex: 1}
    if (detail) {
      dialogTitle.value = '查看/编辑用户：' + row.userName
      nextTick(() => {
        formEl.value.getData({id: row.id})
      })
      // 用于回显部门
      departmentTree.value = [{id: row?.departmentId, name: row?.departmentName}];
    } else {
      dialogTitle.value = '新增用户'
    }
  }
  const formatTime = (time: Date | undefined) => {
    if (time) {
      return dateFormatting(time)
    }
    return time
  }
  const cancelClick = () => {
    visible.value = false
    formEl.value.resetFields()
  }
  const beforeSubmit = (params?: any, type: string) => {
    if (type !== 'detail') {
      if (params.roleId) { // 提交或更新表单时
        params.roleId = params.roleId.join(',')
      }
    }
    if (type === 'add' && params.password === '') {
      ElMessage.error('密码不能为空')
      return false
    }
    return params
  }
  const afterForm = (data: any, success: boolean, type: string) => {
    if (type === 'detail') {
      if (success) {
        data.creatTime = formatTime(data?.creatTime)
        data.updateTime = formatTime(data?.updateTime)
        data.lastLogin = formatTime(data?.lastLogin)
        if (data.roleId) {
          data.roleId = data.roleId.split(',').map(Number)
        }
        return data
      }
    } else {
      cancelClick()
      tableListEl.value.getData()
    }
  }

  const formData = ref([
    {
      prop: 'userName',
      label: '用户名称',
      formItem: {
        rules: [{
          required: true,
          message: '请输入用户名称',
          trigger: 'blur'
        }]
      },
      attr: {
        disabled: isDetailForm
      },
    },
    {
      prop: 'password',
      label: '登录密码',
      attr: {
        type: 'password',
      },
      /*formItem: {
        rules: [{
          required: true,
          message: '请输入登录密码',
          trigger: 'blur'
        }]
      }*/
    },
    {
      prop: 'status',
      label: '状态',
      render: 'radio',
      options: 'status',
      attr: {}
    },
    {
      prop: 'sex',
      label: '性别',
      render: 'radio',
      options: 'sex',
      attr: {}
    },
    {
      prop: 'phone',
      label: '手机号码',
      formItem: {
        rules: [validate('mobile'), validate('required', '请输入手机号码')],
      }
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
      prop: 'roleId',
      label: '角色权限',
      render: 'select',
      ajax: {
        api: 'roleList',
        data: {},
        label: 'name',
        value: 'id'
      },
      attr: {
        multiple: true,
        collapseTags: true,
        collapseTagsTooltip: true
      },
      formItem: {
        rules: [validate('required', '请选择角色权限')],
      }
    },
    {
      prop: 'departmentId',
      label: '所属部门',
      render: 'tree-select',
      attr: {
        clearable: true,
        data: departmentTree,
        placeholder: '请选择上级部门',
        load: lazyLoadSelect,
        lazy: true,
        checkStrictly: true,
        props: {children: 'children', label: 'name', value: 'id', isLeaf: 'hasChildren'}
      },
      formItem: {
        rules: [{
          required: true,
          message: '请选择所属部门',
          trigger: 'blur'
        }]
      }
    },
    {
      prop: 'tid',
      label: '直属上级',
      render: 'component',
      component: markRaw(userSelect),
      attr: {
        //placeholder: '默认为所属部门负责人'
      }
    },
    {
      prop: 'departmentName',
      label:'所属部门',
      render:'text',
      visible: isDetailForm
    },
    {
      prop: 'tidName',
      label:'直属上级',
      render:'text',
      visible: isDetailForm
    },
    {
      prop: 'creatTime',
      label: '创建时间',
      attr: {
        disabled: true
      },
      visible: isDetailForm
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      attr: {
        disabled: true
      },
      visible: isDetailForm
    },
    {
      prop: 'lastLogin',
      label: '最后登录时间',
      attr: {
        disabled: true
      },
      visible: isDetailForm
    },
    {
      prop: 'loginTimer',
      label: '登录次数',
      attr: {
        disabled: true
      },
      visible: isDetailForm
    },
    {
      prop: 'ip',
      label: '最后登录ip',
      attr: {
        disabled: true
      },
      visible: isDetailForm
    },
    {
      prop: 'remark',
      label: '备注',
      attr: {
        style: {width: '100%'},
        rows: 5,
        type: 'textarea'
      }
    }
  ])

</script>

<style scoped lang="scss">

</style>