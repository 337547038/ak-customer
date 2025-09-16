<template>
  <div style="position: relative" v-loading="loading">
    <userSelect @change="userChange"></userSelect>
    <div class="back-btn">
      <el-button type="primary" text v-show="!isChinaMap" @click="backClick">返回中国地图</el-button>
    </div>
    <myEcharts ref="myEchartsRef" @click="mapClick"/>
  </div>
</template>

<script setup lang="ts">
  import userSelect from './components/childUser.vue'
  import myEcharts from '@/components/echarts/index.vue'

  import * as echarts from 'echarts';

  import {ref, onMounted, computed} from 'vue'
  import {getRequest} from "@/api";

  import {getCityBySingleCode} from "@/utils/getCityByCode";


  const loading = ref(false);
  const myEchartsRef = ref()
  const isChinaMap = ref(true)
  const currentCityCode = ref()
  const registerName = ref()
  const seriesData = ref([])
  const maxValue = ref(0)
  const result = ref([])
  const option = ref({
    backgroundColor: '#FFFFFF',
    tooltip: {
      trigger: 'item'
    },
    visualMap: {
      type: 'continuous',
      show: true,
      min: 0,
      max: maxValue,
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      calculable: true,
      inRange: {
        color: ['#FFFFFF', '#0099FF']
      }
    },

    //配置属性
    series: [{
      name: '用户数',
      type: 'map',
      map: registerName,
      roam: true,
      label: {
        normal: {
          show: true  //省份名称
        },
        emphasis: {
          show: false
        }
      },
      data: seriesData  //数据
    }]
  })
  const backClick = () => {
    isChinaMap.value = true
    getMapJson('china', true)
  }
  const userChange = (userId: number) => {
    getData(userId)
  }

  const getMapJson = (map?: string, city?: boolean) => {
    const path = map ? map : 'china'
    registerName.value = path
    getRequest(`echarts/map/${path}.json`)
        .then(() => {
          // 全局拦截了只有code＝1才进这里
        })
        .catch((res) => {
          echarts.registerMap(path, res)
          if (!city) { // 地图下钻时true，不加载数据
            getData()
          } else {
            formatData(result.value)
          }
        })
  }

  const chineseToCode = (city: string) => {
    // 将中文转拼音。地图点击获取到的是中文，要转为拼音去加载相对应的js
    const area = {
      '北京': 'beijing',
      '广东': 'guangdong',
      '湖南': 'hunan',
      '江苏': 'jiangsu',
      '浙江': 'zhejiang',
      '云南': 'yunnan',
      '西藏': 'xizang',
      '新疆': 'xinjiang',
      '四川': 'sichuan',
      '陕西': 'shanxi1',
      '山西': 'shanxi',
      '上海': 'shanghai',
      '山东': 'shandong',
      '青海': 'qinghai',
      '宁夏': 'ningxia',
      '内蒙古': 'neimenggu',
      '辽宁': 'liaoning',
      '吉林': 'jilin',
      '江西': 'jiangxi',
      '湖北': 'hubei',
      '河南': 'henan',
      '黑龙江': 'heilongjiang',
      '河北': 'hebei',
      '海南': 'hainan',
      '贵州': 'guizhou',
      '广西': 'guangxi',
      '甘肃': 'gansu',
      '福建': 'fujian',
      '重庆': 'chongqing',
      '天津': 'tianjin',
      '安徽': 'anhui',
      '台湾': 'taiwan',
      '香港': 'xianggang',
      '澳门': 'aomen'
    }
    return area[city]
  }
  const mapClick = (params: any) => {
    if (params.componentType === 'series' && params.seriesType === 'map') {
      const provinceName = chineseToCode(params.name)
      if (provinceName) {
        //　匹配不到拼音时，表示点击了省份，则不继续下钻
        isChinaMap.value = false
        currentCityCode.value = params.data?.code
        getMapJson(provinceName, true)
      }
    }
  }
  const formatData = (data: any) => {
    if (!isChinaMap.value) {
      // 不是中国地图时，先取出当前省份的数据
      data = data.filter(item => {
        const parts = item.area?.split(',')
        return parts?.[0] === currentCityCode.value
      })
    }
    // 北京上海天津重庆市
    const specialCity = ['11', '12', '31', '50', '81', '82'].includes(currentCityCode.value)
    const obj = {}
    data.forEach((item) => {
      if (item.area) {
        const code = item.area.split(',')
        if (code && (code.length === 3 || code.length === 2)) {
          // 当前显示为中国地图时取0
          let cityCode = isChinaMap.value ? code[0] : code[1]
          if (!obj[cityCode]) {
            obj[cityCode] = 0;
          }
          // 计数
          obj[cityCode]++;
        }
      }
    })
    const city = []
    let max = 0
    if (Object.keys(obj).length > 0) {
      for (const key in obj) {
        let name = getCityBySingleCode(key, isChinaMap.value ? "p" : specialCity ? "a" : "c")

        if (isChinaMap.value) {
          // 在中国地图时要过滤市，如北京市=>北京
          name = name?.replace('市', '')
        }
        city.push({name: name?.replace("省", ""), value: obj[key], code: key})
        if (obj[key] > max) {
          max = obj[key]
        }
      }
    }

    seriesData.value = city
    maxValue.value = max
    myEchartsRef.value.setOption(option.value)

  }
  const getData = (userId?: number) => {
    getRequest("analysisCustomer", {userId})
        .then(res => {
          result.value = res.data?.list
          formatData(res.data?.list)
        })
  }
  onMounted(() => {
    getMapJson()
  })
</script>
<style scoped lang="scss">
.back-btn {
  position: absolute;
  z-index: 1
}
</style>