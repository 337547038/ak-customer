<template>
  <van-collapse v-model="collapseActive">
    <w-list
      ref="listRef"
      :api="{get:'contractList',del:'contractDel'}"
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
              <div @click.stop="detailClick(data)">
                {{ data.name }}
                <van-tag
                  v-if="data.status"
                  :type="data.status===1?'danger':data.status===2?'success':'primary'"
                >
                  {{ statusDict[data.status] }}
                </van-tag>
              </div>
            </template>
            <ul class="ul">
              <li style="width:100%">
                客户名称：{{ data.customerName }}
              </li>
              <li>合同金额：{{ data.money }}</li>
              <li>公司签约人：{{ data.contactName }}</li>
              <li>已收款项：{{ data.payment || 0 }}</li>
              <li>状态：{{ statusDict[data.status] }}</li>
              <li style="width:100%">
                合同有效期：{{ dateFormat(data.startDate, 'd') + ' / ' + dateFormat(data.endDate, 'd') }}
              </li>
              <li>创建时间：{{ dateFormat(data.creatDate) }}</li>
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
      <w-form
        ref="formRef"
        :disabled="formDisabled"
        :data="formDataWap"
        :submit-url="isAdd?'contractSave':'contractEdit'"
        request-url="contractGet"
        :before="beforeSubmit"
        :after="afterSubmit"
        :hide-filed="hideFiled"
        @cancel="cancelForm"
        @field-value-change="fieldValueChange"
      >
        <template #formTop>
          <van-notice-bar
            v-if="showPassTips"
            text="审核通过，不能修改数据"
          />
        </template>
        <template #files="{rows,value}">
          <Uploader
            :item="rows"
            :model-value="value"
            @update:model-value="updateUploadFiles"
          />
        </template>
      </w-form>
    </w-list>
  </van-collapse>
  <van-popup
    v-model:show="showPayForm"
    position="right"
    :destroy-on-close="true"
    :style="{ width: '88%', height: '100%' }"
  >
    <div class="form-title">
      添加回款
      <van-icon
        name="cross"
        @click="showPayForm=false"
      />
    </div>
    <pay-form ref="payFormRef" />
  </van-popup>
</template>

<script setup lang="ts">
  import {ref, computed, nextTick, reactive} from 'vue'
  import {useLayoutStore} from '@/store/layout'
  import {dateFormatting} from '@/utils'
  import {useRoute} from 'vue-router'
  import Uploader from "@/components/wForm/components/upload.vue";
  import PayForm from '../components/payFormWap.vue'

  const props = withDefaults(
      defineProps<{
        formData: any
      }>(),
      {}
  )

  const layoutStore = useLayoutStore()
  const route = useRoute()

  const showPayForm = ref(false)
  const payFormRef = ref()
  const formDisabled = ref(false)
  const showPassTips = ref(false)
  const hideFiled = ref(['status'])
  const listRef = ref()
  const collapseActive = ref([])
  const statusDict: { [key: number]: string } = {1: '待审核', 2: '已确认', 3: '未通过'}
  const hasChild = layoutStore.userInfo?.hasChild
  const uploadFiles = ref()
  const formParams = reactive({customerId: '', userId: ''})
  const formDataWap = computed(() => {
    const temp: any = []
    props.formData.forEach((item: any) => {
      let render = item.render === 'radio' ? 'picker' : item.render
      let obj = {}
      switch (item.prop) {
        case 'customerId':
          render = 'picker'
          obj = {
            picker: {
              remote: true,
              keyword: 'company',
              ajax: {
                api: 'customerList',
                data: {
                  userId: formParams.userId
                }
              },
              columnsFieldNames: {
                text: 'company',
                value: 'id'
              }
            }
          }
          break
        case 'contactId':
          render = 'picker'
          obj = {
            picker: {
              remote: true,
              openLoad: true,
              keyword: 'name',
              ajax: {
                api: 'contactList',
                data: {
                  tid: formParams.customerId,
                  userId: formParams.userId
                }
              },
              columnsFieldNames: {
                text: 'name',
                value: 'id'
              }
            }
          }
          break
        case 'remark':
          obj = {field: {type: 'textarea'}}
          break
        case 'status':
          render = 'picker'
          obj = {
            picker: {
              columnsFieldNames: {
                text: 'label',
                value: 'value'
              }
            }
          }
          break
      }
      temp.push({
        label: item.label,
        prop: item.prop,
        rules: item.formItem?.rules || [],
        render: render,
        options: item.options,
        ...obj
      })
    })
    return temp
  })
  const updateUploadFiles = (value: string) => {
    uploadFiles.value = value
  }
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
      {text: '收款', key: 'money', type: 'success'},
      {text: '编辑', key: 'edit', type: 'primary'},
      {text: '删除', key: 'del', type: 'danger'}
    ].filter(item => {
      if (typeof item.visible === 'function') {
        return item.visible() !== false
      }
      return true
    })
  }
  const operateBtnClick = (obj: { [key: string]: any }, key: string) => {
    if (key === 'del') {
      listRef.value.deleteById(obj.id)
    } else if (key === 'edit' || key === 'check') {
      detailClick(obj, key)
    } else {
      // 收款
      showPayForm.value = true
      nextTick(() => {
        payFormRef.value.setValue({contractId: obj.id})
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
      label: '合同编号'
    },
    {
      prop: 'name',
      label: '合同名称',
    },
    {
      label: '客户名称',
      prop: 'customerId',
      field: {clearable: true},
      render: 'picker',
      modelValue: route.query.id,
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
      prop: 'startEndDate',
      label: '合同有效期',
      render: 'datePicker',
      datePicker: {
        type: 'dateRange',
        startPlaceholder: '合同开始时间',
        endPlaceholder: '合同结束时间',
      }
    },
    {
      prop: 'status',
      label: '状态',
      render: 'picker',
      options: {1: '待审核', 2: '已确认', 3: '未通过'}
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
  const formTitle = ref('新增合同')
  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }
  const listBefore = (params: { [key: string]: any }) => {
    // 条件查询时有参数值时，则使用查询时选择的
    if (!params.customerId) {
      params.customerId = route.query.id
    }
    if (params.startEndDate) {
      const split = params.startEndDate.split(',')
      params.startDate = split[0]
      params.endDate = split[1]
      delete params.startEndDate
    }
    return params
  }
  const formRef = ref()
  const detailClick = (obj: { [key: string]: any }, key?: string) => {
    isAdd.value = false
    formDisabled.value = false
    showPassTips.value = false
    hideFiled.value = ['status']

    if (obj.status === 2) {
      // 通过状态，如果查看下属的可以修改，否则不能修改
      if (obj.userId === layoutStore.userInfo.id) {
        formDisabled.value = true
        showPassTips.value = true
      }
    }
    if (key === 'edit') {
      formTitle.value = `编辑：${obj.name} 信息`
    } else if (key === 'check') {
      formTitle.value = `审核：${obj.name} 信息`
      hideFiled.value = []
    } else {
      formTitle.value = `查看：${obj.name} 信息`
      formDisabled.value = true
    }
    formParams.userId = obj.userId
    formParams.customerId = obj.customerId
    cancelForm(true)
    nextTick(() => {
      formRef.value.getData({id: obj.id})
    })
  }
  const fieldValueChange = (key: string, value: any, model: { [key: string]: any }) => {
    if (key === 'customerId') {
      model.contactId = ''
      formParams.customerId = value
    }
  }
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }
  const beforeSubmit = (obj: { [key: string]: any }) => {
    if (uploadFiles.value) {
      obj.files = uploadFiles.value
    }
    return obj
  }
  const afterSubmit = (type: string) => {
    if (type === 'submit') {
      listRef.value.onRefresh()
      cancelForm(false)
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

  .form-title {
    font-size: 28px;
    font-weight: 400;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between
  }
</style>