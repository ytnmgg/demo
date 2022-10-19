import { JSEncrypt } from "jsencrypt"

// 加密
export const encrypt = (publicKey: string, txt: string) => {
    const encryptor = new JSEncrypt()
    encryptor.setPublicKey(publicKey)
    return encryptor.encrypt(txt)
}
