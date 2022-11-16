<script setup lang="ts">
import { ref, reactive, computed, watch, unref } from "vue";
import {
  ElButton,
  ElButtonGroup,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElConfigProvider,
  ElMessage,
  ElDialog,
  ElDivider,
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";

import * as RoleApi from "@/api/role";
import type { Role } from "@/api/role/types";
import * as UserApi from "@/api/user";
import type { SysUser } from "@/api/user/types";

// ============== 权限表格逻辑 ==============
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

// 翻页参数
const paramsObj = computed(() => {
  return {
    pageSize: tableObject.pageSize,
    pageIndex: tableObject.currentPage,
  };
});

// 分页
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
const refreshRoleList = () => {
  methods.getList();
};

const tableRef = ref();

// 最终提交的内容
const dialogForm = reactive({
  userId: "",
  roleIds: [],
});

// 保存勾选的角色
const selectedTags = ref<Array<Role>>([]);

// 从列表中选择某个角色
const handleSelectClick = (row: Role) => {
  selectedTags.value.push(row);
};

// 取消选择已选的权限
const handleTagRemove = (tag: Role) => {
  selectedTags.value = selectedTags.value.filter((item) => {
    return item.roleKey != tag.roleKey;
  });
};

// 监听已选角色列表，用于设置表格中的那一行是否可选（不允许重复选择）
const checkSelectDisabled = computed(() => (row: Role) => {
  let find = false;
  selectedTags.value.forEach((element) => {
    if (element.roleKey == row.roleKey) {
      find = true;
      return;
    }
  });
  return find;
});

// 提交表单
const confirmButtonLoading = ref(false);
const submit = async () => {
  try {
    confirmButtonLoading.value = true;

    const roleIds = selectedTags.value.map((element) => {
      return element.roleId;
    });

    const updateUser: SysUser = {
      userId: dialogForm.userId,
      roleIds: roleIds,
    };

    await UserApi.updateUserRoles(updateUser);
    ElMessage.success("设置成功");

    closeDialog();
    emits("refreshList");
  } finally {
    confirmButtonLoading.value = false;
  }
};

// ============== 本组件显示、隐藏逻辑 ==============
const thisVisable = ref(false);
const isModify = ref(false);
const dialogTitle = ref("");

// 准备数据数据（分新增和修改两种情况）
const prepareData = (user: SysUser) => {
  dialogForm.userId = user.userId;

  if (user.roles != null) {
    selectedTags.value = user.roles.map((item: Role) => {
      const copyRole: Role = {};
      copyRole.roleId = item.roleId;
      copyRole.roleName = item.roleName;
      copyRole.roleKey = item.roleKey;
      return copyRole;
    });
    dialogForm.roleIds = user.roles.map((item: Role) => {
      return item.roleId;
    });
  } else {
    dialogForm.roleIds = [];
    selectedTags.value = [];
  }
};

const showDialog = (user: SysUser) => {
  prepareData(user);
  thisVisable.value = true;
  refreshRoleList();
};

const closeDialog = () => {
  thisVisable.value = false;
};

defineExpose({
  showDialog,
});

// ============== 父组件列表刷新逻辑 ==============
const emits = defineEmits(["refreshList"]);
</script>

<template>
  <el-dialog v-model="thisVisable" title="用户角色设置" width="600px">
    <div class="flex flex-col justify-center items-center space-y-5">
      <el-config-provider :locale="locale">
        <div>
          <div>
            <el-table
              :data="tableObject.tableList"
              v-loading="tableObject.loading"
              style="width: 100%"
              ref="tableRef"
            >
              <el-table-column prop="roleId" label="ID" width="160" />
              <el-table-column prop="roleName" label="角色名称" width="100" />
              <el-table-column prop="roleKey" label="角色码" width="200" />

              <el-table-column fixed="right" label="操作">
                <template #default="scope">
                  <div class="flex space-x-2">
                    <el-button
                      link
                      type="primary"
                      size="small"
                      @click="handleSelectClick(scope.row)"
                      :disabled="checkSelectDisabled(scope.row)"
                      ><div class="font-normal">添加</div></el-button
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

      <el-divider />

      <div class="place-self-start">
        <div class="mb-2">已选角色：</div>
        <el-tag
          v-for="tag in selectedTags"
          :key="tag"
          class="ml-1 mb-1"
          closable
          :disable-transitions="false"
          @close="handleTagRemove(tag)"
        >
          {{ tag.roleName + "<" + tag.roleKey + ">" }}
        </el-tag>
      </div>
      <el-button-group>
        <el-button style="margin-top: 12px" @click="closeDialog">取消</el-button>
        <el-button
          style="margin-top: 12px"
          type="primary"
          @click="submit"
          :loading="confirmButtonLoading"
          >提交</el-button
        >
      </el-button-group>
    </div>
  </el-dialog>
</template>

<style></style>
