export const userTypes = {
    "00": "管理员",
    "01": "普通用户",
    default: "未知"
}

export const transferUserType = (raw: string): string => {
    return userTypes[raw as keyof typeof userTypes] || userTypes['default'];
};

export const userStatus = {
    "0": "正常",
    "1": "停用",
    default: "未知"
}

export const transferUserStatus = (raw: string): string => {
    return userStatus[raw as keyof typeof userStatus] || userStatus['default'];
};

export const transferUserStatusBool = (raw: string): boolean => {
    if (raw == '0') {
        return true;
    } else {
        return false;
    }
};

export const userSex = {
    "0": "男",
    "1": "女",
    default: "未知"
}

export const transferUserSex = (raw: string): string => {
    return userSex[raw as keyof typeof userSex] || userSex['default'];
};