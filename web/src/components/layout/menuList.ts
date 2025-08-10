export default [
    {
        title: '首页',
        path: '/',
        icon: 'HomeFilled'
    },
    {
        title: '数据分析',
        menuIndex: 'analysis',
        icon: 'icon-analysis1',
        children: [
            {
                icon: 'icon-analysis3',
                title: '客户级别',
                path: '/analysis/type',
            },
            {
                icon: 'icon-source',
                title: '客户来源',
                path: '/analysis/source',
            },
            {
                icon: 'icon-analysis4',
                title: '客户行业',
                path: '/analysis/industry',
            },
            {
                icon: 'icon-area',
                title: '客户区域',
                path: '/analysis/area',
            },
            {
                icon: 'icon-user-num',
                title: '员工客户量',
                path: '/analysis/customer',
            },
            {
                icon: 'icon-follows',
                title: '跟进分析',
                path: '/analysis/follow',
            },
            {
                icon: 'icon-ranking',
                title: '合同排行',
                path: '/analysis/contract',
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
                icon: "icon-check"
            },
            {
                title: '客户列表',
                path: '/customer/list',
                icon: "icon-list"
            },
            {
                title: '联系人',
                path: '/customer/contact',
                icon: "icon-contact"
            },
            {
                title: '跟进记录',
                path: '/customer/follow',
                icon: "icon-follow-record"
            },
            {
                title: '公海客户',
                path: '/customer/list-comm',
                icon: "icon-comm-user"
            },
            {
                title: '无效客户',
                path: '/customer/list-invalid',
                icon: "icon-invalid"
            },
            {
                title: '名片识别',
                path: '/customer/ocr',
                icon: "icon-card"
            },
            {
                title: '删除客户',
                path: 'delCustomer',
                type: 'btn'
            }
        ]
    },
    {
        title: '合同管理',
        icon: 'icon-contract',
        menuIndex: "contract",
        children: [
            {
                title: '我的合同',
                icon: 'icon-contract2',
                path: "/contract/contract"
            },
            {
                title: '我的回款',
                icon: 'icon-payment',
                path: "/contract/payment"
            }
        ]
    },
    {
        title: '系统管理',
        icon: 'icon-config',
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
                icon: "icon-role"
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
            },
            {
                title: '清空缓存',
                path: '/system/cache',
                icon: "Delete"
            }
        ]
    }
]