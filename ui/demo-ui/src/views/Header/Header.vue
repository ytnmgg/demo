<script setup lang="ts">
import { ElMessage, ElTooltip } from "element-plus";
import { useAppStore } from "@/store/modules/app";
import * as LoginApi from "@/api/login";
import { removeToken } from "@/utils/auth";
import IconLogoutOutlined from "~icons/ant-design/logout-outlined";

import { ref, computed, unref } from "vue";
import type { Ref } from "vue";

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
</script>

<template>
  <div class="h-full flex items-center justify-between shadow shadow-gray-500/30">
    <div class="h-50px flex items-center ml-10px">
      <div v-show="isCollapse">
        <i-ep-expand
          class="cursor-pointer self-center text-gray-500"
          @click="toggleCollapse"
        />
      </div>
      <div v-show="!isCollapse">
        <i-ep-fold
          class="cursor-pointer self-center text-gray-500"
          @click="toggleCollapse"
        />
      </div>
    </div>

    <div class="h-full flex items-center mr-20px">
      <el-tooltip class="box-item" effect="dark" content="登出" placement="bottom-end">
        <template #default="scope">
          <icon-logout-outlined
            class="cursor-pointer self-center text-gray-500"
            @click="logout"
          />
        </template>
      </el-tooltip>
    </div>
  </div>
</template>

<style lang="less" scoped></style>
