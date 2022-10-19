import { store } from '../index'
import { Names } from '../store-names'
import { defineStore } from 'pinia'
import { getAccessToken, removeToken } from '@/utils/auth'
import { useCache } from '@/hooks/web/useCache'


const { wsCache } = useCache()

interface UserVO {
    id: number
    avatar: string
    nickname: string
}
interface UserInfoVO {
    permissions: string[]
    roles: string[]
    user: UserVO
}

export const useUserStore = defineStore({
    id: Names.USER,
    state: (): UserInfoVO => ({
        permissions: [],
        roles: [],
        user: {
            id: 0,
            avatar: '',
            nickname: ''
        }
    }),
    getters: {
        getPermissions(): string[] {
            return this.permissions
        },
        getRoles(): string[] {
            return this.roles
        },
        getUser(): UserVO {
            return this.user
        }
    },
    actions: {
        async setUserInfoAction(userInfo: UserInfoVO) {
            if (!getAccessToken()) {
                // this.resetState()
                this.$reset()
                return null
            }
            this.permissions = userInfo.permissions
            this.roles = userInfo.roles
            this.user = userInfo.user
            wsCache.set('user', userInfo)
        },
        loginOut() {
            removeToken()
            wsCache.clear()
            // this.resetState()
            this.$reset()
        }
        // 不用手写reset，试试自带方法 $reset()
        // resetState() {
        //     this.permissions = []
        //     this.roles = []
        //     this.user = {
        //         id: 0,
        //         avatar: '',
        //         nickname: ''
        //     }
        // }
    }
})

export const useUserStoreWithOut = () => {
    return useUserStore(store)
}
