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
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

import * as PermissionApi from "@/api/permission";
import type { Permission } from "@/api/permission/types";

const locale = ref(zhCn);

const tableObject = reactive<TableObject<Permission>>({
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

    const res = await PermissionApi.pageList(unref(paramsObj)).finally(() => {
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

const handleDeleteClick = (row: Permission) => {
  ElMessageBox.confirm("确定删除当前权限码? 该操作不可逆！", "警告", {
    type: "warning",
  })
    .then(() => {
      PermissionApi.deleteById({ permissionId: row.permissionId });
      ElMessage.success("删除权限码成功");
      refreshList();
    })
    .catch(() => {
      // catch error
    });
};

const addDialogFormVisible = ref(false);
const addForm = reactive({
  permissionName: "",
  permissionKey: "",
});

const addConfirmLoading = ref(false);
const handleAdd = async () => {
  try {
    addConfirmLoading.value = true;
    const newPermission: Permission = {
      permissionName: addForm.permissionName,
      permissionKey: addForm.permissionKey,
    };
    await PermissionApi.create(newPermission);
    ElMessage.success("新增成功");
    addDialogFormVisible.value = false;
    refreshList();
  } finally {
    addConfirmLoading.value = false;
  }
};
</script>

<template>
  <div class="m-10">
    <el-config-provider :locale="locale">
      <div class="flex items-center justify-between">
        <div>
          <el-button plain type="primary" @click="addDialogFormVisible = true">
            <i-ep-circle-plus class="mr-5px" />新增</el-button
          >
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
            <el-table-column prop="permissionId" label="ID" width="200" />
            <el-table-column prop="permissionName" label="权限名称" width="300" />
            <el-table-column prop="permissionKey" label="权限码" />
            <el-table-column prop="createTime" label="创建时间" />

            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <div class="flex space-x-2">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click="handleDeleteClick(scope.row)"
                    ><div class="font-normal text-red-500 flex items-center"><i-ep-delete class="text-xs mr-1px"/>删除</div></el-button
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

  <el-dialog v-model="addDialogFormVisible" title="新增权限" width="600px">
    <el-form :model="addForm">
      <el-form-item label="权限名称" label-width="80px">
        <el-input v-model="addForm.permissionName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="权限码" label-width="80px">
        <el-input v-model="addForm.permissionKey" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd" :loading="addConfirmLoading">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style></style>
