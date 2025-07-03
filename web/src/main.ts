import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import AKFormList from './components/index'
// 实例化 Pinia
const pinia = createPinia()
const app = createApp(App)
app.use(pinia).use(ElementPlus).use(router).use(AKFormList).mount('#app')
// 全局注册icon
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
