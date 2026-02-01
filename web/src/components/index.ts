import tableList from './list/index.vue'
import field from './field/index.vue'
import form from './form/index.vue'
import icon from './icon/index.vue'
import wList from './wList/index.vue'
import wForm from './wForm/index.vue'

export default (app: any) => {
  app.component('AkForm', form)
  app.component('AkList', tableList)
  app.component('AkField', field)
  app.component('AkIcon', icon)
  app.component('WList', wList)
  app.component('WForm', wForm)
}
