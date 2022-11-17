<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch, unref } from "vue";
import {
  ElButton,
  ElTooltip,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElConfigProvider,
  ElMessage,
  ElMessageBox,
  ElDialog,
  ElInput,
  ElForm,
  ElFormItem,
  ElDescriptions,
  ElDescriptionsItem,
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

import * as UserApi from "@/api/user";
import type { LoginInfo } from "@/api/user/types";

const locale = ref(zhCn);

const tableObject = reactive<TableObject<LoginInfo>>({
  // 页数
  pageSize: 5,
  // 当前页
  currentPage: 1,
  // 总条数
  total: 10,
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

    const res = await UserApi.getLoginUserPageApi(unref(paramsObj)).finally(() => {
      tableObject.loading = false;
    });

    if (res && res.data) {
      tableObject.tableList = res.data;
      tableObject.total = res.totalCount;
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

const handleDeleteClick = (row: LoginInfo) => {
  //   ElMessageBox.confirm("确定删除当前权限码? 该操作不可逆！", "警告", {
  //     type: "warning",
  //   })
  //     .then(() => {
  //       PermissionApi.deleteById({ permissionId: row.permissionId });
  //       ElMessage.success("删除权限码成功");
  //       refreshList();
  //     })
  //     .catch(() => {
  //       // catch error
  //     });
};
</script>

<template>
  <div class="m-10">
    <el-config-provider :locale="locale">
      <div class="flex items-center justify-end">
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
            <el-table-column type="expand">
              <template #default="props">
                <div class="bg-gray-100 p-10px">
                  <div class="font-medium">TOKEN</div>
                  <div class="break-all mb-10px">
                    {{ props.row.token }}
                  </div>
                  <div class="font-medium">登录时间</div>
                  <div class="mb-10px">{{ props.row.loginTime }}</div>

                  <div class="font-medium">登录IP</div>
                  <div class="mb-10px">{{ props.row.loginIp }}</div>

                  <div class="font-medium">登录客户端信息</div>
                  <div class="break-all">{{ props.row.userAgent }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="userId" label="用户ID" width="200" />
            <el-table-column prop="userName" label="用户名" width="200" />
            <el-table-column prop="loginIp" label="登录IP" width="200" />
            <el-table-column prop="userAgent" label="登录客户端信息">
              <template #default="scope">
                <el-tooltip class="box-item" effect="dark" placement="left">
                  <template #content> {{ scope.row.userAgent }} </template>

                  <div class="truncate">{{ scope.row.userAgent }}</div>
                </el-tooltip>
              </template></el-table-column
            >
            <el-table-column prop="loginTime" label="登录时间" width="200" />

            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <div class="flex space-x-2">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click="handleDeleteClick(scope.row)"
                    ><div class="font-normal text-red-500 flex items-center">
                      <i-ep-delete class="text-xs mr-1px" />踢出
                    </div></el-button
                  >
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="flex justify-end mt-20px">
          <el-pagination
            v-model:currentPage="tableObject.currentPage"
            v-model:page-size="tableObject.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableObject.total"
            :page-sizes="[5, 10, 20, 50]"
            :default-page-size="5"
          >
          </el-pagination>
        </div>
      </div>
    </el-config-provider>
  </div>
</template>

<style></style>
