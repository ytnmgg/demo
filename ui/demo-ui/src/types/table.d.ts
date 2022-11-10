declare interface TableObject<T = any> {
    pageSize: number
    currentPage: number
    total: number
    tableList: T[]
    params: any
    loading: boolean
    exportLoading: boolean
    currentRow: Nullable<T>
}