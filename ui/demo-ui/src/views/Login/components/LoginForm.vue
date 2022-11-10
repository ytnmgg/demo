<script lang="ts" setup>
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElCheckbox,
  ElCol,
  ElLink,
  ElRow,
} from "element-plus";
import type { FormInstance } from "element-plus";

import { reactive, ref, unref, onMounted, computed, watch } from "vue";
import * as LoginApi from "@/api/login";
import {
  setToken,
  getRememberMe,
  setRememberMe,
  removeRememberMe,
  getUsername,
  setUsername,
  removeUsername,
  getPassword,
  setPassword,
  removePassword,
} from "@/utils/auth";
import { usePermissionStore } from "@/store/modules/permission";
import { useRouter } from "vue-router";
import { required, lengthValidate } from "@/utils/formRules";
import { encrypt } from "@/utils/jsencrypt";
import { LoginStateEnum, useLoginState } from "./useLogin";
import type { RouteLocationNormalizedLoaded } from "vue-router";
import { TokenType } from "@/api/login/types";

const { currentRoute, push } = useRouter();
const permissionStore = usePermissionStore();
const formLogin = ref<FormInstance>();
const { setLoginState, getLoginState } = useLoginState();
const getShow = computed(() => unref(getLoginState) === LoginStateEnum.LOGIN);

const redirect = ref<string>("");
const LoginRules = {
  username: [required, lengthValidate(3, 10)],
  password: [required, lengthValidate(3, 20)],
};
const loginLoading = ref(false);
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

// 记住我
const getCache = () => {
  const rememberMe = getRememberMe();
  if (rememberMe) {
    const username = getUsername();
    const password = getPassword();

    loginData.loginForm.username = username;
    loginData.loginForm.password = password;
    loginData.loginForm.rememberMe = rememberMe;
  }
};
const setCache = () => {
  if (loginData.loginForm.rememberMe) {
    setRememberMe(loginData.loginForm.rememberMe.toString());
    setUsername(loginData.loginForm.username);
    setPassword(loginData.loginForm.password);
  } else {
    removeRememberMe();
    removeUsername();
    removePassword();
  }
};

// 登录
const handleLogin = async () => {
  loginLoading.value = true;

  const formLoginUnRef = unref(formLogin);

  let validateResult = false;
  await formLoginUnRef?.validate((valid, fields) => {
    validateResult = valid;
    if (valid) {
      console.log("submit!");
    } else {
      console.log("error submit!", fields);
    }
  });

  if (!validateResult) {
    loginLoading.value = false;
    return;
  }

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

    setCache();

    if (!redirect.value) {
      redirect.value = "/";
    }
    push({ path: redirect.value || permissionStore.addRouters[0].path });
  } finally {
    loginLoading.value = false;
  }
};

watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string;
  },
  {
    immediate: true,
  }
);

onMounted(() => {
  getCache();
});
</script>
<template>
  <el-form
    :model="loginData.loginForm"
    :rules="LoginRules"
    label-position="top"
    label-width="120px"
    size="large"
    v-show="getShow"
    ref="formLogin"
  >
    <el-row>
      <el-col :span="24" class="pl-10px pr-10px flex justify-center items-center">
        <el-form-item>
          <h2
            class="mb-3 text-2xl font-bold text-center text-sky-800 xl:text-3xl enter-x xl:text-center"
          >
            账号登录
          </h2>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="pl-10px pr-10px">
        <el-form-item prop="username">
          <el-input v-model="loginData.loginForm.username" placeholder="请输入用户名称">
            <template #prepend>
              <i-ep-avatar />
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="pl-10px pr-10px">
        <el-form-item prop="password">
          <el-input
            v-model="loginData.loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prepend>
              <i-ep-lock />
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="pl-10px pr-10px mt-[-10px] mb-[-20px]">
        <el-form-item>
          <el-row justify="space-between" style="width: 100%">
            <el-col :span="6">
              <el-checkbox v-model="loginData.loginForm.rememberMe">记住我</el-checkbox>
            </el-col>
            <el-col :span="12" :offset="6">
              <el-link type="primary" style="float: right">忘记密码？</el-link>
            </el-col>
          </el-row>
        </el-form-item>
      </el-col>
      <el-col :span="24" style="padding-left: 10px; padding-right: 10px">
        <el-form-item>
          <el-button
            :loading="loginLoading"
            type="primary"
            class="w-[100%]"
            @click="handleLogin"
            >登录</el-button
          >
        </el-form-item>
      </el-col>
      <el-col :span="24" style="padding-left: 10px; padding-right: 10px">
        <el-form-item>
          <el-button class="w-[100%]" @click="setLoginState(LoginStateEnum.REGISTER)"
            >注册新用户</el-button
          >
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<style lang="less" scoped>
:deep(.anticon) {
  &:hover {
    color: var(--el-color-primary) !important;
  }
}
</style>
