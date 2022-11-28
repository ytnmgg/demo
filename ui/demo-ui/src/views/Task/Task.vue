<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch, unref } from "vue";
import {
  ElButton,
  ElTooltip,
  ElTable,
  ElTableColumn,
  ElConfigProvider,
  ElTag,
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

import * as TaskApi from "@/api/task";
import type { JobInfo } from "@/api/task/types";

const locale = ref(zhCn);

const tableObject = reactive<TableObject<JobInfo>>({
  // 页数
  pageSize: 0,
  // 当前页
  currentPage: 0,
  // 总条数
  total: 0,
  // 表格数据
  tableList: [],
  // AxiosConfig 配置
  params: {},
  // 加载中
  loading: true,
  // 导出加载中
  exportLoading: false,
  // 当前行的数据
  currentRow: null,
});

const paramsObj = computed(() => {
  return {
    pageSize: tableObject.pageSize,
    pageIndex: tableObject.currentPage,
  };
});

watch(
  () => tableObject.currentPage,
  () => {
    methods.getList();
  }
);

watch(
  () => tableObject.pageSize,
  () => {
    // 当前页不为1时，修改页数后会导致多次调用getList方法
    if (tableObject.currentPage === 1) {
      methods.getList();
    } else {
      tableObject.currentPage = 1;
      methods.getList();
    }
  }
);

const methods = {
  getList: async () => {
    tableObject.loading = true;

    const res = await TaskApi.listTask(unref(paramsObj)).finally(() => {
      tableObject.loading = false;
    });

    if (res) {
      tableObject.tableList = res;
    }
  },
};

const tableRef = ref();

onMounted(async () => {
  methods.getList();
});

const refreshList = () => {
  methods.getList();
};

const addDialogFormVisible = ref(false);
const addForm = reactive({
  permissionName: "",
  permissionKey: "",
});

const addConfirmLoading = ref(false);
</script>

<template>
  <div class="m-10">
    <el-config-provider :locale="locale">
      <div class="flex items-center justify-between">
        <div>
          <!-- <el-button plain type="primary" @click="addDialogFormVisible = true">
            <i-ep-circle-plus class="mr-5px" />新增</el-button
          > -->
        </div>

        <div>
          <el-tooltip class="box-item" effect="dark" content="刷新" placement="top-end">
            <el-button circle @click="refreshList">
              <i-ep-refresh />
            </el-button>
          </el-tooltip>
          <el-tooltip class="box-item" effect="dark" content="列配置" placement="top-end">
            <el-button circle>
              <i-ep-menu />
            </el-button>
          </el-tooltip>
        </div>
      </div>
      <div>
        <div>
          <el-table
            :data="tableObject.tableList"
            v-loading="tableObject.loading"
            style="width: 100%"
            ref="tableRef"
          >
            <el-table-column prop="schedulerName" label="Scheduler" width="200" />
            <el-table-column prop="jobName" label="任务名称" width="200" />
            <el-table-column prop="jobGroup" label="任务组" width="200" />
            <el-table-column prop="startTime" label="开始时间" />
            <el-table-column prop="prevFireTime" label="上次触发时间" />
            <el-table-column prop="nextFireTime" label="下次触发时间" />
            <el-table-column prop="triggerState" label="触发器状态">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <div v-if="scope.row.triggerState === 'NORMAL'">
                    <el-tag type="success"> {{ scope.row.triggerState }}</el-tag>
                  </div>

                  <div v-else>
                    <el-tag type="info"> {{ scope.row.triggerState }}</el-tag>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="cronExpression" label="Cron表达式" />
          </el-table>
        </div>
      </div>
    </el-config-provider>
  </div>
</template>

<style></style>
