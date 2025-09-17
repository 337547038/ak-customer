/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, any>
  export default component
}

declare module 'virtual:generated-pages' {
  const routesList: any[]
  export default routesList
}

declare module '@vitejs/plugin-vue'
declare module '@vitejs/plugin-vue-jsx'
/*
declare module 'vuex' {
  type StoreStateType = typeof store.state
  type ModulesType = {
    common: typeof common.state
  }
  export function useStore<S = StoreStateType & ModulesType>(): Store<S>
}
*/
