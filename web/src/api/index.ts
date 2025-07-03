import request from '../utils/request'

//const isDev = import.meta.env.DEV

const api: any = {
  // tableList: '/get/table/list',
  formSubmit: '/formSubmit',
  tableList: './mock/list.json',
  getFormContent: './mock/form.json'
}
export const getRequest = (apiKey: string, data?: any, options = {}) => {
  const obj: any = Object.assign(
    {
      url: api[apiKey] || apiKey,
      method: 'get',
      data
    },
    options
  )
  return request(obj)
}
