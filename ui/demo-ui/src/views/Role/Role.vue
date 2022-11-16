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
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

import * as RoleApi from "@/api/role";
import type { Role } from "@/api/role/types";

import RoleCreate from "./RoleCreate.vue";

const locale = ref(zhCn);

const tableObject = reactive<TableObject<Role>>({
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

    const res = await RoleApi.pageList(unref(paramsObj)).finally(() => {
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

const refreshRoleList = () => {
  methods.getList();
};

const handleDeleteClick = async (row: Role) => {
  ElMessageBox.confirm("确定删除当前角色码? 该操作不可逆！", "警告", {
    type: "warning",
  })
    .then(async () => {
      await RoleApi.deleteById({ roleId: row.roleId });
      ElMessage.success("删除角色成功");
      refreshRoleList();
    })
    .catch(() => {
      // catch error
    });
};

const roleCreateRef = ref();

const handleModifyClick = (row: Role) => {
  unref(roleCreateRef).showDialog(row);
};

const handleAddClick = () => {
  unref(roleCreateRef).showDialog(null);
};
</script>

<template>
  <div class="m-10">
    <el-config-provider :locale="locale">
      <div class="flex items-center justify-between">
        <div>
          <el-button plain type="primary" @click="handleAddClick">
            <i-ep-circle-plus class="mr-5px" />新增
          </el-button>
        </div>

        <div>
          <el-tooltip class="box-item" effect="dark" content="刷新" placement="top-end">
            <el-button circle @click="refreshRoleList">
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
            <el-table-column prop="roleId" label="ID" width="200" />
            <el-table-column prop="roleName" label="角色名称" width="150" />
            <el-table-column prop="roleKey" label="角色码" width="150" />
            <el-table-column label="绑定权限">
              <template #default="scope">
                <el-tag
                  v-for="tag in scope.row.permissions"
                  :key="tag"
                  class="ml-1 mb-1"
                  :disable-transitions="false"
                >
                  {{ tag.permissionName + "<" + tag.permissionKey + ">" }}
                </el-tag>

              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200" />

            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <div class="flex space-x-2">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click="handleModifyClick(scope.row)"
                  >
                    <div class="font-normal flex items-center"><i-ep-edit class="text-xs mr-1px "/>修改</div>
                  </el-button>
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click="handleDeleteClick(scope.row)"
                  >
                    <div class="font-normal text-red-500 flex items-center"><i-ep-delete class="text-xs mr-1px"/>删除</div>
                  </el-button>
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

  <RoleCreate ref="roleCreateRef" @refreshList="refreshRoleList" />
</template>

<style></style>
