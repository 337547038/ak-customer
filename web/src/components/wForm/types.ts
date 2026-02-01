import type {FieldProps, FieldRule} from "vant";

export interface FormData {
    prop: string
    render?: string
    label: string
    visible?: boolean | (() => boolean)
    component?: any // 仅render=component
    modelValue?: any //初始值
    field?: FieldProps | any
    placeholder?: string
    rules?: FieldRule[] | undefined
    options?: string | { [key: string]: any }[] | { [key: string]: any } // 仅render=picker
    picker?:{ [key: string]: any } // 仅render=picker
    cascader?:{ [key: string]: any } // 仅render=cascader
}