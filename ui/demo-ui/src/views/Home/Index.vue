<script setup lang="ts">
import { onMounted, ref } from "vue";

import * as UserApi from "@/api/user";
import type { SysUser } from "@/api/user/types";

const user = ref<SysUser>();

// const getUser = async () => {
//   const res = await UserApi.getMe();
//   user.value = res;
// };

// onMounted(async () => {
//   await getUser();
// });

</script>

<template>
  <!-- <div v-if="user">Welcome {{ user.nickName }} !</div> -->
  <div>in home</div>

  
                                   /grafana/d/rYdddlPWk/node-exporter-full?orgId=1&var-DS_PROMETHEUS=default&var-job=node&var-node=172.27.183.154:9100&var-diskdevices=%5Ba-z%5D%2B%7Cnvme%5B0-9%5D%2Bn%5B0-9%5D%2B%7Cmmcblk%5B0-9%5D%2B&refresh=10s&viewPanel=20&kiosk
  <iframe src="http://139.224.72.37/grafana/d/rYdddlPWk/node-exporter-full?orgId=1&var-DS_PROMETHEUS=default&var-job=node&var-node=172.27.183.154%3A9100&var-diskdevices=%5Ba-z%5D%2B%7Cnvme%5B0-9%5D%2Bn%5B0-9%5D%2B%7Cmmcblk%5B0-9%5D%2B&refresh=10&theme=light&viewPanel=20" 
  width="199"
  height="144"
  frameborder="0"/>

</template>

<style></style>
