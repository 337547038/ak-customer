<template>
  <van-collapse v-model="collapseActive">
    <w-list
      ref="listRef"
      :api="{get:'followList',del:'followDel'}"
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
            :value="data.company"
          >
            <template #title>
              <div>
                {{ data.contactName }}
              </div>
            </template>
            <ul class="ul">
              <li>跟进方式：{{ sysDict.followType[data.type] }}</li>
              <li>跟进时间：{{ dateFormat(data.dateTime) }}</li>
              <li>跟进人：{{ data.userName }}</li>
              <li>跟进内容：{{ data.remark }}</li>
            </ul>
          </van-collapse-item>
          <template #right>
            <van-button
              v-for="item in operateBtn.filter(v=>v.visible!==false)"
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
        :data="formData"
        submit-url="followSave"
        request-url="followGet"
        :after="afterSubmit"
        :hide-filed="hideFiled"
        @cancel="cancelForm"
        @field-value-change="fieldValueChange"
      />
    </w-list>
  </van-collapse>
</template>

<script setup lang="ts">
  import {computed, ref} from 'vue'
  import {useLayoutStore} from "@/store/layout";
  import {useRoute} from 'vue-router'
  import {dateFormatting} from "@/utils";
  import validate from "@/components/form/validate";

  const route = useRoute()
  const layoutStore = useLayoutStore()
  const hasChild = layoutStore.userInfo?.hasChild
  const collapseActive = ref([])
  const listRef = ref()
  const isAdd = ref(true)
  const formTitle = ref('新增跟进记录')
  const userId = ref()
  const customerId = ref()
  const sysDict = computed(() => {
    return layoutStore.getSystemDict() || {}
  })
  const dateFormat = (time: string) => {
    return dateFormatting(time, '{y}-{m}-{d} {h}:{i}')
  }
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
      label: '客户名称',
      prop: 'tid',
      field: {clearable: true},
      render: 'picker',
      modelValue: route.query.id,
      picker: {
        remote: true,
        openLoad: true,
        keyword:'company',
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
      label: '联系人',
      prop: 'name',
      field: {clearable: true},
      render: 'picker',
      picker: {
        remote: true,
        openLoad: true,
        keyword:'name',
        ajax: {
          api: 'contactList',
          data: {
            userId: userId,
            customerId: customerId
          }
        },
        columnsFieldNames: {
          text: 'name',
          value: 'id'
        }
      }
    },
    {
      label: '跟进方式',
      prop: 'type',
      render: 'picker',
      options: 'followType',
    }
  ])
  const listBefore = (params: { [key: string]: any }) => {
    // 条件查询时有参数值时，则使用查询时选择的
    if (!params.tid) {
      params.tid = route.query.id
    }
    return params
  }
  const searchFormChange = (key: string, value: any, model: { [key: string]: any }) => {
    // 选择所属人员时，根据条件条件客户名称
    if (key === 'userId') {
      model.tid = ''
      userId.value = value
    }
    if (key === 'tid') {
      customerId.value = value
      model.name = ''
    }
  }
  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }
  const operateBtn = [{text: '删除', key: 'del', type: 'danger'}]
  const operateBtnClick = (obj: { [key: string]: any }, key: string) => {
    if (key === 'del') {
      listRef.value.deleteById(obj.id)
    }
  }
 const addCustomerId=ref()
  const fieldValueChange = (key: string, value: any, model: { [key: string]: any }) => {
    // 选择所属人员时，根据条件条件客户名称
    if (key === 'customerId') {
      addCustomerId.value = value
      model.contactId = ''
    }

  }
  const formData = ref([
    {
      label: '客户名称',
      prop: 'customerId',
      field: {clearable: true},
      render: 'picker',
      picker: {
        remote: true,
        keyword:'company',
        ajax: {
          api: 'customerList',
          data: {}
        },
        columnsFieldNames: {
          text: 'company',
          value: 'id'
        }
      },
      rules: [validate('required', '客户名称不能为空')]
    },
    {
      label: '联系人',
      prop: 'contactId',
      field: {clearable: true},
      render: 'picker',
      picker: {
        remote: true,
        keyword:'name',
        openLoad: true,
        ajax: {
          api: 'contactList',
          data: {
            tid: addCustomerId
          }
        },
        columnsFieldNames: {
          text: 'name',
          value: 'id'
        }
      },
      rules: [validate('required', '联系人不能为空')]
    },
    {
      label: '跟进方式',
      prop: 'type',
      render: 'picker',
      options: 'followType',
      rules: [validate('required', '请选择跟进方式')]
    },
    {
      label: '下次跟进时间',
      prop: 'nextTime',
      render: 'datePicker',
      datePicker: {
        type: 'datetime'
      }
    },
    {
      label: '跟进内容',
      prop: 'remark',
      field: {type:'textarea'},
      rules: [validate('required', '请输入跟进内容')]
    }
  ])
  const afterSubmit = (type:string) => {
    if (type === 'submit') {
      listRef.value.onRefresh()
      cancelForm(false)
    }
  }
  const hideFiled = ref([])
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }
</script>

<style scoped lang="scss">

</style>