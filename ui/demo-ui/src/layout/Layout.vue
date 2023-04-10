<script setup lang="ts">
import { ElScrollbar, ElBreadcrumb, ElBreadcrumbItem } from "element-plus";
import { Icon } from "@/components/Icon";
import Menu from "@/views/Menu/Menu.vue";
import Header from "@/views/Header/Header.vue";

import { useAppStore } from "@/store/modules/app";

import { ref, computed, unref, onMounted } from "vue";

const activeIndex = ref("1");
const activeIndex2 = ref("1");
const openItems = ref(["2"]);

const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};

const appStore = useAppStore();

const isCollapse = computed(() => appStore.getCollapse);

const menueClass = computed(() => {
  if (appStore.getCollapse) {
    return "h-full w-auto";
  } else {
    return "h-full w-auto";
  }
});

const menuRef = ref();

let progress = ref(0);
onMounted(() => {
  setInterval(() => {
    if (progress.value < 100) {
      progress.value += 10;
    } else {
      progress.value = 0;
    }
  }, 1000);
});
</script>

<template>
  <div>
    <Header :menuRef="menuRef"></Header>

    <div class="flex">
      <div class="">
        <Menu ref="menuRef"></Menu>
      </div>
      <div class="flex-1 min-w-[calc(100vw-201px)]">
        <div class="h-[calc(100vh-50px)]">
          <el-scrollbar>
            <router-view></router-view>
          </el-scrollbar>
        </div>

        <!-- <ElScrollbar
            v-loading={pageLoading.value}
            class={[
              `${prefixCls}-content-scrollbar`,
              {
                '!h-[calc(100%-var(--top-tool-height)-var(--tags-view-height))] mt-[calc(var(--top-tool-height)+var(--tags-view-height))]':
                  fixedHeader.value
                  }
                ]}
              >
                <div
                  class={[
                    {
                      'fixed top-0 left-0 z-10': fixedHeader.value,
                      'w-[calc(100%-var(--left-menu-min-width))] left-[var(--left-menu-min-width)]':
                        collapse.value && fixedHeader.value && !mobile.value,
                      'w-[calc(100%-var(--left-menu-max-width))] left-[var(--left-menu-max-width)]':
                        !collapse.value && fixedHeader.value && !mobile.value,
                      '!w-full !left-0': mobile.value
                    }
                  ]}
                  style="transition: all var(--transition-time-02);"
                >
                  <ToolHeader class="border-bottom-1 border-solid border-[var(--top-tool-border-color)] bg-[var(--top-header-bg-color)] dark:border-[var(--el-border-color)]"></ToolHeader>

                  {tagsView.value ? (
                    <TagsView class="border-bottom-1 border-top-1 border-solid border-[var(--tags-view-border-color)] dark:border-[var(--el-border-color)]"></TagsView>
                  ) : undefined}
                </div>

                <AppView></AppView>
              </ElScrollbar> -->
      </div>
    </div>
  </div>
</template>

<style lan="less" scoped></style>
