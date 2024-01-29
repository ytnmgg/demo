<script setup lang="ts">
import {ElButton, ElDrawer, ElLoading, ElMessage, ElMessageBox,} from "element-plus";
import * as KafkaApi from "@/api/kafka";
import type {SysUser} from "@/api/user/types";


import {reactive, ref} from "vue";


const drawerRef = ref<HTMLElement | null>(null);

const show = ref(false);
let changed = false;

const detail = reactive({
  data: {
    name: "",
    uuid: "",
    partitions: [],
  },
});

const showDetail = (row: SysUser) => {
  show.value = true;
  detail.data = row;
};

const refreshDrawer = async () => {
  const loadingInstance = ElLoading.service({
    target: document.querySelector(".topic-edit-drawer") as HTMLElement,
  });

  const topic = await KafkaApi.getTopic({topicName: detail.data.name});
  detail.data = topic;

  loadingInstance.close();
};

const showEdit = reactive({
  nickName: false,
  sex: false,
  userType: false,
  phonenumber: false,
  email: false,
});

const save = async (el: HTMLElement, item: string) => {
  changed = true;
  const loadingInstance = ElLoading.service({
    target: el,
  });


  loadingInstance.close();
  showEdit[item as keyof typeof showEdit] = false;

  refreshDrawer();
};
const onOpened = () => {
  refreshDrawer();
  changed = false;
};

defineExpose({
  showDetail,
});

const emits = defineEmits(["refreshTopicList"]);
const handelClosed = () => {
  if (changed) {
    changed = false;
    emits("refreshTopicList");
  }
};

const handleTopicDeleteClick = async (name: string) => {
  ElMessageBox.confirm("确定删除当前Topic码? 该操作不可逆！", "警告", {
    type: "warning",
  })
      .then(async () => {
        await KafkaApi.deleteTopic({topicName: name});
        ElMessage.success("删除Topic成功");
        changed = true
        show.value = false
      })
      .catch(() => {
        // catch error
      });
};
</script>

<template>
  <el-drawer
      ref="drawerRef"
      v-model="show"
      title="Topic详情"
      @opened="onOpened"
      @closed="handelClosed"
  >
    <div class="topic-edit-drawer">
      <div class="flex flex-col space-y-4">
        <div class="flex space-x-4">
          <div class="text-neutral-500 min-w-100px">Topic名称</div>
          <div class="text-neutral-300">{{ detail.data.name }}</div>
        </div>
        <div class="flex space-x-4">
          <div class="text-neutral-500 min-w-100px">UUID</div>
          <div class="text-neutral-300">{{ detail.data.uuid }}</div>
        </div>
        <div class="text-neutral-500 min-w-100px">分区信息</div>
        <el-card class="box-card"
                 v-for="partition in detail.data.partitions"

        >
          <div slot="header" class="clearfix">
            <span>分区：{{ partition.partition }}</span>
          </div>
          <div><span>Leader:</span>
            <el-tag> Node {{ partition.leader.id }} ({{ partition.leader.host }}:{{ partition.leader.port }})</el-tag>
          </div>
          <div><span>Replicas	:</span>
            <el-tag v-for="rep in partition.replicas"> Node {{ rep.id }} ({{ rep.host }}:{{ rep.port }})</el-tag>
          </div>
          <div><span>Isr	:</span>
            <el-tag v-for="isr in partition.isr"> Node {{ isr.id }} ({{ isr.host }}:{{ isr.port }})</el-tag>
          </div>

        </el-card>
        <div>
          <el-button type="danger"
                     size="default"
                     class="w-full"
                     @click="handleTopicDeleteClick(detail.data.name)">删除Topic</el-button>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<style></style>
