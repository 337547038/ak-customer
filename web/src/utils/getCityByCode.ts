import ProvincesJson from '@/assets/json/provinces.json'
import CityJson from '@/assets/json/cities.json'
import AreaJson from '@/assets/json/areas.json'

const getCityByCode = (code: string[] | string) => {
    let cityCode = code
    if (code && typeof code === 'string') {
        cityCode = code.split(',')
    }
    const temp = []
    if (cityCode && (cityCode.length === 2 || cityCode.length === 3)) {
        if (containsChinese(cityCode.join(','))) {
            // 包含中文时，直接返回中文
            return cityCode.join('')
        }
        const pro = getCityBySingleCode(cityCode[0], 'p')
        pro && temp.push(pro)
        if (cityCode.length === 2) {
            const area = getCityBySingleCode(cityCode[1], 'a')
            area && temp.push(area)
        } else {
            const city = getCityBySingleCode(cityCode[1], 'c')
            city && temp.push(city)
            const area = getCityBySingleCode(cityCode[2], 'a')
            area && temp.push(area)
        }
        return temp?.join('')

    } else {
        return ''
    }
}
export const getCityBySingleCode = (code: string, type: string) => {
    if (containsChinese(code)) {
        return code
    }
    if (type === "p") {
        //省份
        const pro = ProvincesJson.find(item => item.code === code)
        return pro && pro?.name
    } else if (type === "c") {
        //城市
        const city = CityJson.find(item => item.code === code)
        return city && city?.name
    } else {
        // 区
        const area = AreaJson.find(item => item.code === code)
        return area && area?.name
    }
}
/*export const getCodeByName = (name: string, type: string) => {
    if (!containsChinese(name)) {
        return name
    }
    if (type === "p") {
        //省份
        const pro = ProvincesJson.find(item => item.name === name)
        return pro && pro?.name
    } else if (type === "c") {
        //城市
        const city = CityJson.find(item => item.name === name)
        return city && city?.name
    } else {
        // 区
        const area = AreaJson.find(item => item.name === name)
        return area && area?.name
    }
}*/

function containsChinese(str) {
    return /[\u4e00-\u9fa5]/.test(str);
}

export default getCityByCode