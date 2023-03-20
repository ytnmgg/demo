<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch, unref } from "vue";
import moment from 'moment';

import {
  ElButton,
  ElTooltip,
  ElTable,
  ElTableColumn,
  ElConfigProvider,
  ElDatePicker,
  ElInput,
  ElTag,
  ElSwitch,
} from "element-plus";
import * as EsApi from "@/api/es";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

moment.locale('zh-cn');

const locale = ref(zhCn);

const shortcuts = [
  {
    text: '1分钟',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 60 * 1000)
      return [start, end]
    },
  },
  {
    text: '5分钟',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 5 * 60 * 1000)
      return [start, end]
    },
  },
  {
    text: '1小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000)
      return [start, end]
    },
  },
  {
    text: '今天',
    value: () => {
      const end = new Date()
      const todayStartTime = new Date(new Date().setHours(0, 0, 0, 0))
      return [todayStartTime, end]
    },
  },
  {
    text: '1周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: '1月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: '3月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]

const searchText = ref('');
const xhrOnly = ref(true);

const searchParam = reactive<any>({
  timeRange: [],
  data: {
    pageIndex: 1,
    pageSize: 10,
    from: "1677067750",
    to: "1677067751",
    query: searchText,
    fieldKey: "info",
    timestampKey: "info.msec.keyword",
    logType: "info-log-fs"
  }
});

const tableData = reactive<any>({
  total: 0,
  rows: [],
  // 加载中
  loading: true,
});

watch(
  () => searchParam.timeRange,
  (newValue, oldValue) => {
    var start = moment.parseZone(newValue[0]);
    var end = moment.parseZone(newValue[1]);
    searchParam.data.from = start.format('X')
    searchParam.data.to = end.format('X')
    refreshList();
  }
);

watch(
  () => searchParam.data.pageIndex,
  () => {
    refreshList();
  }
);

watch(
  () => searchParam.data.pageSize,
  () => {
    // 当前页不为1时，修改页数后会导致多次调用getList方法
    if (searchParam.data.pageIndex === 1) {
      refreshList();
    } else {
      searchParam.data.pageIndex = 1;
      refreshList();
    }
  }
);

const methods = {
  search: async () => {
    tableData.loading = true;

    var finalParam = { ...searchParam.data }
    if (unref(xhrOnly)) {
      finalParam['wildcardField'] = 'content.uri';
      finalParam['wildcardValue'] = '*.json';
    }

    const res = await EsApi.search(unref(finalParam)).finally(() => {
      tableData.loading = false;
    });

    tableData.rows = [];
    tableData.total = 0;

    if (res && res.total > 0) {

      // 返回的对象内部字段进行排序 及 其它处理
      const apiData: { [x: string]: any }[] = []
      res.data.forEach((row: { [x: string]: string }) => {
        var sortedRow: { [x: string]: string } = {}
        Object.keys(row).sort().map(key => {
          sortedRow[key] = row[key]
        });

        var time_local = sortedRow["time_local"];
        if (time_local != null && time_local.length != 0) {
          time_local = time_local.replace("T", " ");
          time_local = time_local.replace("+08:00", "");
        }
        sortedRow["time_local"] = time_local

        apiData.push(sortedRow)
      });

      tableData.rows = apiData;
      tableData.total = res.total;
    }
  },
};

const tableRef = ref();

onMounted(async () => {

  const end = new Date()
  const start = new Date()
  start.setTime(start.getTime() - 60 * 1000)
  searchParam.timeRange = [start, end]
});


const refreshList = () => {
  methods.search();
};

</script>
<template>
  <div class="m-10">
    <el-config-provider :locale="locale">
      <div class="flex  space-x-4">
        <el-input v-model="searchText" @keyup.enter.native="refreshList">
          <template #prepend>
            <i-ep-avatar />
          </template>
        </el-input>
        <el-switch v-model="xhrOnly" class="ml-2" inline-prompt active-text="XHR" inactive-text="ALL" />
        <el-date-picker class="min-w-380px" v-model="searchParam.timeRange" type="datetimerange" :shortcuts="shortcuts"
          range-separator="To" start-placeholder="开始日期" end-placeholder="结束日期" />

        <el-tooltip class="box-item" effect="dark" content="搜索" placement="top-end">
          <el-button circle @click="refreshList">
            <i-ep-search />
          </el-button>
        </el-tooltip>
        <el-tooltip class="box-item" effect="dark" content="列配置" placement="top-end">
          <el-button circle>
            <i-ep-menu />
          </el-button>
        </el-tooltip>
      </div>
      <div class="flex justify-end mt-20px">
        <el-pagination v-model:currentPage="searchParam.data.pageIndex" v-model:page-size="searchParam.data.pageSize"
          layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" :page-sizes="[5, 10, 20, 50]"
          :default-page-size="5">
        </el-pagination>
      </div>
      <div>
        <div class="h-[calc(100vh-180px)] overflow-y-scroll">
          <el-table :data="tableData.rows" v-loading="tableData.loading" style="width: 100%" ref="tableRef"
            :cell-style="{ 'vertical-align': 'top' }">
            <el-table-column prop="" label="" width="180">
              <template #default="scope">
                <span class="text-small font-bold">{{ scope.row.time_local }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="" label="">
              <template #default="scope">
                <el-tag disable-transitions>{{ scope.row.hostAddress }}</el-tag>
                <div v-for="(value, key) in scope.row">
                  <span class="text-small font-mono">{{ key }}: {{ value }}</span>
                </div>

              </template>
            </el-table-column>

          </el-table>
        </div>
      </div>
    </el-config-provider>
  </div>
</template>

<style></style>
