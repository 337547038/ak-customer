<template>
  <ak-form
    :data="data"
    :api="{ add: '/project/add', edit: '/project/edit' }"
    v-model="model"
    :rules="rules"
  >
    <!--    可通用data数据渲染，还可以通过field组件渲染-->
    <ak-field label="field" v-model="model.field" prop="field" />
    <!--    前面两种方式不能满足时，可通过el-form-item渲染-->
    <el-form-item label="测试" prop="test">
      <el-input v-model="model.test" />
    </el-form-item>
    <template #diy>
      可自定义
    </template>
  </ak-form>
</template>
<script setup lang="ts">
  import { ref, reactive } from 'vue'
  import validate from '@/components/form/validate'
  const model = ref({})
  const rules = reactive({
    field: [{ required: true, message: '请输入', trigger: 'blur' }],
    test: [validate('required')],
    name: [
      {
        required: true,
        message: 'Please input Activity name',
        trigger: 'blur'
      }
    ]
  })
  const data = ref([
    {
      prop: 'name',
      label: '名称',
      formItem: {
        rules: [
          {
            min: 3,
            max: 5,
            message: 'Length should be 3 to 5',
            trigger: 'blur'
          }
        ]
      }
    },
    {
      prop: 'diy',
      type:'scope',
    },
    {
      prop: 'name2',
      label: '名称',
      formItem: {},
      options: [{ label: '选项1', value: '123' }],
      type: 'select'
    }
  ])
</script>
<style scoped lang="scss"></style>
