export type SearchRequest = {
    pageIndex: number,
    pageSize: number,
    from: string,
    to: string,
    query: string,
    fieldKey: string,
    timestampKey: string,
    logType: string,
}
