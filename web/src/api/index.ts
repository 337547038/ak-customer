import request from '../utils/request'
import sys from './sys'

const allApi: any = Object.assign({},sys)
export const getRequest = (apiKey: string, data?: any, options = {}) => {
    let url = allApi[apiKey] || apiKey
    // 不是以/和http开头的，添加全局前缀
    if (!(url.startsWith('/') || url.startsWith('http'))) {
        url = 'api/' + url
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
