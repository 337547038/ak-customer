<template>
  <van-collapse v-model="collapseActive">
    <w-list
      ref="listRef"
      :api="{get:'contactList',del:'contactDel'}"
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
              <div @click.stop="detailClick(data)">
                {{ data.name }} <span v-if="data.position">({{ data.position }})</span>
              </div>
            </template>
            <ul class="ul">
              <li>手机：{{ data.phone }}</li>
              <li>是否决策人：{{ isDecisionMaker[data.decisionMaker] }}</li>
              <li>微信：{{ data.weixin }}</li>
              <li>性别：{{ sysDict.sex[data.sex] }}</li>
              <li>电话：{{ data.tel }}</li>
              <li>创建时间：{{ dateFormat(data.creatDate) }}</li>
              <li>电子邮箱：{{ data.email }}</li>
              <li>最后联系：{{ dateFormat(data.lastTime) }}</li>
              <li>QQ：{{ data.qq }}</li>
              <li>下次联系：{{ dateFormat(data.nextTime) }}</li>
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
        :data="formDataWap"
        :submit-url="isAdd?'contactSave':'contactEdit'"
        request-url="contactGet"
        :before="beforeSubmit"
        :after="afterSubmit"
        :hide-filed="hideFiled"
        @cancel="cancelForm"
      />
    </w-list>
  </van-collapse>
</template>

<script setup lang="ts">
  import {ref, computed, nextTick} from 'vue'
  import {useLayoutStore} from '@/store/layout'
  import {dateFormatting} from '@/utils'
  import {useRoute} from 'vue-router'

  const props = withDefaults(
      defineProps<{
        formData: any
      }>(),
      {}
  )

  const layoutStore = useLayoutStore()
  const route = useRoute()

  const listRef = ref()
  const collapseActive = ref([])
  const isDecisionMaker = {1: '是', 2: '否', 3: '未知'}
  const hasChild = layoutStore.userInfo?.hasChild
  const sysDict = computed(() => {
    return layoutStore.getSystemDict() || {}
  })
  const formDataWap = computed(() => {
    const temp = []
    props.formData.forEach(item => {
      let render = item.render === 'radio' ? 'picker' : item.render
      let obj = {}
      switch (item.prop) {
        case 'creatDate':
        case 'lastTime':
          obj = {field: {disabled: true}}
          break
        case 'tid':
          render = 'picker'
          obj = {
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
            }
          }
          break
        case 'birthday':
          obj = {
            datePicker: {
              minDate: new Date(1900, 0, 1),
              maxDate: new Date(),
            }
          }
          break
        case 'nextTime':
          obj = {
            datePicker: {
              type: 'dateTime'
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
  const dateFormat = (time: string) => {
    return dateFormatting(time, '{y}-{m}-{d} {h}:{i}')
  }
  const operateBtn = [{text: '编辑', key: 'edit', type: 'primary'}, {text: '删除', key: 'del', type: 'danger'}]
  const operateBtnClick = (obj: { [key: string]: any }, key: string) => {
    if (key === 'del') {
      listRef.value.deleteById(obj.id)
    } else if (key === 'edit') {
      formTitle.value = `编辑：${obj.name} 信息`
      detailClick(obj)
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
      label: '客户名称',
      prop: 'tid',
      field: {clearable: true},
      render: 'picker',
      modelValue:route.query.id,
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
    },
    {
      label: '手机',
      prop: 'phone',
    },
    {
      label: '微信',
      prop: 'weixin',
    },
  ])
  const searchFormChange = (key: string, value: any, model: { [key: string]: any }) => {
    // 选择所属人员时，根据条件条件客户名称
    if (key === 'userId') {
      model.tid = ''
    }
    userId.value = value
  }
  const isAdd = ref(true)
  const formTitle = ref('新增联系人')
  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }
  const listBefore = (params: { [key: string]: any }) => {
    // 条件查询时有参数值时，则使用查询时选择的
    if (!params.tid) {
      params.tid = route.query.id
    }
    return params
  }
  const formRef = ref()
  const detailClick = (obj: { [key: string]: any }) => {
    isAdd.value = false
    cancelForm(true)
    nextTick(() => {
      formRef.value.getData({id: obj.id})
    })
  }
  const hideFiled = computed(() => {
    if (isAdd.value) {
      return ['lastTime', 'creatDate']
    } else {
      return []
    }
  })
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }
  const beforeSubmit = (obj: { [key: string]: any }) => {
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
</style>