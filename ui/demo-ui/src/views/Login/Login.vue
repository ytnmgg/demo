<script setup lang="ts">
import { LoginForm, RegisterForm } from "./components";
</script>

<template>
  <div
    class="h-[100%] relative overflow-hidden <xl:bg-v-dark <sm:px-10px <xl:px-10px <md:px-10px"
  >
    <div class="relative h-full flex mx-auto">
      <div class="flex-1 bg-gray-500 bg-opacity-20 relative p-30px <xl:hidden">
        <!-- 左上角的 logo + 系统标题 -->
        <div class="flex items-center relative text-sky-800">
          <img src="@/assets/images/logo.png" alt="" class="w-48px h-48px mr-10px" />
          <span class="text-20px font-bold">Demo管理系统</span>
        </div>
        <!-- 左边的背景图 + 欢迎语 -->
        <div class="flex flex-col justify-center items-center h-[calc(100%-60px)]">
          <img
            src="@/assets/svgs/vue.svg"
            key="1"
            alt=""
            class="w-200px mb-10px transform rotate-180"
          />
          <div class="text-3xl text-sky-800" key="2">欢迎使用本系统</div>
          <div class="mt-5 font-normal text-sky-800 text-14px" key="3">
            开箱即用的中后台管理系统
          </div>
        </div>
      </div>
      <div class="flex-1 p-30px <sm:p-10px dark:bg-v-dark relative">
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
        <div
          class="h-full flex items-center m-auto w-[100%] @2xl:max-w-600px @xl:max-w-500px @md:max-w-500px @lg:max-w-500px"
        >
          <!-- 账号登录 -->
          <LoginForm class="p-20px h-auto m-auto <xl:(rounded-3xl light:bg-white)" />
          <!-- 注册 -->
          <RegisterForm class="p-20px h-auto m-auto <xl:(rounded-3xl light:bg-white)" />
        </div>
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
