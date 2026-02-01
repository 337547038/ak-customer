<template>
  <van-collapse v-model="collapseActive">
    <w-list
      ref="listRef"
      :api="{get:'contractPaymentList',del:'contractPaymentDel'}"
      :search-form-data="searchFormData"
      :add-icon-visible="true"
      :form-title="formTitle"
      :before="listBefore"
      @search-form-change="searchFormChange"
      @nav-bar-event="navBarEvent"
    >
      <template #item="{data}">
        <van-swipe-cell>
          <van-collapse-item
            :name="data.id"
            :value="data.code"
          >
            <template #title>
              <div @click.stop="operateBtnClick(data)">
                {{ data.customerName }}
                <van-tag
                  v-if="data.status"
                  :type="data.status===1?'danger':data.status===2?'success':'primary'"
                >
                  {{ statusDict[data.status] }}
                </van-tag>
              </div>
            </template>
            <ul class="ul">
              <li>合同名称：{{ data.contractName }}</li>
              <li>合同编号：{{ data.contractCode }}</li>
              <li>回款金额：{{ data.money }}</li>
              <li>回款账户：{{ sysDict.accountType[data.account] }}</li>
              <li>创建时间：{{ dateFormat(data.creatDate) }}</li>
              <li>审核状态：{{ statusDict[data.status] }}</li>
            </ul>
          </van-collapse-item>
          <template #right>
            <van-button
              v-for="item in operateBtn(data)"
              :key="item.key"
              square
              :color="item.color"
              :type="item.type"
              :text="item.text"
              size="small"
              @click="operateBtnClick(data,item.key)"
            />
          </template>
        </van-swipe-cell>
      </template>
      <pay-form
        ref="payFormRef"
        :is-add="isAdd"
        :current-user-id="currentUserId"
        @click-event="payFormClickEvent"
      />
    </w-list>
  </van-collapse>
</template>

<script setup lang="ts">
import {ref, nextTick, computed} from 'vue'
import {useLayoutStore} from '@/store/layout'
import {dateFormatting} from '@/utils'
import PayForm from "../components/payFormWap.vue";

const layoutStore = useLayoutStore()
const listRef = ref()
const collapseActive = ref([])
const statusDict: { [key: number]: string } = {1: '待审核', 2: '已确认', 3: '未通过'}
const sysDict = computed(() => {
  return layoutStore.getSystemDict()
})
const hasChild = layoutStore.userInfo?.hasChild


const dateFormat = (time: string, type?: string) => {
  return dateFormatting(time, type === 'd' ? '{y}-{m}-{d}' : '{y}-{m}-{d} {h}:{i}')
}
const operateBtn: any = (row: any) => {
  return [
    {
      text: '审核', key: 'check', type: 'warning', visible: () => {
        return hasChild && row.userId !== layoutStore.userInfo?.id && row.status === 1
      }
    },
    {text: '编辑', key: 'edit', type: 'primary'},
    {text: '删除', key: 'del', type: 'danger'}
  ].filter(item => {
    if (typeof item.visible === 'function') {
      return item.visible() !== false
    }
    return true
  })
}
const payFormRef = ref()
const currentUserId = ref()
const operateBtnClick = (obj: { [key: string]: any }, key?: string) => {
  if (key === 'del') {
    listRef.value.deleteById(obj.id)
  } else {
    isAdd.value = false
    switch (key) {
      case 'check':
        formTitle.value = '审核合同回款'
        break
      case 'edit':
        formTitle.value = '修改合同回款'
        break
      default:
        formTitle.value = '查看合同回款'
        break
    }
    listRef.value.showForm(true)
    currentUserId.value = obj.userId
    nextTick(() => {
      payFormRef.value.getData(obj,key)
    })
  }
}
const userId = ref()
const searchFormData = ref([
  {
    label: '所属人员',
    prop: 'userId',
    render: 'picker',
    visible: () => {
      return hasChild
    },
    field: {clearable: true},
    picker: {
      ajax: {
        api: 'userChildList',
        data: {}
      },
      columnsFieldNames: {
        text: 'userName',
        value: 'id'
      },
    }
  },
  {
    prop: 'code',
    label: '回款编号'
  },
  {
    label: '客户名称',
    prop: 'customerId',
    field: {clearable: true},
    render: 'picker',
    //modelValue: route.query.id,
    picker: {
      remote: true,
      openLoad: true,
      keyword: 'company',
      ajax: {
        api: 'customerList',
        data: {
          userId: userId
        }
      },
      columnsFieldNames: {
        text: 'company',
        value: 'id'
      }
    }
  },
  {
    prop: 'status',
    label: '审核状态',
    render: 'picker',
    options: {1: '待审核', 2: '通过', 3: '拒绝'}
  }
])
const searchFormChange = (key: string, value: any, model: { [key: string]: any }) => {
  // 选择所属人员时，根据条件条件客户名称
  if (key === 'userId') {
    model.customerId = ''
  }
  userId.value = value
}
const isAdd = ref(true)
const formTitle = ref('新增合同回款')
const navBarEvent = (type: string) => {
  if (type === 'add') {
    isAdd.value = true
  }
}
const listBefore = (params: { [key: string]: any }) => {
  // 条件查询时有参数值时，则使用查询时选择的
  return params
}

const payFormClickEvent = (key: string) => {
  switch (key) {
    case 'cancel':
      listRef.value.showForm()
      break
    case 'submit':
      listRef.value.onRefresh()
      listRef.value.showForm()
      break
  }
}

</script>

<style scoped lang="scss">
.ul {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  color: #666;

  li {
    width: 50%;
    padding: 3px 0;
    font-size: 13PX;
  }
}

:deep(.van-swipe-cell__right) {
  display: flex;
  align-items: center;

  .van-button {
    height: 100%
  }
}
</style>