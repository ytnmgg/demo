
// 必填项
export const required = {
    required: true,
    message: "该项为必填项",
    trigger: 'blur'
}

export const lengthValidate = (min: number, max: number) => {
    return {
        min,
        max,
        message: `长度应该在${min}~${max}之间`,
        trigger: 'blur'
    }
}
