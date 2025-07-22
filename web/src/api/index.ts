import request from '../utils/request'
import sys from './sys'
import customer from './customer'


const allApi: any = Object.assign({}, sys, customer)
const apiPrefix: string = 'api/'
export const getRequest = (apiKey: string, data?: any, options = {}) => {
    let url = allApi[apiKey] || apiKey
    // 不是以/和http开头的，添加全局前缀
    if (!(url.startsWith('/') || url.startsWith('http'))) {
        url = apiPrefix + url
    }
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
export const refreshToken = apiPrefix + 'system/user/refreshToken'