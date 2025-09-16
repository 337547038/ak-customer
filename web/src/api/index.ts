import request from '../utils/request'
import sys from './sys'
import customer from './customer'
import analysis from './analysis'
import contract from './contract'


const allApi: any = Object.assign({}, sys, customer, analysis, contract)
let apiPrefix: string = 'api/'
const host: string = window.location.host
let localhost = false
if (host.indexOf('localhost') !== -1 || host.indexOf('github') !== -1) {
    apiPrefix = 'mock/'
    localhost = true
}

export const getRequest = (apiKey: string, data?: any, options = {}) => {
    let url = allApi[apiKey] || apiKey
    // 不是以/和http开头的，添加全局前缀
    // 还有个特殊的地图.json，以echarts开头
    if (!(url.startsWith('/') || url.startsWith('http') || url.startsWith('echarts'))) {
        url = apiPrefix + url
    }
    if (localhost) {
        let id = data?.id || ''
        let suffix = url.indexOf('.json') !== -1 ? '' : '.json'
        return request(Object.assign(
            {
                url: url + id + suffix,
                method: 'GET',
                data
            },
            options
        ))
    } else {
        const obj: any = Object.assign(
            {
                url: url,
                method: 'POST',
                data
            },
            options
        )
        return request(obj)
    }
}
export const refreshToken = apiPrefix + 'system/user/refreshToken'