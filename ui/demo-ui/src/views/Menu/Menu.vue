<script setup lang="ts">
import { ElMenu, ElMenuItem, ElSubMenu, ElScrollbar, ElIcon } from "element-plus";

import { isUrl } from "@/utils/is";

import { ref, computed, unref } from "vue";
import { useAppStore } from "@/store/modules/app";
import { useRouter } from "vue-router";
import IconGridIcon from "~icons/lucide/grid";
import IconSettingsIcon from "~icons/lucide/settings";
import IconUserIcon from "~icons/lucide/user";
import IconVenetianMask from "~icons/lucide/venetian-mask";
import IconShieldIcon from "~icons/lucide/shield";
import IconUsersIcon from "~icons/lucide/users";
import IconClipboardIcon from "~icons/lucide/clipboard";
import IconClipboardCopyIcon from "~icons/lucide/clipboard-copy";
import IconClipboardEditIcon from "~icons/lucide/clipboard-edit";
import IconClapperboardIcon from "~icons/lucide/clapperboard";

const openSubMenuIndex = "sys";

const openItems = computed(() => (unref(isCollapse) ? [] : [openSubMenuIndex]));

const { push, currentRoute } = useRouter();
const handleSelect = (key: string, keyPath: string[]) => {
  if (isUrl(key)) {
    window.open(key);
  } else {
    push(key);
  }
};

const appStore = useAppStore();
const isCollapse = computed(() => appStore.getCollapse);

const activeMenu = computed(() => {
  const { meta, path } = unref(currentRoute);

  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu as string;
  }
  return path;
});

const logoText = computed(() => (unref(isCollapse) ? "" : "Demo管理系统"));

const leftMenu = ref();

const openSubMenu = () => {
  if (!unref(isCollapse)) {
    leftMenu.value.open(openSubMenuIndex);
  }
};

defineExpose({
  openSubMenu,
});
</script>

<template>
  <el-scrollbar>
    <el-menu
      :default-active="activeMenu"
      @select="handleSelect"
      background-color="#1f2937"
      text-color="#bfcbd9"
      :default-openeds="openItems"
      :collapse="isCollapse"
      class="h-[100vh]"
      ref="leftMenu"
    >
      <div
        class="flex items-center text-[#4d7c0f] pt-10px pl-10px !h-[50px] bg-[#1f2937]"
      >
        <img src="@/assets/images/logo.png" alt="" class="w-32px h-32px mr-10px" />
        <span class="text-20px font-bold">{{ logoText }}</span>
      </div>
      <el-menu-item index="index">
        <icon-grid-icon class="mr-2" /><span>首页</span></el-menu-item
      >
      <el-sub-menu index="sys">
        <template #title>
          <icon-settings-icon class="mr-2" />
          <span>系统管理</span>
        </template>
        <el-menu-item index="user">
          <icon-user-icon class="mr-2" />
          用户管理
        </el-menu-item>
        <el-menu-item index="role">
          <icon-venetian-mask class="mr-2" />角色管理
        </el-menu-item>
        <el-menu-item index="permission">
          <icon-shield-icon class="mr-2" />权限管理
        </el-menu-item>
        <el-menu-item index="logins">
          <icon-users-icon class="mr-2" />在线用户
        </el-menu-item>
        <el-menu-item index="task">
          <i-ep-timer class="mr-2" />任务管理
        </el-menu-item>
        <el-sub-menu index="log">
          <template #title>
            <icon-clipboard-icon class="mr-2" />
            <span>日志管理</span>
          </template>
          <el-menu-item index="log-login">
            <icon-clipboard-copy-icon class="mr-2" />登陆日志
          </el-menu-item>
          <el-menu-item index="log-op">
            <icon-clipboard-edit-icon class="mr-2" />操作日志
          </el-menu-item>
        </el-sub-menu>
      </el-sub-menu>
      <el-menu-item index="process" disabled>
        <icon-clapperboard-icon class="mr-2" /><span>工作流程</span></el-menu-item
      >
    </el-menu>
  </el-scrollbar>
</template>

<style lang="less" scoped></style>
