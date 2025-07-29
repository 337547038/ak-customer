export default [
    {
        title: '首页',
        path: '/',
        icon: 'HomeFilled'
    },
    {
        title: '数据分析',
        menuIndex: 'analysis',
        icon: '',
        children: [
            {
                title: '员工业绩',
                path: '/analysis/1',
            },
            {
                title: '客户级别',
                path: '/analysis/2',
            },
            {
                title: '客户来源',
                path: '/analysis/3',
            },
            {
                title: '客户行业',
                path: '/analysis/4',
            },
            {
                title: '员工客户量',
                path: '/analysis/5',
            },
            {
                title: '跟进分析',
                path: '/analysis/061',
            },
            {
                title: '合同排行',
                path: '/analysis/16',
            }
        ]
    },
    {
        title: '客户管理',
        icon: 'user',
        menuIndex: "customer",
        children: [
            {
                title: '客户查重',
                path: '/customer/check',
                icon: "Search"
            },
            {
                title: '客户列表',
                path: '/customer/list',
                icon: "List"
            },
            {
                title: '联系人',
                path: '/customer/contact',
                icon: ""
            },
            {
                title: '跟进记录',
                path: '/customer/follow',
                icon: "Tickets"
            },
            {
                title: '公海客户',
                path: '/customer/list-comm',
                icon: "Baseball"
            },
            {
                title: '无效客户',
                path: '/customer/list-invalid',
                icon: "Filter"
            },
            {
                title: '名片识别',
                path: '/customer/ocr',
                icon: "Aim"
            },
            {
                title: '删除客户',
                path: 'delCustomer',
                type: 'btn'
            }
        ]
    },
    {
        title: '系统管理',
        icon: 'setting',
        menuIndex: "system",
        children: [
            {
                title: '用户管理',
                path: '/system/user',
                icon: "UserFilled"
            },
            {
                title: '角色管理',
                path: '/system/role',
                icon: "CircleCheck"
            },
            {
                title: '部门管理',
                path: '/system/dept',
                icon: "OfficeBuilding"
            },
            {
                title: '登录日志',
                path: '/system/log',
                icon: "Notebook"
            },
            {
                title: '字典管理',
                path: '/system/dict',
                icon: "Memo"
            }
        ]
    }
]