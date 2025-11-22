import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 关键：导入中文语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(ElementPlus, { locale: zhCn })

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
