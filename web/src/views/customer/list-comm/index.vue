<template>
  <ak-list
      ref="tableListEl"
      pk="id"
      :columns="columns"
      :api="{ list: 'customerList',del:'customerDel'}"
      :controlBtn="controlBtn"
      :before="beforeList"
  >
  </ak-list>
  <DetailTable ref="detailTableRef" :disabled="true" tabsType="shareWithMe"/>
</template>

<script setup lang="ts">
  import {markRaw, ref, computed} from 'vue'
  import ProvincesCity from "@/components/provincesCity/index.vue";
  import getCityByCode from "@/utils/getCityByCode";
  import DetailTable from "../list/components/detailTabs.vue";
  import {getRequest} from "@/api";
  import {ElMessage} from "element-plus";
  import {permission} from "@/directive/permissions";

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
      if(data.area){
        data.area = data.area.join(',')
      }
    }
    return data
  }
</script>

<style scoped lang="scss">

</style>