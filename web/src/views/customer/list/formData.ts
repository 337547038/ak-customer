import validate from "@/components/form/validate";
import {markRaw} from "vue";
import ProvincesCity from "@/components/provincesCity/index.vue";

export default [
    {
        prop: 'company',
        label: '客户名称',
        formItem: {
            rules: [validate('required')]
        }
    },
    {
        prop: 'brandName',
        label: '品牌名称',
    },
    {
        prop: 'type',
        label: '合作类型',
        render: 'select',
        options: 'cooperationType'
    },
    {
        prop: 'industry',
        label: '行业分类',
        render: 'select',
        options: 'industryType'
    },
    {
        prop: 'code',
        label: '社会信用代码',
    },
    {
        prop: 'source',
        label: '客户来源',
        render: 'select',
        options: 'source'
    },
    {
        prop: 'tel',
        label: '公司电话',
        formItem: {
            rules: [validate('tel')]
        }
    },
    {
        prop: 'web',
        label: '网站',
        formItem: {
            rules: [validate('url')]
        }
    },
    {
        prop: 'area',
        label: '所属区域',
        render: 'component',
        component: markRaw(ProvincesCity)
    },
    {
        prop: 'address',
        label: '详细地址',
    },
    {
        prop: 'creatTime',
        label: '创建时间',
        attr:{
            disabled: true
        },
        visible: 'isDetailForm'
    },
    {
        prop: 'updateTime',
        label: '更新时间',
        attr:{
            disabled: true
        },
        visible: 'isDetailForm'
    },
    {
        prop: 'remark',
        label:'备注',
        attr:{
            type: 'textarea',
            rows: 3,
        }
    },
    {
        prop: 'files',
        label: '相关附件',
        render:'scope'
    },
]