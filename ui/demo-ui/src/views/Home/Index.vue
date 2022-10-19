<script setup lang="ts">
import { onMounted, ref } from "vue";

import * as UserApi from "@/api/user";
import type { SysUser } from "@/api/user/types";

const user = ref<SysUser>();

const getUser = async () => {
  const res = await UserApi.getMe();
  user.value = res;
};

onMounted(async () => {
  await getUser();
});

</script>

<template>
  <div v-if="user">Welcome {{ user.nickName }} !</div>
</template>

<style></style>
