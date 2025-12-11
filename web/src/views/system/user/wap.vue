<template>
  <w-list
    ref="listRef"
    :api="{get:'userList',del:'userDelete'}"
    :add-icon-visible="true"
    :search-form-data="searchFormData"
    :form-title="isAdd?'新增用户':'编辑用户'"
    @nav-bar-event="navBarEvent"
  >
    <template #item="{data}">
      <van-swipe-cell>
        <van-cell-group class="cell-group">
          <h3>
            {{ data.userName }}
            <van-tag :type="getSexType(data.sex)">
              {{ sysDict.sex[data.sex] }}
            </van-tag>
          </h3>
          <ul>
            <li>手机:{{ data.phone }}</li>
            <li>微信:{{ data.weixin }}</li>
            <li>
              状态:
              <van-tag :type="data.status?'primary':'danger'">
                {{ sysDict.status[data.status] }}
              </van-tag>
            </li>
            <li>最后登录:{{ dateFormatting(data.lastLogin) }}</li>
            <li>登录次数:{{ data.loginTimer }}</li>
            <li>登录IP:{{ data.ip }}</li>
            <li>所属部门:{{ data.departmentName }}</li>
            <li>创建时间:{{ dateFormatting(data.creatTime) }}</li>
          </ul>
        </van-cell-group>
        <template #right>
          <van-button
            square
            type="primary"
            text="修改"
            @click="editClick(data)"
          />
          <van-button
            square
            type="danger"
            text="删除"
            @click="delClick(data.id)"
          />
        </template>
      </van-swipe-cell>
    </template>
    <w-form
      ref="formRef"
      :data="formData"
      :submit-url="isAdd?'userSave':'userEdit'"
      request-url="userGet"
      :before="beforeSubmit"
      :after="afterSubmit"
      :hide-filed="hideFiled"
      @cancel="cancelForm"
    />
  </w-list>
</template>

<script setup lang="ts">
  import {computed, markRaw, nextTick, ref, onMounted} from 'vue'
  import {getRequest} from "@/api";
  import validate from "@/components/form/validate";
  import userSelect from "@/components/userDialog/index.vue";
  import {dateFormatting} from "@/utils";
  import {useLayoutStore} from "@/store/layout";


  const layoutStore = useLayoutStore()

  const sysDict = computed(() => {
    return layoutStore.getSystemDict() || {}
  })
  const getSexType = (sex: number | undefined) => {
    switch (sex) {
      default:
        return 'primary'
      case 0:
        return 'danger'
      case 1:
        return 'success'
    }
  }
  const listRef = ref()
  const formRef = ref()
  const isAdd = ref(true)
  const hideFiled = computed(() => {
    if (isAdd.value) {
      return ['creatTime', 'updateTime', 'lastLogin', 'loginTimer', 'ip']
    } else {
      return []
    }
  })
  const lazyload = (node: any, resolve: any) => {
    getRequest('deptList', {tid: node?.id || 0}).then((res) => {
      const list = res.data?.list || []
      list.forEach(item => {
        if (item.hasChildren) {
          item.children = []
        }
      })
      resolve(list)
    })
  }
  const searchFormData = ref([
    {prop: 'userName', label: '用户名称'},
    {prop: 'phone', label: '手机'},
    {prop: 'weixin', label: '微信'},
    {prop: 'sex', label: '性别', render: 'picker', options: 'sex', picker: {}},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
    {
      prop: 'departmentId',
      label: '所属部门',
      render: 'cascader',
      cascader: {
        lazyLoad: lazyload,
        fieldNames: {text: 'name', value: 'id', children: 'children'},
        confirmVisible: true
      }
    },
    {
      prop: 'roleId', label: '角色权限', render: 'picker', picker: {
        ajax: {
          api: 'roleList',
          data: {}
        },
        columnsFieldNames: {
          text: 'name',
          value: 'id'
        }
      }
    },
  ])

  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }
  const editClick = (obj: { [key: string]: any }) => {
    listRef.value.showForm(true)
    nextTick(() => {
      formRef.value.getData({id: obj.id})
      isAdd.value = false
      deptOptions.value = ''
      nextTick(() => {

      })
    })
  }
  const delClick = (id: number) => {
    listRef.value.deleteById(id)
  }
  const deptOptions = ref()
  const formData = ref([
    {prop: 'userName', label: '用户名称', rules: [{required: true, message: '请填写用户名称'}]},
    {prop: 'password', label: '登录密码', field: {type: 'password'}},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
    {prop: 'sex', label: '性别', render: 'picker', options: 'sex'},
    {prop: 'phone', label: '手机号码', rules: [validate('mobile'), validate('required', '请输入手机号码')]},
    {prop: 'weixin', label: '微信号码'},
    {prop: 'qq', label: 'qq号码'},
    {
      prop: 'roleId', label: '角色权限', render: 'picker',
      rules: [{required: true, message: '请选择角色权限'}],
      picker: {
        ajax: {
          api: 'roleList',
          data: {}
        },
        columnsFieldNames: {
          text: 'name',
          value: 'id'
        },
        multiple: true
      }
    },
    {
      prop: 'departmentId',
      label: '所属部门',
      render: 'cascader',
      cascader: {
        options: deptOptions,
        lazyLoad: lazyload,
        fieldNames: {text: 'name', value: 'id', children: 'children'},
        confirmVisible: true
      }
    },
    {prop: 'tid', label: '直属上级', render: 'component', component: markRaw(userSelect)},
    {prop: 'remark', label: '备注', field: {type: 'textarea', rows: 3, autoSize: true}},
    {
      prop: 'creatTime',
      label: '创建时间',
      field: {
        disabled: true
      }
    },
    {
      prop: 'updateTime',
      label: '更新时间',
      field: {
        disabled: true
      }
    },
    {
      prop: 'lastLogin',
      label: '最后登录时间',
      field: {
        disabled: true
      }
    },
    {
      prop: 'loginTimer',
      label: '登录次数',
      field: {
        disabled: true
      }
    },
    {
      prop: 'ip',
      label: '最后登录ip',
      field: {
        disabled: true
      }
    },
  ])
  const beforeSubmit = (obj: { [key: string]: any }) => {
    return obj
  }
  const afterSubmit = (type: string, data: { [key: string]: any }) => {
    if (type === 'submit') {
      cancelForm(false)
      listRef.value.onRefresh()
    } else {
      data.creatTime = dateFormatting(data?.creatTime)
      data.updateTime = dateFormatting(data?.updateTime)
      data.lastLogin = dateFormatting(data?.lastLogin)
      deptOptions.value = data.departmentName
      return data
    }
  }
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }
  onMounted(() => {
  })
</script>

<style scoped lang="scss">
.cell-group {
  margin-bottom: 10px;
  padding: 10px 20px;

  h3 {
    font-weight: 400;
    margin-bottom: 10px;
    font-size: 15PX;
  }

  ul {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    color: #666;

    li {
      width: 50%;padding: 3px 0;font-size: 13PX;
    }
  }
}
:deep(.van-swipe-cell__right){display: flex;align-items: center;
.van-button{height:100%}}
</style>