<template>
  <div
    ref="containerEl"
    v-loading="loading"
    class="table-list-container"
  >
    <Transition name="autoHeight">
      <search-form
        v-if="showSearch"
        v-show="toggleFormSearch"
        ref="searchFormEl"
        :columns="columns"
        @onsubmit="searchClick"
        @field-change="formFieldChange"
      />
    </Transition>
    <slot />
    <div class="control-btn">
      <div class="control-btn-group">
        <operate-button
          v-if="props.controlBtn?.length"
          position="top"
          class="group"
          :row="selectRows"
          :buttons="mergeDefaultBtn(props.controlBtn, 'top')"
          @click="btnClick"
        />
        <slot name="btnAppend" />
      </div>
      <icon-search-column
        v-if="columnsIconVisible||searchIconVisible"
        v-model="columnsCheck"
        :columns="columns"
        :show-search="showSearch"
        :search-icon-visible="searchIconVisible"
        :columns-icon-visible="columnsIconVisible"
        :key-columns="keyColumns"
        @search-toggle-click="toggleFormSearch = !toggleFormSearch"
      />
    </div>
    <slot name="tablePrepend" />
    <el-table
      v-bind="tableProp"
      ref="table"
      :data="tableData"
      class="table-list"
      :empty-text="tableProp?.emptyText||'暂无数据'"
      @selection-change="tableSelect"
    >
      <template
        v-for="item in columnsFilter"
        :key="item.prop || item.label"
      >
        <!-- 将一些多余的绑定信息去掉-->
        <el-table-column
          v-bind="item"
        >
          <template
            v-if="item.help"
            #header="scope"
          >
            {{ scope.column.label }}
            <el-tooltip placement="top">
              <template #content>
                <span v-html="item.help" />
              </template>
              <el-icon>
                <InfoFilled />
              </el-icon>
            </el-tooltip>
          </template>
          <template #default="scope">
            <slot
              v-if="item.prop && $slots[item.prop]"
              :name="item.prop"
              :index="scope.$index"
              :prop="item.prop"
              :row="scope.row"
              :value="scope.row[item.prop]"
            />
            <el-switch
              v-if="item.prop && item.render === 'switch'"
              v-bind="item.attr"
              v-model="scope.row[item.prop]"
              :loading="switchLoading"
              :before-change="
                switchBeforeChange.bind(this, scope.row[item.prop])
              "
              @change="switchChange($event, item, scope.row)"
            />
            <el-image
              v-if="item.prop && item.render === 'image'"
              v-bind="item.attr"
              :style="{
                width: item.attr?.width || '100px',
                height: item.attr?.height || '100px'
              }"
              :preview-teleported="true"
              :z-index="99"
              :preview-src-list="getImgSrc(scope.row[item.prop], 'preview')"
              :src="getImgSrc(scope.row[item.prop])"
            />
            <el-tag
              v-if="item.prop && item.render === 'tag' && scope.row[item.prop]!==undefined"
              v-bind="item.attr"
              :type="getTagType(scope.row[item.prop], item.custom)"
            >
              {{ getTagVal(scope.row[item.prop], item.replaceValue) }}
            </el-tag>
            <el-text
              v-if="item.prop && item.render === 'text'"
              v-bind="item.attr"
              :type="getTagType(scope.row[item.prop], item.custom)"
            >
              {{
                getTagVal(scope.row[item.prop], item.replaceValue)
              }}
            </el-text>
            <el-link
              v-if="item.prop && item.render === 'link'"
              v-bind="item.attr"
            >
              {{ scope.row[item.prop] }}
            </el-link>
            <span
              v-if="
                item.prop &&
                  item.render &&
                  ['datetime', 'date'].includes(item.render)
              "
            >
              {{ getDateFormat(item, scope.row[item.prop]) }}
            </span>
            <OperateButton
              v-if="item.render === 'buttons' && item.buttons?.length"
              class="btn-group"
              :row="scope.row"
              :buttons="mergeDefaultBtn(item.buttons)"
              @click="tableBtnClick(scope.row, $event)"
            />
          </template>
        </el-table-column>
      </template>
    </el-table>
    <slot name="tableAppend" />
    <div class="pagination">
      <el-pagination
        v-if="props.pagination"
        v-show="total > pageSize"
        v-model:current-page="current"
        :page-size="pageSize"
        layout="total, prev, pager, next"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
  import {
    ref,
    onMounted,
    computed,
    markRaw,
    onBeforeUnmount,
    nextTick
  } from 'vue'
  import {ElMessage} from 'element-plus'
  import {getRequest} from '@/api'
  import {Api, Columns} from './types'
  import {dateFormatting, jsonParseStringify} from '@/utils'
  import OperateButton from './components/operateButton.vue'
  import SearchForm from './components/searchForm.vue'
  import {Delete, Edit, Histogram, Plus} from '@element-plus/icons-vue'
  import type {Button, EventType} from './types'
  import {useLayoutStore} from '@/store/layout'

  import IconSearchColumn from './components/iconSearchColumn.vue'


  defineOptions({name: 'AkList'})
  const props = withDefaults(
      defineProps<{
        tableProp?: any
        columns?: Columns[]
        autoLoad?: boolean
        params?: { [key: string]: any }
        api?: Api
        pagination?: { pageSize: number; current: number } | boolean
        before?: (type: EventType, params: any) => boolean
        after?: (type: EventType, res: any, isSuccess?: boolean) => any
        pk?: number | string // 主键，用于删除等
        controlBtn?: Button[]
        showSearch?: boolean
        fixedBottomScroll?: boolean | string
        searchIconVisible?: boolean // 搜索折叠icon按钮
        columnsIconVisible?: boolean // 列显示隐藏icon按钮
        keyColumns?: string
      }>(),
      {
        tableProp:null,
        before:null,
        after:null,
        controlBtn:null,
        keyColumns:'',
        columns: () => {
          return []
        },
        autoLoad: true, //是否自动加载列表数据
        params: () => {
          return {}
        },
        api: () => {
          return {
            export:'',
            list: '', // 列表数据api接口
            del: '', // 删除api接口
            edit: '' // 修改保存api,用于列表中的switch切换各种状态时
          }
        },
        pagination: () => {
          //分页信息，false时不显示分页信息
          return {
            pageSize: 20,
            current: 1
          }
        },
        pk: 'id',
        showSearch: true,
        fixedBottomScroll: true,
        searchIconVisible: true,
        columnsIconVisible: true
      }
  )

  const emits = defineEmits<{
    (e: 'selection-change', rows: any): void
    (e: 'formFieldChange', prop: string, val: any): void
    (e: 'eventClick', type: string, btn: any, row?: any): void
  }>()

  const loading = ref(false)
  const layoutStore = useLayoutStore()
  const columnsCheck = ref([])

  const columnsFilter = computed(() => {
    return props.columns.filter((item: Columns) => {
      let visible = item.visible
      if (typeof item.visible === 'function') {
        visible = item.visible()
      }
      if (columnsCheck.value.length) {
        return columnsCheck.value.includes(item.prop || item.type) && visible !== false
      } else {
        return visible !== false
      }
    })
  })
  const containerEl = ref()
  //条件搜索相关
  const toggleFormSearch = ref(true)
  const searchFormValue = ref({})
  const searchClick = (model: any) => {
    searchFormValue.value = model
    getData(1)
  }
  //筛选表单改变事件
  const formFieldChange = (prop: string, value: any, model: any) => {
    emits('formFieldChange', prop, value, model)
  }

  //处理时间
  const getDateFormat = (obj: any, val: any) => {
    if (!val) {
      return val
    }
    let formatType = obj.timeFormat //指定格式时
    if (!formatType) {
      //没有指定格式时
      if (obj.render === 'date') {
        formatType = '{yyyy}-{mm}-{dd}'
      } else {
        formatType = ''
      }
    }
    return dateFormatting(val, formatType)
  }
  //处理时间结束
  //处理tag
  const getTagType = (val: string | number, custom: any) => {
    if (!custom) {
      return
    }
    return custom[val]
  }
  const getTagVal = (val: string | number, replaceValue: any) => {
    if (!replaceValue || val === undefined) {
      return val
    } else {
      if (typeof replaceValue === 'string') {
        // 使用字典时
        const obj = layoutStore.getSystemDict(replaceValue)
        return obj[val] || val
      }
      return replaceValue[val] || val
    }
  }
  //处理tag结束

  //处理switch切换事件
  const switchLoading = ref(false)
  const oldVal = ref() //修改前的值
  const switchBeforeChange = (val: number | string | boolean) => {
    oldVal.value = val
    return true
  }
  const switchChange = (val: string | number | boolean, obj: any, row: any) => {
    //提交修改
    const edit = props.api?.edit
    if (edit) {
      switchLoading.value = true
      let params: any = Object.assign({}, row, {[obj.prop]: val})
      if (props.before) {
        params = props.before('switchChange', params)
      }
      if (params === false) {
        return
      }
      getRequest(edit, params)
          .then((res: any) => {
            //修改成功，开关值已改变不用刷新数据
            switchLoading.value = false
            showMessage(res.msg, true)
            props.after && props.after('switchChange', res, true)
          })
          .catch((res: any) => {
            //修改失败，提示错误。
            row[obj.prop] = oldVal.value //回退状态
            switchLoading.value = false
            showMessage(res.msg)
            props.after && props.after('switchChange', res)
          })
    }
  }
  //处理switch切换事件结束

  //处理图片开始
  const getImgSrc = (src: string | string[], type?: string) => {
    if (!src) {
      return
    }
    if (type === 'preview') {
      if (typeof src !== 'object') {
        return src.split(',')
      }
      return src
    } else {
      //如果是数组则返回第一张
      if (typeof src === 'object') {
        return src[0]
      } else {
        return src.split(',')[0]
      }
    }
  }
  //处理图片结束

  //处理表格行右侧操作按钮
  const defaultBtn = ref<any>({
    export: {
      type: 'primary',
      name: 'export',
      label: '',
      tooltip: '导出',
      icon: 'icon-export',
      class: '',
      key: 'export',
    },
    add: {
      type: 'primary',
      name: 'Add',
      label: '',
      tooltip: '添加',
      icon: markRaw(Plus),
      class: '',
      key: 'add'
    },
    edit: {
      type: 'primary',
      name: 'Edit',
      label: '',
      tooltip: '编辑',
      icon: markRaw(Edit),
      class: '',
      key: 'edit'
    },
    detail: {
      type: 'primary',
      tooltip: '查看',
      key: 'detail',
      name: 'Detail',
      class: '',
      icon: markRaw(Histogram)
    },
    del: {
      render: 'confirm',
      type: 'danger',
      label: '',
      tooltip: '删除',
      name: 'Del',
      icon: markRaw(Delete),
      key: 'del',
      popConfirm: {
        title: '确认删除该记录吗？',
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        confirmButtonType: 'danger'
      }
    }
  })
  /**
   * 列表右侧按钮
   * @param btn 自定义的按钮
   * @param position 位置 top为表格上面位置，不传默认为表格右侧
   * @return 合并了初始配置的按钮数组
   */
  const mergeDefaultBtn = (btn: Button[], position?: string) => {
    const temp: any = []
    //表格上方按钮预设有add/edit/del，表格行右侧预设有edit/detail/del
    const includeBtn =
        position === 'top' ? ['edit', 'add', 'del', 'export'] : ['edit', 'detail', 'del']
    btn.forEach((item: Button) => {
      if (item.key && includeBtn.includes(item.key)) {
        if (item.key === 'del') {
          item.popConfirm = Object.assign(
              defaultBtn.value[item.key].popConfirm,
              item.popConfirm || {}
          )
        }
        //表格上方时默认添加label
        let defaultLabel: any = {}
        if (position === 'top' && !item.label) {
          const labelArray: any = {add: '新增', edit: '编辑', del: '批量删除', export: '导出'}
          defaultLabel = {label: labelArray[item.key]}
        }
        temp.push(
            Object.assign({}, defaultBtn.value[item.key], defaultLabel, item)
        )
      } else {
        temp.push(item)
      }
    })
    return temp
  }
  //列表右侧按钮事件
  const tableBtnClick = (row: any, btn: Button) => {
    emits('eventClick', 'row', btn, row)
    if (btn.key === 'del' && props.pk) {
      getDel([row[props.pk]])
    } else {
      //console.log(row, btn.key)
      // todo 编辑或查看时请数据拉回来，或者是对外暴露拉取数据的方法
    }
  }
  //表格上方操作按钮事件
  const btnClick = (btn: Button) => {
    emits('eventClick', 'top', btn)
    if (btn.key === 'del' && selectRows.value.length && props.pk) {
      const ids = selectRows.value.map((item) => item[props.pk])
      getDel(ids)
    } else if (btn.key === 'export' && selectRows.value.length) {
      downloadExport(selectRows.value)
    } /*else if (btn.key === "add") {

    } else {
      // todo 编辑或查看时请数据拉回来，或者是对外暴露拉取数据的方法
    }*/
  }

  //处理操作按钮结束

  //分页相关
  const current = ref(props.pagination?.current)
  const total = ref(0)
  const pageSize = ref(props.pagination?.pageSize)

  const getPageInfo = computed(() => {
    if (typeof props.pagination === 'boolean' && !props.pagination) {
      //不显示分页
      return {}
    } else {
      return {
        pageSize: pageSize.value,
        pageNum: current.value
      }
    }
  })

  const handleSizeChange = (page: number) => {
    pageSize.value = page
    console.log("1")
    getData()
  }
  const handleCurrentChange = (val: number) => {
    current.value = val
    getData()
  }

  //表格相关
  const tableData = ref([])
  const table = ref()
  const selectRows = ref([]) //勾选中上行
  //数据相关
  const getData = (index?: number) => {
    current.value = index || 1 //每次删除时都应该回到第一页，列表改变状态和编辑时刷新当前页，传index即可
    const listApi = props.api?.list
    const source = 'get'
    if (listApi) {
      let params: any = {
        extend: getPageInfo.value,
        ...props.params,
        ...searchFormValue.value //条件筛选数据
      }
      if (props.before) {
        params = props.before(source, jsonParseStringify(params)) ?? jsonParseStringify(params)
      }
      if (params === false) {
        return
      }
      loading.value = true
      getRequest(listApi, params)
          .then((res: any) => {
            let data = res.data
            if (props.after) {
              data = props.after(source, data, true) || data
            }
            tableData.value = data.list || data || []
            total.value = data.total || 0
            loading.value = false
          })
          .catch((res) => {
            showMessage(res.msg)
            props.after && props.after(source, res)
            loading.value = false
          })
    }
  }
  //删除单个或批量删除，这里使用id数组
  const getDel = (id: string[]) => {
    const delApi = props.api?.del
    if (delApi) {
      let params: any = {[props.pk]: id?.join(','), ...props.params}
      if (props.before) {
        params = props.before('del', params)
      }
      if (params === false) {
        return
      }
      getRequest(delApi, params)
          .then((res: any) => {
            let data = res.data
            if (props.after) {
              props.after('del', data, true)
            }
            if (data) {
              showMessage(res.message, true)
              getData(current.value)
            } else {
              showMessage(res.message)
            }
          })
          .catch((res: any) => {
            showMessage(res.message)
            props.after && props.after('del', res)
          })
    } else {
      ElMessage.error('请设置api.del删除地址')
    }
  }
  // 导出
  const downloadExport = (params: { [key: string]: any }) => {
    const exportApi = props.api?.export
    if (exportApi) {
      if (props.before) {
        params = props.before('export', params)
      }
      if (params === false) {
        return
      }
      getRequest(exportApi, params, {
        responseType: 'blob'
      })
          .then((res: any) => {
            ElMessage.success('正在导出..')
            const {data, headers} = res
            const filename: string
                = decodeURI(
                    headers['content-disposition'].split(';')[1].split('=')[1]
            )
            const downloadUrl: string = window.URL.createObjectURL(
                new Blob([data], {type: data.type})
            )
            const link = document.createElement('a')
            link.style.display = 'none'
            link.href = downloadUrl
            link.setAttribute('download', filename)
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
          })
          .catch((err) => {
            ElMessage.error(err.message)
          })
    } else {
      ElMessage.error('请设置api.export导出地址')
    }
  }
  const showMessage = (msg: string, success?: boolean) => {
    if (!msg) {
      return
    }
    if (success) {
      ElMessage.success(msg)
    } else {
      ElMessage.warning(msg)
    }
  }

  //当用户手动勾选数据行的 Checkbox 时触发的事件
  const tableSelect = (row: any) => {
    selectRows.value = row
    if (props.tableProp?.selectionChange) {
      props.tableProp?.selectionChange(row)
    }
    emits('selection-change', row)
  }
  const getSelectionRows = () => {
    return selectRows.value
  }
  //设置条件筛选表单初始值
  const searchFormEl = ref()
  const setFormValue = (obj: { [key: string]: any }) => {
    searchFormEl.value.setValue(obj)
  }

  const fixedBottomScroll = () => {
    if (props.fixedBottomScroll) {
      nextTick(() => {
        const tableEl = containerEl.value
        const tableBodyWrapDom = tableEl.querySelector('.el-scrollbar__view') // table父一级
        const tableBodyDom = tableEl.querySelector('.el-table__body') // table
        const {top: tableBodyDomTop} =
            tableBodyWrapDom.getBoundingClientRect()
        // console.log(tableBodyWrapDom.classList)
        if (
            tableBodyDomTop > window.innerHeight ||
            tableBodyWrapDom.classList.contains('is-scrolling-none')
        ) {
          // 此时列表在可视窗口的下侧不可见区域，因此不做任何修改
          tableBodyWrapDom.style.height = 'unset'
          tableBodyWrapDom.style.marginBottom = 'unset'
        } else {
          /*console.log('window.innerHeight:' + window.innerHeight)
          console.log('tableBodyDomTop:' + tableBodyDomTop)
          console.log('tableBodyDom.offsetHeight:' + tableBodyDom.offsetHeight)*/
          // 窗口高度 - 列表距顶部值 且 不超过自身实际值
          let wrapHeight = Math.min(
              window.innerHeight - tableBodyDomTop - 10,
              tableBodyDom.offsetHeight
          )
          // console.log(wrapHeight)
          tableBodyWrapDom.style.minHeight = '60px'
          tableBodyWrapDom.style.height = wrapHeight + 10 + 'px'
          // 需要用marginBottom填充，以保持列表原有高度，避免页面的纵向滚动条变化导致页面滚动的不流畅
          // tableBodyWrapDom.style.marginBottom =
          //  tableBodyDom.offsetHeight - wrapHeight + 'px'
        }
      })
    }
  }
  onMounted(() => {
    if (props.autoLoad) {
      getData()
    }
    if (props.fixedBottomScroll) {
      nextTick(() => {
        const scroll =
            typeof props.fixedBottomScroll === 'string'
                ? props.fixedBottomScroll
                : 'body'
        const scrollEl = document.querySelector(scroll)
        if (scrollEl) {
          scrollEl.addEventListener('scroll', fixedBottomScroll)
        }
        window.addEventListener('resize', fixedBottomScroll)
      })
    }
  })
  onBeforeUnmount(() => {
    if (props.fixedBottomScroll) {
      const scroll =
          typeof props.fixedBottomScroll === 'string'
              ? props.fixedBottomScroll
              : 'body'
      const scrollEl = document?.querySelector(scroll)
      if (scrollEl) {
        scrollEl.removeEventListener('scroll', fixedBottomScroll)
      }
      window.removeEventListener('resize', fixedBottomScroll)
    }
  })
  defineExpose({getData, getSelectionRows, table, setFormValue})
</script>
<style scoped lang="scss">
.control-btn {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;

  .control-btn-group {
    display: flex;
    align-items: center;
    flex: 2;

    .group {
      display: flex;
      align-items: center;

      :deep(button, div) {
        margin-left: 0 !important;
        margin-right: 12px;
      }
    }
  }

  .other {
    display: flex;
    align-items: center;

    :deep(button, div) {
      margin-left: 12px;
    }
  }
}

.btn-group {
  display: flex;
  align-items: center;

  :deep(button, div) {
    margin-left: 12px;
  }

  :deep(.is-text) {
    margin-left: 1px;
  }
}

.autoHeight-enter-active,
.autoHeight-leave-active {
  max-height: 200px;
  transition: all 0.6s;
  overflow: hidden;
}

.autoHeight-enter-from,
.autoHeight-leave-to {
  max-height: 0;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 15px 0
}
</style>
