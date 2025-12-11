import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import AKFormList from './components/index'
import './assets/scss/style.scss'
import permission from '@/directive'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'amfe-flexible'
import useVant from './components/vantComponents'
// 实例化 Pinia
const pinia = createPinia()
const app = createApp(App)
app.use(pinia).use(ElementPlus,{locale: zhCn}).use(router).use(AKFormList).use(permission).use(useVant).mount('#app')