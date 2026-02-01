/**
 * 根据正则判断是否为必填
 * @param rules
 */
const getRequired = (rules: any) => {
    if (rules && rules.length) {
        return rules.some(item => item.required === true)
    } else {
        return false
    }
}
export {getRequired}