import ProvincesJson from '@/assets/json/provinces.json'
import CityJson from '@/assets/json/cities.json'
import AreaJson from '@/assets/json/areas.json'

const getCityByCode = (code: string[] | string) => {
    let cityCode = code
    if (code && typeof code === 'string') {
        cityCode = code.split(',')
    }
    const temp = []
    if (cityCode && cityCode.length === 3) {
        if (containsChinese(cityCode.join(','))) {
            // 包含中文时，直接返回中文
            return cityCode.join('')
        }
        const pro = ProvincesJson.find(item => item.code === cityCode[0]);
        pro && temp.push(pro?.name)
        const city = CityJson.find(item => item.code === cityCode[1]);
        city && temp.push(city?.name)
        const area = AreaJson.find(item => item.code === cityCode[2]);
        area && temp.push(area?.name)
        return temp?.join('')?.replace('市辖区', '');
    } else {
        return ''
    }
}

function containsChinese(str) {
    return /[\u4e00-\u9fa5]/.test(str);
}

export default getCityByCode