import type { Permission } from "@/api/permission/types";

export type Role = {
    roleId: string
    roleName?: string
    roleKey?: string
    createTime?: string
    permissions?: Array<Permission>
    permissionIds?: Array<string>
}
