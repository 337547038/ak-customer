import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import Pages from 'vite-plugin-pages'
import { fileURLToPath, URL } from 'url'
//import * as path from 'path'
//import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        //vueDevTools(),
        vue(),
        vueJsx({}),
        Pages({
            dirs: [
                {dir: 'src/views', baseRoute: ''}
            ],
            extensions: ['vue'],
            exclude: ['**/components/*.vue']
        })
    ],
    resolve: {
        alias: {
           // '@': path.resolve(__dirname, './src')
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
        // 使用路径别名时想要省略的后缀名，官方不建议将.vue文件省略后缀
        extensions: ['.js', '.ts']
    },
    base: './',
    build: {
        outDir: '../docs',
        /*rollupOptions: {
            external: ['echarts'], // 排除 echarts
            output: {
                globals: {
                    echarts: 'echarts' // 指定全局变量名
                }
            }
        }*/
    },
    server: {
        // port: 3000,
        host: '0.0.0.0',
        open: false,
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true
            }
        }
    }
})
