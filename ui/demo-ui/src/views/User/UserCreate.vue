<script setup lang="ts">
import { onMounted, ref, reactive, computed, watch, unref } from "vue";
import {
  ElButton,
  ElButtonGroup,
  ElTooltip,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElConfigProvider,
  ElMessage,
  ElDialog,
  ElInput,
  ElForm,
  ElFormItem,
  ElStep,
  ElSteps,
  ElDivider,
  ElDescriptions,
  ElDescriptionsItem,
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";
import type { FormInstance } from "element-plus";
import { required, lengthValidate } from "@/utils/formRules";

import * as RoleApi from "@/api/role";
import * as UserApi from "@/api/user";

import type { Role } from "@/api/role/types";
import type { Permission } from "@/api/permission/types";
import User from "@/views/User/User.vue";
import {SysUser, UserRegisterVO} from "@/api/user/types";
import * as LoginApi from "@/api/login";
import {encrypt} from "@/utils/jsencrypt";

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

const tableRef = ref();

const refreshRoleList = () => {
  methods.getList();
};

// ============== 步骤切换逻辑 ==============

// 最终提交的内容
const dialogForm = reactive({
  userName: "",
  userPwd: "",
  roleIds: [],
});

// 表单验证规则
const rules = {
  userName: [required, lengthValidate(1, 20)],
  userPwd: [required, lengthValidate(1, 20)],
};

// 第几步处于激活中
const active = ref(0);

// 如果表格那一步激活，重新刷新表格内容
watch(
  () => active.value,
  () => {
    if (active.value == 1) {
      refreshRoleList();
    }
  }
);

// ============== 第一步：基本信息 ==============
const firstFormRef = ref<FormInstance>();
const firstFormDisabled = ref(false);
const handleFirstNext = () => {
  unref(firstFormRef)?.validate((valid, fields) => {
    if (valid) {
      active.value++;
    }
  });
};

// ============== 第二步：权限选择、删除相关逻辑 ==============
// 保存勾选的权限
const selectedTags = ref<Array<Role>>([]);

// 从列表中选择某个权限
const handleSelectClick = (row: Role) => {
  selectedTags.value.push(row);
};

// 取消选择已选的权限
const handleTagRemove = (tag: Role) => {
  selectedTags.value = selectedTags.value.filter((item) => {
    return item.roleId != tag.roleId;
  });
};

// 监听已选权限列表，用于设置表格中的那一行是否可选（不允许重复选择）
const checkSelectDisabled = computed(() => (row: Role) => {
  let find = false;
  selectedTags.value.forEach((element) => {
    if (element.roleId == row.roleId) {
      find = true;
      return;
    }
  });
  return find;
});

// ============== 第三步：预览和提交 ==============
// 提交表单
const addConfirmLoading = ref(false);
const submit = async () => {
  try {
    const publicKey = await LoginApi.getPublicKey();
    const passwordEncrypted = encrypt(
        publicKey,
        dialogForm.userPwd
    ) || "";

    addConfirmLoading.value = true;

    const roleIds = selectedTags.value.map((element) => {
      return element.roleId;
    });

    const newUser: UserRegisterVO = {
      username: dialogForm.userName,
      password: passwordEncrypted,
      roleIds: roleIds,
    };
    await UserApi.register(newUser);
    ElMessage.success("新增成功");

    closeDialog();
    emits("refreshList");
  } finally {
    addConfirmLoading.value = false;
  }
};

// ============== 本组件显示、隐藏逻辑 ==============
const thisVisable = ref(false);
const dialogTitle = ref("");

// 准备数据数据
const prepareData = () => {
  dialogForm.userName = "";
  dialogForm.userPwd = "";
  dialogForm.roleIds = [];
  selectedTags.value = [];
  firstFormDisabled.value = false;
  dialogTitle.value = "新增角色";

  active.value = 0;
};

const showDialog = () => {
  prepareData();
  thisVisable.value = true;
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
  <el-dialog v-model="thisVisable" :title="dialogTitle" width="600px">
    <el-steps :active="active" finish-status="success" class="mb-10 ml-10 mr-10">
      <el-step title="第一步" description="设置基本信息" />
      <el-step title="第二步" description="设置角色" />
      <el-step title="第三步" description="结果预览" />
    </el-steps>
    <el-divider />

    <div
      v-if="active == 0"
      class="flex flex-col justify-center items-center space-y-5 ml-10 mr-20"
    >
      <el-form
        :model="dialogForm"
        class="w-100"
        ref="firstFormRef"
        :rules="rules"
        :disabled="firstFormDisabled"
      >
        <el-form-item label="登陆名称" label-width="80px" prop="roleName">
          <el-input
            v-model="dialogForm.userName"
            minlength="1"
            maxlength="20"
            :show-word-limit="true"
            :clearable="true"
          />
        </el-form-item>
        <el-form-item label="登陆密码" label-width="80px" prop="roleKey">
          <el-input
            v-model="dialogForm.userPwd"
            minlength="1"
            maxlength="20"
            :show-word-limit="true"
            :clearable="true"
          />
        </el-form-item>
      </el-form>

      <el-button @click="handleFirstNext">下一步</el-button>
    </div>
    <div v-if="active == 1" class="flex flex-col justify-center items-center space-y-5">
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
              <el-table-column prop="roleKey" label="角色码" width="100" />
              <el-table-column prop="roleName" label="角色名称" width="200" />

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
        <div class="mb-2">已选权限：</div>
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
        <el-button @click="active--">上一步</el-button>
        <el-button @click="active++">下一步</el-button>
      </el-button-group>
    </div>
    <div
      v-if="active == 2"
      class="flex flex-col justify-center items-center space-y-5 ml-10 mr-10"
    >
      <el-descriptions title="结果预览" :column="1" class="w-[100%]">
        <el-descriptions-item label="用户名称:">{{
          dialogForm.userName
        }}</el-descriptions-item>
        <el-descriptions-item label="用户密码:">{{
          dialogForm.userPwd
        }}</el-descriptions-item>
        <el-descriptions-item label="角色:">
          <el-tag
            v-for="tag in selectedTags"
            :key="tag"
            class="ml-1 mb-1"
            :disable-transitions="false"
            @close="handleTagRemove(tag)"
          >
            {{ tag.roleName + "<" + tag.roleName + ">" }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-button-group>
        <el-button style="margin-top: 12px" @click="active--">上一步</el-button>

        <el-button style="margin-top: 12px" @click="closeDialog">取消</el-button>

        <el-button
          style="margin-top: 12px"
          type="primary"
          @click="submit"
          :loading="addConfirmLoading"
          >提交</el-button
        >
      </el-button-group>
    </div>
  </el-dialog>
</template>

<style></style>
