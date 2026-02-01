<template>
  <w-list
    ref="listRef"
    :api="{get:'dictList',del:'dictDelete'}"
    list-cls="container"
    :search-form-data="searchFormData"
    :form-title="isAdd?'新增字典':'编辑字典'"
    :add-icon-visible="true"
    @nav-bar-event="navBarEvent"
  >
    <template #item="{data}">
      <van-swipe-cell>
        <van-cell
          :title="data.name"
          :value="data.type"
          is-link
          @click="cellClick(data)"
        >
          <template #title>
            <van-tag
              v-if="data.status===0"
              type="danger"
              style="margin-right: 2px"
            >
              禁用
            </van-tag>
            {{ data.name }}
          </template>
        </van-cell>
        <template #right>
          <van-button
            :disabled="data.isSys===1"
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
      :submit-url="isAdd?'dictSave':'dictEdit'"
      :before="beforeSubmit"
      :after="afterSubmit"
      @cancel="cancelForm"
    >
      <template #content>
        <van-cell
          title="设置字典数据"
        />
        <div
          v-for="(item,index) in labelList"
          :key="item.value"
          class="item-group"
        >
          <van-field
            v-model="item.label"
            label-width="40px"
            name="label"
            label="标签"
            placeholder="标签名称"
            :rules="[{ required: true, message: '请填写标签名称' }]"
          />
          <van-field
            v-model="item.value"
            label-width="50px"
            name="value"
            label="标签值"
            placeholder="用户名"
            :rules="[{ required: true, message: '请填写标签值' }]"
          />
          <van-button
            type="danger"
            size="small"
            class="del-btn"
            @click="delLabelClick(index)"
          >
            删除
          </van-button>
        </div>
        <div class="add-btn">
          <van-button
            type="primary"
            size="small"
            @click="addLabelClick"
          >
            添加一行
          </van-button>
        </div>
      </template>
    </w-form>
  </w-list>
</template>

<script setup lang="ts">
  import {nextTick, ref} from "vue";

  const listRef = ref()
  const formRef = ref()

  const delClick = (id: number) => {
    listRef.value.deleteById(id)
  }

  const isAdd = ref(true)
  const formData = ref([
    {prop: 'name', label: '字典名称', rules: [{required: true, message: '请填写字典名称'}]},
    {prop: 'type', label: '字典标识', rules: [{required: true, message: '请填写字典标识'}]},
    {prop: 'status', label: '状态', render: 'switch'},
    {prop: 'remark', label: '备注'},
    {prop: 'content', render: 'scope', label: ''},
  ])
  const searchFormData = ref([
    {prop: 'name', label: '字典名称'},
    {prop: 'status', label: '状态', render: 'switch', modelValue: 1},
  ])
  const navBarEvent = (type: string) => {
    if (type === 'add') {
      isAdd.value = true
    }
  }
  const cellClick = (data: any) => {
    cancelForm(true)
    isAdd.value = false
    labelList.value = JSON.parse(data.children)
    nextTick(() => {
      formRef.value.setValue(data)
    })
  }

  const labelList = ref<{ label: string, value: string | number }[]>([])
  const addLabelClick = () => {
    labelList.value.push({label: '', value: ''})
  }
  const delLabelClick = (index: number) => {
    labelList.value.splice(index, 1)
  }

  const cancelForm = (visible?: boolean) => {
    listRef.value.showForm(visible)
  }
  const beforeSubmit = (params: { [key: string]: any }) => {
    params.children = JSON.stringify(labelList.value)
    delete params.dateTime
    return params
  }
  const afterSubmit = () => {
    cancelForm()
    listRef.value.onRefresh()
  }

</script>

<style scoped lang="scss">
.item-group {
  display: flex;
  align-items: center
}

.del-btn {
  white-space: nowrap
}

.add-btn {
  padding-top: 15px
}
</style>