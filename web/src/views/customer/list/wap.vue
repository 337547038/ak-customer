<template>
  <van-tabs
    v-model:active="tabsActive"
    @change="tabsChange"
  >
    <van-tab
      title="我的客户"
      name="default"
    />
    <van-tab
      v-if="hasChild"
      title="下属客户"
      name="child"
    />
    <van-tab
      title="共享客户"
      name="shareWithMe"
    />
    <van-tab
      title="我共享的"
      name="myShare"
    />
    <template #nav-right>
      <div
        v-show="tabsActive!=='shareWithMe'"
        class="manage-btn"
        @click="showCheckbox=!showCheckbox"
      >
        <i class="icon-manage" />
        {{ showCheckbox ? '完成' : '管理' }}
      </div>
    </template>
  </van-tabs>
  <van-collapse v-model="collapseActive">
    <van-checkbox-group v-model="checkedResult">
      <w-list
        ref="listRef"
        :api="{get:'customerList'}"
        :search-form-data="searchFormData"
        :add-icon-visible="true"
        :form-title="formTitle"
        :before="listBefore"
        @nav-bar-event="navBarEvent"
      >
        <template #item="{data}">
          <van-swipe-cell>
            <van-collapse-item
              :name="data.id"
              :value="tabsActive==='child'?`${data.userName}`:''"
            >
              <template #title>
                <van-checkbox
                  v-show="showCheckbox"
                  :name="data.id"
                  icon-size="16px"
                  @click.stop
                />
                <span @click.stop="detailClick(data)">{{ data.company }}</span>
              </template>
              <ul class="ul">
                <li>合作类型：{{ sysDict.cooperationType[data.type] }}</li>
                <li>所属区域：{{ getCityByCode(data.area) }}</li>
                <li>行业分类：{{ sysDict.industryType[data.industry] }}</li>
                <li>来源：{{ sysDict.source[data.source] }}</li>
                <li>最近联系：{{ dateFormat(data.lastTime) }}</li>
                <li>下次联系：{{ dateFormat(data.nextTime) }}</li>
                <li>创建时间：{{ dateFormat(data.creatTime) }}</li>
                <li>更新时间：{{ dateFormat(data.updateTime) }}</li>
              </ul>
            </van-collapse-item>
            <template #right>
              <van-button
                v-for="item in operateBtn.filter(v=>v.visible!==false)"
                :key="item.key"
                square
                :color="item.color"
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
          :submit-url="isAdd?'customerSave':'customerEdit'"
          request-url="customerDetail"
          :before="beforeSubmit"
          :after="afterSubmit"
          :hide-filed="hideFiled"
          :disabled="tabsActive==='shareWithMe'"
          @cancel="cancelForm"
        >
          <template #files="{rows,value}">
            <Uploader
              :disabled="tabsActive==='shareWithMe'"
              :item="rows"
              :model-value="value"
              @update:model-value="updateUploadFiles"
            />
          </template>
        </w-form>
      </w-list>
    </van-checkbox-group>
  </van-collapse>
  <van-action-bar
    v-show="showCheckbox"
    :placeholder="true"
  >
    <div class="btn-group">
      <van-button
        size="small"
        type="primary"
        :disabled="!checkedResult.length"
        @click="selectUser('toUser')"
      >
        移交
      </van-button>
      <van-button
        size="small"
        type="warning"
        :disabled="!checkedResult.length"
        @click="moveUser('toCom')"
      >
        转入公海
      </van-button>
      <van-button
        v-if="tabsActive === 'default'"
        size="small"
        type="success"
        :disabled="!checkedResult.length"
        @click="selectUser('myShare')"
      >
        共享客户
      </van-button>
      <van-button
        v-if="tabsActive === 'myShare'"
        size="small"
        type="success"
        :disabled="!checkedResult.length"
        @click="moveUser('cancelShare')"
      >
        取消共享
      </van-button>
      <van-button
        size="small"
        type="danger"
        :disabled="!checkedResult.length"
        @click="moveUser('toInvalid')"
      >
        设为无效客户
      </van-button>
    </div>
    <van-button
      size="small"
      type="danger"
      color="linear-gradient(to right, #ff6034, #ee0a24)"
      @click="showCheckbox=false"
    >
      取消
    </van-button>
  </van-action-bar>
  <user-dialog
    ref="userDialogRef"
    v-model="userSelect"
    :show-field="false"
    :data="{label:'用户'}"
    :multiple="userMultiple"
    @select-click="selectClick"
  />
  <records ref="recordsRef" />
  <van-popup
    v-model:show="showSharePicker"
    destroy-on-close
    position="right"
    teleport="body"
    :style="{ width: '88%', height: '100%' }"
    closeable
    :before-close="shareInfoChange"
  >
    <share-info
      ref="shareRef"
      :user-ids="shareInfoData.ids"
      :customer-id="shareInfoData.customer"
      @change="shareInfoChange"
    />
  </van-popup>
</template>

<script setup lang="ts">
  import {computed, markRaw, nextTick, ref} from 'vue'
  import ProvincesCity from "@/components/provincesCity/index.vue";
  import {useLayoutStore} from "@/store/layout";
  import getCityByCode from "@/utils/getCityByCode";
  import {dateFormatting} from "@/utils";
  import UserDialog from '@/components/userDialog/index.vue'
  import {getRequest} from "@/api";
  import {showSuccessToast} from 'vant'
  import formData from "./formData";
  import Uploader from '@/components/wForm/components/upload.vue'
  import {useRouter} from 'vue-router'
  import Records from "./components/records.vue";
  import ShareInfo from "./components/shareInfo.vue";

  const router = useRouter();
  const layoutStore = useLayoutStore()

  const hasChild = computed(() => {
    return layoutStore.userInfo?.hasChild
  })

  const sysDict = computed(() => {
    return layoutStore.getSystemDict() || {}
  })

  const listRef = ref()
  const formTitle = ref('新增客户')
  const isAdd = ref(true)
  const formRef = ref()
  const showCheckbox = ref(false)
  const dateFormat = (time: string) => {
    return dateFormatting(time, '{y}-{m}-{d} {h}:{i}')
  }
  const checkedResult = ref([])
  const collapseActive = ref([])
  const tabsActive = ref('default')

  const tabsChange = () => {
    listRef.value.onRefresh()
    showCheckbox.value = false
    collapseActive.value = []
  }

  const listBefore = (params: any) => {
    params.extend.columns = 'contact'
    if (tabsActive.value !== "default") {
      params.extend.search = tabsActive.value
    }
    return params
  }

  const searchFormData = ref([
    {
      prop: 'userId',
      label: '下属用户',
      render: 'picker',
      visible: () => {
        return tabsActive.value === 'child'
      },
      picker: {
        ajax: {
          api: 'userChildList',
          data: {}
        },
        columnsFieldNames: {
          text: 'userName',
          value: 'id'
        }
      }
    },
    {prop: 'company', label: '客户名称'},
    {
      prop: 'type', label: '合作类型', render: 'picker',
      options: 'cooperationType'
    },
    {
      prop: 'area', label: '所属区域', render: 'component',
      component: markRaw(ProvincesCity)
    },
    {prop: 'industry', label: '行业分类', render: 'picker', options: 'industryType'},
    {prop: 'source', label: '来源', render: 'picker', options: 'source'},
  ])

  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }

  //
  const userSelect = ref()
  const userDialogRef = ref()
  const currentBtnType = ref()
  const userMultiple = ref(false)
  const selectUser = (type: string) => {
    userMultiple.value = type === 'myShare';
    userDialogRef.value.showPopup()
    currentBtnType.value = type
  }
  // 移入公海/设为无效客户
  const moveUser = (type: string) => {
    currentBtnType.value = type
    selectClick({})
  }
  const selectClick = (userSelect: any) => {
    const ids = checkedResult.value.join(',')
    let userIds = userSelect?.id
    let urlKey = 'customerMove'
    if (['myShare', 'cancelShare'].includes(currentBtnType.value)) {
      if (['myShare'].includes(currentBtnType.value)) {
        userIds = userSelect === '0' ? '0' : userSelect?.join(',')
      }
      urlKey = 'customerShare'
    }
    getRequest(urlKey, {ids: ids, userId: userIds, type: currentBtnType.value})
        .then(() => {
          showSuccessToast('操作成功')
          listRef.value.onRefresh()
          showCheckbox.value = false
          checkedResult.value = []
        })
  }

  const detailClick = (obj: { [key: string]: any }) => {
    cancelForm(true)
    isAdd.value = false
    formTitle.value = '编辑/查看客户：' + obj.company
    nextTick(() => {
      formRef.value.getData({id: obj.id})
    })
  }

  // add or edit
  const uploadFiles = ref()
  const hideFiled = computed(() => {
    return isAdd.value ? ['creatTime', 'updateTime', 'lastTime', 'nextTime'] : []
  })
  const updateUploadFiles = (value: string) => {
    uploadFiles.value = value
  }
  const formDataWap = computed(() => {
    return formData.filter(item => !hideFiled.value.includes(item.prop)).map(item => {
      const newItem = Object.assign({}, item);
      if (newItem.formItem) {
        newItem.rules = newItem.formItem?.rules || []
        delete newItem.formItem
      }
      if (newItem.attr) {
        newItem.field = newItem.attr
        delete newItem.attr
      }
      if (newItem.visible) {
        delete newItem.visible
      }
      if (newItem.render === 'select') {
        newItem.render = 'picker'
      }
      return newItem;
    })
  })
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
  const cancelForm = (visible?: boolean) => {
    // 关闭开启弹层
    listRef.value.showForm(visible)
  }
  const recordsRef = ref()
  const shareRef = ref()
  const showSharePicker = ref(false)
  const shareInfoData = ref({ids: '', customer: ''})
  const operateBtn = computed(() => {
    return [
      {text: '详情', key: 'detail', color: 'rgb(84, 112, 198)'},
      {text: '跟进记录', key: 'follow', color: 'rgb(145, 204, 117)'},
      {text: '联系人', key: 'contact', color: 'rgb(250, 200, 88)'},
      {text: '合同', key: 'contract', color: 'rgb(238, 102, 102)'},
      {text: '操作记录', key: 'record', color: 'rgb(115, 192, 222)'},
      {text: '分享详情', key: 'share', color: 'rgb(234, 124, 204)', visible: tabsActive.value === 'myShare'}
    ]
  })
  const operateBtnClick = (obj: { [key: string]: any }, key: string) => {
    switch (key) {
      case 'detail':
        detailClick(obj)
        break
      case 'record':
        recordsRef.value.getData(obj.id)
        break
      case 'share':
        showSharePicker.value = true
        shareInfoData.value.ids = obj.shareUserId
        shareInfoData.value.customer = obj.id
        nextTick(() => {
          shareRef.value.getData()
        })
        break
      case 'contract':
        router.push({path: `/contract/${key}`, query: {id: obj.id}})
        break
      default:
        router.push({path: `/customer/${key}`, query: {id: obj.id}})
    }
  }
  const shareInfoChange = () => {
    showSharePicker.value = false
    shareInfoData.value.ids=''
    shareInfoData.value.customer=''
    listRef.value.onRefresh()
  }
</script>

<style scoped lang="scss">
.manage-btn {
  display: flex;
  align-items: center;
  font-size: 14PX;
  text-align: right;
  cursor: pointer;
  margin-left: 60px;
  margin-right: 20px;
  color: #1989fa
}

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

:deep(.van-action-bar) {
  bottom: 50PX;
  display: flex;
  justify-content: space-between;
  padding: 0 10PX;

  button {
    margin-right: 5px
  }
}

:deep(.van-cell__title) {
  display: flex;

  .van-checkbox {
    margin-right: 5px
  }
}
:deep(.van-collapse-item .van-cell__value){
  display: none;
}
</style>