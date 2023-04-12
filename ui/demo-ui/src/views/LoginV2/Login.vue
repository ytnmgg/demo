<template lang="">
  <div class="flex justify-center items-center h-full">
    <div
      class="bg-neutral-800 max-w-md p-12 rounded-lg text-base font-normal text-white/60"
    >
      <div>Account</div>
      <div>
        <el-input
          placeholder="Enter your username"
          v-model="loginData.loginForm.username"
          size="large"
        >
          <template #prepend>
            <i-ep-avatar />
          </template>
        </el-input>
        <div class="min-h-[22px]"></div>
      </div>

      <div>Password</div>
      <div>
        <el-input
          placeholder="Enter your password"
          show-password
          v-model="loginData.loginForm.password"
          size="large"
          autocomplete="new-password"
        >
          <template #prepend>
            <i-ep-lock />
          </template>
        </el-input>
        <div class="min-h-[22px]"></div>
      </div>

      <div
        class="bg-neutral-700 mt-4 mb-6 p-4 font-sans text-xs font-light leading-6 rounded-md"
      >
        Warning: You are accessing a PRIVATE website, Please leave ASAP, or we
        reserve the right to take further legal-measures.
      </div>
      <el-button
        class="w-full"
        size="large"
        :loading="loginLoading"
        @click="handleLogin"
      >
        <span class="text-sky-400">LOG IN</span></el-button
      >
    </div>
  </div>
</template>
<script setup lang="ts">
import { ElInput, ElButton } from "element-plus";
import { reactive, ref, unref, onMounted, computed, watch } from "vue";
import * as LoginApi from "@/api/login";
import { encrypt } from "@/utils/jsencrypt";
import { TokenType } from "@/api/login/types";
import { setToken } from "@/utils/auth";
import { useRouter } from "vue-router";
import type { RouteLocationNormalizedLoaded } from "vue-router";
import { useAppStore } from "@/store/modules/app";

const appStore = useAppStore();
const { currentRoute, push } = useRouter();

const loginLoading = ref(false);
const redirect = ref<string>("");

onMounted(() => {
  appStore.setIsDark(true);
});

const loginData = reactive({
  isShowPassword: false,
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  token: "",
  loading: {
    signIn: false,
  },
  loginForm: {
    username: "",
    password: "",
    captchaVerification: "",
    rememberMe: false,
  },
});

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string;
  },
  {
    immediate: true,
  }
);

const handleLogin = async () => {
  loginLoading.value = true;

  try {
    const publicKey = await LoginApi.getPublicKey();
    const passwordEncrypted = encrypt(publicKey, loginData.loginForm.password);
    const data = {
      ...loginData.loginForm,
      password: passwordEncrypted,
    };

    const res: string = await LoginApi.loginApi(data);

    const tonken: TokenType = {
      accessToken: res,
    };

    setToken(tonken);

    if (!redirect.value) {
      redirect.value = "/";
    }
    push({ path: redirect.value });
  } catch (e) {
  } finally {
    loginLoading.value = false;
  }
};
</script>
<style lang=""></style>
