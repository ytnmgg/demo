<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch, unref } from "vue";
import {
  ElButton,
  ElTooltip,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElConfigProvider,
  ElTag,
  ElMessage,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElMessageBox,
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

import * as UserApi from "@/api/user";
import * as LoginApi from "@/api/login";
import type { SysUser } from "@/api/user/types";
import { transferUserType, transferUserStatus } from "@/api/user/util";
import UserDetail from "./UserDetail.vue";
import { encrypt } from "@/utils/jsencrypt";

const locale = ref(zhCn);

const tableObject = reactive<TableObject<SysUser>>({
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

    const res = await UserApi.getUserPageApi(unref(paramsObj)).finally(() => {
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

const refreshUserList = () => {
  methods.getList();
};

const detailRef = ref();

const handleDetailClick = (row: SysUser) => {
  unref(detailRef).showDetail(row);
};

const handleDeleteClick = (row: SysUser) => {
  ElMessageBox.confirm("确定删除当前用户? 该操作不可逆！", "警告", {
    type: "warning",
  })
    .then(() => {
      UserApi.deleteUser({ userId: row.userId });
      ElMessage.success("删除用户成功");
      refreshUserList();
    })
    .catch(() => {
      // catch error
    });
};

const handleResetPwd = (row: SysUser) => {
  ElMessageBox.prompt("请输入新密码", "重置密码", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPattern: /^(?=.*\S).+$/,
    inputErrorMessage: "请输入新的密码",
    callback: async (action, instance) => {
      if (action.action == "confirm") {
        instance.confirmButtonLoading = true;

        const publicKey = await LoginApi.getPublicKey();
        const passwordEncrypted = encrypt(publicKey, instance.inputValue);

        const newUser: SysUser = {
          userId: row.userId,
          password: passwordEncrypted,
        };

        await UserApi.resetUserPwd(newUser);

        ElMessage.success("重置用户密码成功");
        instance.confirmButtonLoading = false;
      }
    },
  });
};

const moreDropdown = ref();
</script>

<template>
  <div class="m-10">
    <el-config-provider :locale="locale">
      <div class="flex items-center justify-between">
        <div>
          <el-button plain type="primary">
            <i-ep-circle-plus class="mr-5px" />新增</el-button
          >
        </div>

        <div>
          <el-tooltip class="box-item" effect="dark" content="刷新" placement="top-end">
            <el-button circle @click="refreshUserList">
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
            <el-table-column prop="userId" label="ID" width="180" />
            <el-table-column prop="userName" label="用户名" width="180" />
            <el-table-column prop="nickName" label="昵称" />
            <el-table-column label="用户类型">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <div>{{ transferUserType(scope.row.userType) }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />

            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <div v-if="scope.row.status === '0'">
                    <el-tag>{{ transferUserStatus(scope.row.status) }}</el-tag>
                  </div>
                  <div v-else-if="scope.row.status === '1'">
                    <el-tag type="info">{{
                      transferUserStatus(scope.row.status)
                    }}</el-tag>
                  </div>
                  <div v-else>{{ transferUserStatus(scope.row.status) }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <div class="flex space-x-2">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click="handleDetailClick(scope.row)"
                    ><div class="font-normal">详情</div></el-button
                  >
                  <el-dropdown ref="moreDropdown" trigger="click">
                    <el-button link type="primary" size="small"
                      ><div class="font-normal">更多...</div></el-button
                    >
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item @click="handleResetPwd(scope.row)"
                          >重置用户密码</el-dropdown-item
                        >
                        <el-dropdown-item @click="handleDeleteClick(scope.row)">
                          <template #default>
                            <div class="font-normal text-red-500">删除用户</div>
                          </template>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
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
      <UserDetail ref="detailRef" @refreshList="refreshUserList"></UserDetail>
    </el-config-provider>
  </div>
</template>

<style></style>
