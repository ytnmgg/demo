<script setup lang="ts">
import { computed, reactive, ref, unref } from "vue";
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElCol,
  ElRow,
  ElProgress,
  ElMessage,
} from "element-plus";
import { useLoginState, LoginStateEnum } from "./useLogin";
import type { FormInstance } from "element-plus";
import { required, lengthValidate } from "@/utils/formRules";
import { zxcvbn } from "@zxcvbn-ts/core";
import * as LoginApi from "@/api/login";
import * as UserApi from "@/api/user";
import type { UserRegisterVO } from "@/api/user/types";
import { encrypt } from "@/utils/jsencrypt";

const { handleBackLogin, getLoginState } = useLoginState();

const getShow = computed(
  () => unref(getLoginState) === LoginStateEnum.REGISTER
);

const formRegister = ref<FormInstance>();

const registerLoading = ref(false);

const registerData = reactive({
  isShowPassword: false,
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  token: "",
  loading: {
    signIn: false,
  },
  registerForm: {
    username: "",
    password: "",
    checkPassword: "",
    captchaVerification: "",
    rememberMe: false,
  },
});

// “确认密码框”变化的时候，验证两个密码是否一致
const validateCheckPassword = (rule: any, value: any, callback: any) => {
  if (value !== registerData.registerForm.password) {
    callback(new Error("密码和验证密码不相同"));
  } else {
    callback();
  }
};

// 密码框变化的时候，调用“确认密码框”的校验逻辑，看两个密码是否一致
const validatePassword = (rule: any, value: any, callback: any) => {
  if (registerData.registerForm.checkPassword !== "") {
    formRegister.value!.validateField("checkPassword", () => null);
  }
  callback();
};

const RegisterRules = reactive({
  username: [required, lengthValidate(3, 10)],
  password: [
    required,
    lengthValidate(3, 20),
    { validator: validatePassword, trigger: "blur" },
  ],
  checkPassword: [
    required,
    lengthValidate(3, 20),
    { validator: validateCheckPassword, trigger: "blur" },
  ],
});

// 登录
const handleRegister = async () => {
  registerLoading.value = true;

  const formRegisterUnRef = unref(formRegister);

  let validateResult = false;

  await formRegisterUnRef?.validate((valid, fields) => {
    validateResult = valid;
    if (valid) {
      console.log("submit!");
    } else {
      console.log("error submit!", fields);
    }
  });

  if (!validateResult) {
    registerLoading.value = false;
    return;
  }

  try {
    const publicKey = await LoginApi.getPublicKey();
    const passwordEncrypted = encrypt(
      publicKey,
      registerData.registerForm.password
    );

    const data: UserRegisterVO = {
      username: registerData.registerForm.username,
      password: passwordEncrypted,
      roleIds: null
    };

    await UserApi.register(data);

    afterRegisterSuccess();
  } catch (err) {
    registerLoading.value = false;
  }
};

const afterRegisterSuccess = () => {
  ElMessage({
    message: "注册成功，马上跳转登录页面登录...",
    type: "success",
    onClose: () => {
      formRegister.value!.resetFields();
      registerLoading.value = false;
      handleBackLogin();
    },
  });
};

// 密码强度显示
const percentage = ref(0);
const checkPercentage = ref(0);

// 根据比例值，返回对应的颜色和文案
const percentageMapping = (percentage: number) => {
  if (percentage < 30) {
    return {
      color: "#f97316",
      text: "较弱",
    };
  }
  if (percentage < 70) {
    return {
      color: "#f59e0b",
      text: "中等",
    };
  }
  return {
    color: "#84cc16",
    text: "较强",
  };
};

// 进度条回调函数：动态获取颜色
const customColorMethod = (percentage: number) => {
  const { color } = percentageMapping(percentage);
  return color;
};

// 进度条回调函数：动态获取文案
const customProgressText = (percentage: number) => {
  const { text } = percentageMapping(percentage);
  return text;
};

const passwordChange = (value: string) => {
  const strength = zxcvbn(value).guessesLog10;

  if (strength < 10) {
    percentage.value = strength * 10;
  } else {
    percentage.value = 100;
  }
};

const checkPasswordChange = (value: string) => {
  const strength = zxcvbn(value).guessesLog10;

  if (strength < 10) {
    checkPercentage.value = strength * 10;
  } else {
    checkPercentage.value = 100;
  }
};
</script>

<template>
  <el-form
    :model="registerData.registerForm"
    :rules="RegisterRules"
    label-position="top"
    label-width="120px"
    size="large"
    v-show="getShow"
    ref="formRegister"
  >
    <el-row>
      <el-col :span="24" class="flex justify-center">
        <el-form-item>
          <h2 class="mb-3 text-3xl font-bold text-center text-sky-800">
            账号注册
          </h2>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item prop="username">
          <!-- <span
            class="ml-1 w-35 text-gray-600 font-bold inline-flex items-center"
            >用户名</span
          > -->
          <el-input
            v-model="registerData.registerForm.username"
            placeholder="请输入用户名称"
          >
            <template #prepend> <i-ep-avatar /> </template
          ></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item prop="password">
          <!-- <span
            class="ml-1 w-35 text-gray-600 font-bold inline-flex items-center"
            >密码</span
          > -->
          <el-input
            v-model="registerData.registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            @input="passwordChange"
          >
            <template #prepend> <i-ep-lock /> </template
          ></el-input>
        </el-form-item>
        <div class="password-strength pt-1">
          <el-progress
            :percentage="percentage"
            :color="customColorMethod"
            :format="customProgressText"
          />
        </div>
      </el-col>
      <el-col :span="24">
        <el-form-item prop="checkPassword">
          <!-- <span
            class="ml-1 w-35 text-gray-600 font-bold inline-flex items-center"
            >确认密码</span
          > -->
          <el-input
            v-model="registerData.registerForm.checkPassword"
            type="password"
            placeholder="请确认密码"
            show-password
            @input="checkPasswordChange"
          >
            <template #prepend> <i-ep-lock /> </template
          ></el-input>
        </el-form-item>
        <div class="password-strength pt-1">
          <el-progress
            :percentage="checkPercentage"
            :color="customColorMethod"
            :format="customProgressText"
          />
        </div>
      </el-col>
      <el-col :span="24" style="padding-left: 10px; padding-right: 10px">
        <el-form-item>
          <el-button
            :loading="registerLoading"
            type="primary"
            class="w-[100%]"
            @click="handleRegister"
            >注册</el-button
          >
        </el-form-item>
      </el-col>
      <el-col :span="24" style="padding-left: 10px; padding-right: 10px">
        <el-form-item>
          <el-button class="w-[100%]" @click="handleBackLogin"
            >已有账号？去登录</el-button
          >
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<style lang="less" scoped>
.password-strength .el-progress--line {
  margin-top: -10px;
  margin-bottom: 20px;
  width: 100%;
  text-align: right;
}
</style>
