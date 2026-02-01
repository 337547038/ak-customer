import {markRaw} from "vue";
import ProvincesCity from "@/components/provincesCity/index.vue";
import getCityByCode from "@/utils/getCityByCode";

export const columnUser = {
    label: '所属人员',
    prop: 'userId',
    width: 100,
    search: {
        changeRefresh: true,
        style: {width: '230px'},
        render: 'select',
        ajax: {
            api: 'userChildList',
            data: {},
            label: 'userName',
            value: 'id'
        },
        clearable: true
    },
    formatter: (row: any) => {
        return row.userName
    }
}

export default [
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
        label: '客户名称',
        width: 240,
        showOverflowTooltip: true
    },
    {
        prop: 'type',
        label: '合作类型',
        width: 150,
        render: 'tag',
        replaceValue: 'cooperationType',
        search: {
            render: 'select',
            options: 'cooperationType'
        }
    },
    {
        prop: 'area',
        label: '所属区域',
        search: {
            render: 'component',
            component: markRaw(ProvincesCity),
            clearable:true
        },
        formatter: (row: any, column: any, cellValue: any) => {
            return getCityByCode(cellValue)
        },
        width: 200,
        showOverflowTooltip: true
    },
    {
        prop: 'industry',
        label: '行业分类',
        width: 150,
        render: 'tag',
        replaceValue: 'industryType',
        search: {
            render: 'select',
            options: 'industryType'
        }
    },
    {
        prop: 'source',
        label: '来源',
        width: 140,
        render: 'tag',
        replaceValue: 'source',
        search: {
            render: 'select',
            options: 'source'
        }
    },
    {
        prop: 'lastTime',
        label: '最近联系时间',
        width: 150,
        showOverflowTooltip: true,
        render: 'datetime',
        search: false
    },
    {
        prop: 'nextTime',
        label: '下次联系时间',
        render: 'datetime',
        search: false,
        width: 150,
        showOverflowTooltip: true
    },
    {
        prop: 'creatTime',
        label: '创建时间',
        render: 'datetime',
        search: false,
        width: 150,
        showOverflowTooltip: true
    },
    {
        prop: 'updateTime',
        label: '更新时间',
        width: 150,
        showOverflowTooltip: true,
        render: 'datetime',
        search: false
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
                }
            }
        ]
    }
]