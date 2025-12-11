const wxLogin = (urlPath = 'login') => {
    const appid = 'wx28c54e66df42d7d1'
    const redirect_uri = encodeURIComponent(`http://192.168.10.9/#/${urlPath}`)
    console.log(redirect_uri)
    //const redirect_uri = 'https%3A%2F%2Fpassport.yhd.com%2Fwechat%2Fcallback.do'
    const state = Math.random().toString(36).substr(2, 10) // 先加个随机数
    window.location.href = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appid}&redirect_uri=${redirect_uri}&response_type=code&scope=snsapi_userinfo&state=${state}#wechat_redirect`
}
export default wxLogin