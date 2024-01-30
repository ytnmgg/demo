<script setup lang="ts">
import { ElMessage, ElTooltip } from "element-plus";
import { useDark, useToggle } from "@vueuse/core";

import { useAppStore } from "@/store/modules/app";
import * as LoginApi from "@/api/login";
import { removeToken } from "@/utils/auth";
import IconLogoutOutlined from "~icons/ant-design/logout-outlined";

import { ref, computed, unref } from "vue";
import type { Ref } from "vue";

const isDark = useDark();
const toggleDark = useToggle(isDark);

const props = defineProps<{
  menuRef: Ref;
}>();

const appStore = useAppStore();

const isCollapse = computed(() => appStore.getCollapse);

const toggleCollapse = () => {
  const collapsed = unref(isCollapse);
  appStore.setCollapse(!collapsed);
  if (collapsed) {
    unref(props.menuRef).openSubMenu();
  }
};

const logout = async () => {
  await LoginApi.logoutApi();

  removeToken();

  ElMessage({
    message: "登出成功，马上跳转登录页面登录...",
    type: "success",
    onClose: () => {
      window.location.href = "/";
    },
  });
};

const onDarkChange = () => {
  appStore.setIsDark(isDark.value)
}
</script>

<template>
  <div class="flex h-12 bg-[#343a40] text-gray-300">
    <div class="flex items-center font-mono font-bold mr-8">
      <img src="@/assets/images/logo.png" alt="" class="w-8 ml-2 mr-2" />
      <span>Demo管理系统</span>
    </div>

    <div v-show="isCollapse" class="flex items-center pl-2">
      <i-ep-expand class="cursor-pointer text-gray-500" @click="toggleCollapse" />
    </div>
    <div v-show="!isCollapse" class="flex items-center pl-2">
      <i-ep-fold class="cursor-pointer text-gray-500" @click="toggleCollapse" />
    </div>

    <div class="flex items-center ml-auto mr-5">
      
      <div class="flex items-center mr-2">
        <el-tooltip class="box-item" effect="dark" content="黑暗模式" placement="bottom-end">
          <el-switch
              v-model="isDark"
              @change="onDarkChange"
              size="small"
              style="--el-switch-on-color: #64748b; --el-switch-off-color: #94a3b8">
          </el-switch>
        </el-tooltip>
      </div>

      <el-tooltip class="box-item" effect="dark" content="登出" placement="bottom-end">
        <template #default="scope">
          <icon-logout-outlined class="cursor-pointer text-gray-500" @click="logout" />
        </template>
      </el-tooltip>
    </div>
  </div>
</template>

<style lang="less" scoped></style>
