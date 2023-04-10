<script setup lang="ts">
import { LoginForm, RegisterForm } from "./components";
</script>

<template>
  <div class="h-screen flex">
    <!-- 左半边 -->
    <div
      class="flex flex-col flex-1 bg-gray-500 bg-opacity-20 font-mono text-sky-800 <xl:hidden"
    >
      <!-- 左上角的 logo + 系统标题 -->
      <div class="flex items-center ml-6 mt-6 text-3xl font-bold">
        <img src="@/assets/images/logo.png" alt="" class="w-12 h-12 mr-2" />
        <span>Demo管理系统</span>
      </div>
      <!-- 左下边的背景图 + 欢迎语 -->
      <div class="flex flex-col justify-center items-center h-screen">
        <img
          src="@/assets/svgs/vue.svg"
          key="1"
          alt=""
          class="w-60 mb-2 transform rotate-180"
        />
        <div class="text-3xl" key="2">欢迎使用本系统</div>
        <div class="mt-5 text-xl" key="3">开箱即用的中后台管理系统</div>
      </div>
    </div>

    <!-- 右半边 -->
    <div class="flex-1 p-10 <sm:p-10px dark:bg-v-dark">
      <!-- 右上角的主题、语言选择 -->
      <!-- <div
          class="flex justify-between items-center text-white @2xl:justify-end @xl:justify-end"
        >
          <div class="flex justify-end items-center space-x-10px">
                                      <ThemeSwitch />
                                      <LocaleDropdown class="<xl:text-white dark:text-white" />
                                    </div>
                                  </div> -->
      <!-- 右边的登录界面 -->
      <div class="h-full flex items-center">
        <!-- 账号登录 -->
        <LoginForm/>
        <!-- 注册 -->
        <RegisterForm/>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.fade-enter-from {
  width: 0;
  height: 0;
}

.fade-enter-active {
  transition: all 1.5s ease;
}
</style>
