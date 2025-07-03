export const dateFormatting = (time: any, cFormat?: string) => {
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  // 字符串数字形式的时间戳要转换下
  let newTime = time
  if (/^\d+?$/.test(time)) {
    newTime = parseInt(time)
  }
  const date = typeof time === 'object' ? time : new Date(newTime)
  const formatObj: any = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const timeStr = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    if (key === 'a')
      return ['一', '二', '三', '四', '五', '六', '日'][value - 1]
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return timeStr
}
// 动态远程加载script脚本
export function loadScript(src: string) {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.onload = resolve
    script.onerror = reject
    script.src = src
    document.head.appendChild(script)
  })
}

/**
 * 将数字转千分位表示
 * */
export function parseToThousandth(number: number) {
  return (number + '').replace(/(\d)(?=(\d{3})+(\.|$))/g, '$1,')
}

// 过滤前后空格
export function replaceSpace(text: string) {
  return text.replace(/(^\s*)|(\s*$)/g, '') // 过滤掉前后空格
}


/**
 * 设置 localStorage 添加对时间的控制，hour单位为小时
 * @param key 保存在storage的key
 * @param data 需存储的数据
 * @param hour null时存sessionStorage(key,value)，即关闭浏览器过期
 * hour=0时，使用localStorage，即永不过期
 * hour>0时localStorage添加时间控制
 */

export function setStorage(key: string, data: any, hour?: number | null): void {
  let newData = data
  if (typeof data === 'object') {
    newData = JSON.stringify(data)
  }
  /* if (!data) {
    return
  } */
  if (hour === 0) {
    window.localStorage.setItem(key, newData)
  } else if (hour && hour > 0) {
    const now = new Date()
    const valueDate: string = JSON.stringify({
      __value: data,
      __time: now.setSeconds(now.getSeconds() + hour * 3600)
    })
    window.localStorage.setItem(key, valueDate)
  } else {
    window.sessionStorage.setItem(key, newData)
  }
}

/**
 * 获取storage
 * @param key 保存时的key
 * @param hour 如果保存时使用了时间，则需要传true。false在sessionStorage里取
 * @return 返回保存的值，过期后返回false,其他异常或不存在返回undefined
 */
export const getStorage = (key: string, hour?: boolean) => {
  let data: any
  if (hour) {
    data = window.localStorage.getItem(key)
    try {
      data = JSON.parse(data)
      if (typeof data === 'object' && data.__time) {
        if (!data.__value) {
          data = undefined
        }
        // 使用了时间的
        // 在当前时间后，表示没过期
        if (new Date().getTime() < data.__time) {
          data = data.__value
        } else {
          // 过期了
          data = false
        }
      }
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (e) {
      data = undefined
    }
  } else {
    // 保存时没传时间的，存在session里
    data = window.sessionStorage.getItem(key)
  }
  try {
    return JSON.parse(data)
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (e) {
    return data
  }
}
/**
 * 移除storage
 * @param key 要移除的key
 * @param hour set时使用了hour，移除时则传true
 */
export const removeStorage = (key: string, hour?: boolean): void => {
  if (hour) {
    window.localStorage.removeItem(key)
  } else {
    window.sessionStorage.removeItem(key)
  }
}