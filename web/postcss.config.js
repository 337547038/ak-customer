/**
 * 配置所有样式路径包含了scss目录的样式文件不转换,vue文件中的样式使用在大写单位px的不转换
 */
export default {
    plugins: {
        // postcss-pxtorem 插件的版本需要 >= 5.0.0
        'postcss-pxtorem': {
            rootValue({file}) {
                return file.indexOf('vant') !== -1 ? 37.5 : 75;
            },
            propList: ['*'],
            selectorBlackList: [], // 排除所有以 "pc-" 开头的类名
            exclude: /(\/scss\/|node_modules)/i, // 忽略 node_modules, scss目录的样式
            //exclude: /(vue&type=style|\/scss\/|node_modules)/i, // 忽略 node_modules, scss目录及.vue文件中style的样式
            /*exclude: (file) => {
                //返回true，文件将被忽略
                //排除scss目录及.vue文件中style的样式
                const bool = file.indexOf('node_modules') !== -1 || file.indexOf('/scss/') !== -1
                console.log('exclude1',file,bool)
                return bool;
            }*/
        },
    },
}

