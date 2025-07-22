import axios from 'axios'
import { useLayoutStore } from '@/store/layout'
import {getStorage} from "@/utils/index";
import {ElMessage} from "element-plus";
import {refreshToken as refreshTokenApi} from "@/api";
// 全局设置
/* axios.defaults.baseURL = window.APP_CONFIG.BASE_URL
axios.defaults.headers.common['Authorization'] = getToken() */

const service = axios.create({
  //baseURL: import.meta.env.VITE_APP_URL, // api的base_url
  //timeout: 6000, // request timeout
  headers: {}
})
/** *************************4.无感刷新换token相关****************/
let refreshTokenAjax: boolean = false
// 存储请求的数组
const subscribesArr: ((newToken: any) => void)[] = []

// 请求push到数组中
function subscribesArrRefresh(cb: (newToken: any) => void) {
  subscribesArr.push(cb)
}

// 用新token发起请求
function reloadSubscribesArr(newToken: any) {
  subscribesArr.map((cb) => cb(newToken))
}

// 使用refreshToken请求获取新的token
function getNewToken(refreshToken: any) {
  const layoutStore = useLayoutStore()
  axios
      .post(refreshTokenApi, { refreshToken: refreshToken })
      .then((result: any) => {
        console.log(result)
        if (result.data.code === 1) {
          const data = result.data.data
          // 统一方法保存保存token，和登录时一致
          layoutStore.setLoginInfo(data)
          reloadSubscribesArr(data.token)
          refreshTokenAjax = false
        } else {
          layoutStore.logout()
          refreshTokenAjax = false
        }
      })
      .catch(() => {
        // console.log('换取token失败,直接退出')
        layoutStore.logout()
        refreshTokenAjax = false
      })
}

/** *************************4.无感刷新换token相关结束****************/
service.interceptors.request.use(
  (config) => {
      const token: any = getStorage('token', true)
      if (token) {
          config.headers['Authorization'] = token
      }
      // 4. 无感刷新token开始
      const refreshToken = getStorage('refreshToken', true)
      if (!token && refreshToken) {
          if (!refreshTokenAjax) {
              getNewToken(refreshToken)
          }
          refreshTokenAjax = true
          return new Promise((resolve) => {
              subscribesArrRefresh((newToken) => {
                  config.headers['Authorization'] = newToken
                  resolve(config)
              })
          })
      }
      // 无感刷新token结束
    return config
  },
  (error) => {
    Promise.reject(error)
  }
)

service.interceptors.response.use(
  (res) => {
      const code: number = res.data.code
      const msg: string = res.data.message
      // 二进制数据则直接返回，返回时保存的文件名可从res.headers['content-disposition']获取
      if (['blob', 'arraybuffer'].includes(res.request.responseType)) {
          return res
      }
      switch (code) {
          case 1:
              return res.data
          case 401:
              // 这里可以直接跳到登录页
              // return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
              ElMessage({ message: msg, type: 'error' })
              // todo 这里使用了router后在开发热更新时会导致页面刷新
              // router.push({ path: '/login' })
              window.location.href = '/#/login' // 根据路由模式决定是否添加/#
              break
          default:
              // 这里可统一处理其他异常拦截，或提示
              if (msg) {
                  ElMessage({ message: msg, type: 'error' })
              }
              return Promise.reject(res.data)
      }
  },
  (error) => {
      const msg = error.response?.data.message || error.message
      ElMessage({ message: msg, type: 'error' })
    return Promise.reject(error)
  }
)
export default service
