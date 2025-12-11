<template>
  <div v-if="!isMobile()">
    <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'customerList',del:'customerDel'}"
      :control-btn="controlBtn"
      :before="beforeList"
    />
    <DetailTable
      ref="detailTableRef"
      :disabled="true"
      tabs-type="shareWithMe"
    />
  </div>
  <van-collapse v-model="collapseActive">
    <van-checkbox-group v-model="checkedResult">
      <w-list
        ref="listRef"
        :api="{get:'customerList',del:'customerDel'}"
        :search-form-data="searchFormData"
        :add-icon-visible="false"
        :params="{status : invalidPage ? 3 : 2}"
        form-title="详情"
      >
        <template #item="{data}">
          <van-swipe-cell>
            <van-collapse-item
              :name="data.id"
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
                <li>行业分类：{{ sysDict.industryType[data.industry] }}</li>
                <li>所属区域：{{ getCityByCode(data.area) }}</li>
                <li>最近联系：{{ dateFormat(data.lastTime) }}</li>
                <li>更新时间：{{ dateFormat(data.updateTime) }}</li>
              </ul>
            </van-collapse-item>
            <template #right>
              <van-button
                v-for="item in operateBtn.filter(v=>v.visible!==false)"
                :key="item.key"
                :type="item.type"
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
          request-url="customerDetail"
          :disabled="true"
          @cancel="cancelForm"
        />
      </w-list>
    </van-checkbox-group>
  </van-collapse>
</template>

<script setup lang="ts">
  import {markRaw, ref, computed, nextTick} from 'vue'
  import ProvincesCity from "@/components/provincesCity/index.vue";
  import getCityByCode from "@/utils/getCityByCode";
  import DetailTable from "../list/components/detailTabs.vue";
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";
  import {permission} from "@/directive/permissions";
  import {dateFormatting, isMobile} from "@/utils";
  import {useLayoutStore} from '@/store/layout'
  import formData from "../list/formData";
  import {showSuccessToast, showFailToast} from 'vant'

  const layoutStore = useLayoutStore()

  const props = withDefaults(
      defineProps<{
        invalidPage?: boolean
      }>(),
      {}
  )

  const detailTableRef = ref()
  const tableListEl = ref()

  const canDelete = computed(() => {
    // 权限中存在delCustomer即可删除
    return permission("delCustomer")
  })

  const columns = [
    {
      type: 'selection',
      width: 30
    },
    {
      label: '序号',
      type: 'index',
      width: 60
    },
    {
      prop: 'company',
      label: '客户名称'
    },
    {
      prop: 'industry',
      label: '行业分类',
      render: 'tag',
      replaceValue: 'industryType',
      search: {
        render: 'select',
        options: 'industryType'
      }
    },
    {
      prop: 'area',
      label: '所属区域',
      search: {
        render: 'component',
        component: markRaw(ProvincesCity)
      },
      formatter: (row: any, column: any, cellValue: any) => {
        return getCityByCode(cellValue)
      },
      showOverflowTooltip: true
    },
    {
      prop: 'operation',
      label: '操作',
      render: 'buttons',
      search: false,
      fixed: 'right',
      buttons: [
        {
          key: 'detail',
          label: '详情',
          icon: '',
          attr: {
            text: true
          },
          click: (row) => {
            detailTableRef.value.openDrawer(false, row || {}, "")
          }
        },
        {
          key: 'del',
          label: '删除',
          icon: '',
          attr: {
            text: true
          },
          display: () => {
            return canDelete.value
          }
        }
      ]
    }
  ]
  const controlBtn = [
    {
      key: 'toMy',
      label: '转入跟进',
      type: 'primary',
      click: (row) => {
        // 提取所选行id
        const ids = row?.map(item => item.id).join(',')
        getRequest('customerMove', {ids: ids, type: 'toFollow'})
            .then(res => {
              ElMessage.success(res.message || '转入成功')
              tableListEl.value.getData()
            })
            .catch((res) => {
              ElMessage.success(res.message || '转入失败')
            })
      },
      disabled: (row: { [key: string]: any }) => {
        return !row?.length
      }
    },
    {
      key: 'del',
      display: () => {
        return canDelete.value
      }
    }
  ]
  const beforeList = (type: string, data: any) => {
    if (type === 'get') {
      data.status = props.invalidPage ? 3 : 2
      if (data.area) {
        data.area = data.area.join(',')
      }
    }
    return data
  }

  // wap
  const formRef = ref()
  const sysDict = computed(() => {
    return layoutStore.getSystemDict() || {}
  })
  const dateFormat = (time: string) => {
    return dateFormatting(time, '{y}-{m}-{d} {h}:{i}')
  }
  const listRef = ref()
  const collapseActive = ref([])
  const checkedResult = ref([])
  const searchFormData = ref([
    {prop: 'company', label: '客户名称'},
    {
      prop: 'area', label: '所属区域', render: 'component',
      component: markRaw(ProvincesCity)
    },
    {prop: 'industry', label: '行业分类', render: 'picker', options: 'industryType'},
  ])
  const showCheckbox = ref(false)
  const detailClick = (obj: { [key: string]: any }) => {
    listRef.value.showForm(true)
    nextTick(() => {
      formRef.value.getData({id: obj.id})
    })
  }
  const formDataWap = computed(() => {
    return formData.map(item => {
      const newItem = Object.assign({}, item);
      if (newItem.formItem) {
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
  const cancelForm = () => {
    listRef.value.showForm(false)
  }
  const operateBtn = ref([
    {text: '详情', key: 'detail', type: 'primary'},
    {text: '转入跟进', key: 'toFollow', type: 'success'},
    {text: '删除', key: 'del', type: 'danger', visible: canDelete.value}
  ])
  const operateBtnClick = (obj: { [key: string]: any }, key: string) => {
    if (key === 'detail') {
      detailClick(obj)
    } else if (key === 'del' && canDelete.value) {
      listRef.value.deleteById(obj.id)
    } else if (key === 'toFollow') {
      getRequest('customerMove', {ids: obj.id + '', type: key})
          .then(res => {
            showSuccessToast(res.message || '转入成功')
            listRef.value.onRefresh()
          })
          .catch((res) => {
            showFailToast(res.message || '转入失败')
          })
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