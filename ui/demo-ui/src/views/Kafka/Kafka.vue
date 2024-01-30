<script setup lang="ts">
import {computed, onMounted, reactive, ref, unref, watch} from "vue";
import {
  ElButton,
  ElConfigProvider,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElPagination,
  ElTable,
  ElTableColumn,
  ElTag,
} from "element-plus";
import zhCn from "element-plus/lib/locale/lang/zh-cn";
import * as KafkaApi from "@/api/kafka";
import TopicDetail from "@/views/Kafka/TopicDetail.vue";
import IconRefresh from "~icons/ic/baseline-refresh";
import IconNew from "~icons/carbon/new-tab";
import {useAppStore} from "@/store/modules/app";

const appStore = useAppStore()

const bg_color = ref()
const title_color = ref()
watch(
    () => appStore.isDark,
    () => {
      if (appStore.isDark) {
        bg_color.value = "#262626"
        title_color.value = "#a3a3a3"
      } else {
        bg_color.value = "#f8fafc"
        title_color.value = "#334155"
      }
    }
);

const locale = ref(zhCn);

const clusterObj = reactive<any>({
  tableData: [],
  clusterId: "",
  loading: true,
});

const topicObj = reactive<TableObject<any>>({
  // 单页条数
  pageSize: 5,
  // 当前页
  currentPage: 1,
  // 总条数
  total: 10,
  // 表格数据
  tableList: [],
  // AxiosConfig 配置
  params: {},
  // 加载中
  loading: true,
  // 导出加载中
  exportLoading: false,
  // 当前行的数据
  currentRow: null,
});

const listTopicParamsObj = computed(() => {
  return {
    pageSize: topicObj.pageSize,
    pageIndex: topicObj.currentPage,
  };
});

watch(
    () => topicObj.currentPage,
    () => {
      methods.listTopic();
    }
);

watch(
    () => topicObj.pageSize,
    () => {
      // 当前页不为1时，修改页数后会导致多次调用getList方法
      if (topicObj.currentPage === 1) {
        methods.listTopic();
      } else {
        topicObj.currentPage = 1;
        methods.listTopic();
      }
    }
);

const methods = {
  getCluster: async () => {
    clusterObj.loading = true;

    const res = await KafkaApi.getCluster().finally(() => {
      clusterObj.loading = false;
    });

    if (res) {
      clusterObj.clusterId = res.clusterId
      clusterObj.tableData = []
      const controllerId = res.controller && res.controller.id || "unknown"
      const nodes = res.nodes || []
      nodes.forEach((node: { id: any; host: any; port: any; }) => {
        clusterObj.tableData.push({
          "id": node.id,
          "host": node.host,
          "port": node.port,
          "status": node.id === controllerId ? 'controller' : 'n'
        })
      })
    }
  },

  listTopic: async () => {
    topicObj.loading = true;

    const res = await KafkaApi.listTopic(unref(listTopicParamsObj)).finally(() => {
      topicObj.loading = false;
    });

    if (res) {
      topicObj.tableList = res.data;
      topicObj.total = res.totalCount;
    }
  },
};

// const tableRef = ref();

onMounted(async () => {
  methods.getCluster();
  methods.listTopic();
});


const addTopicDialogFormVisible = ref(false);
const handleTopicAddClick = () => {
  addTopicDialogFormVisible.value = true;
};
const addTopicForm = reactive({
  topicName: "",
  partitions: 1,
  replicationFactor: 1,
});
const addTopicConfirmLoading = ref(false);
const handleTopicAdd = async () => {
  try {
    addTopicConfirmLoading.value = true;
    const newTopic: any = {
      topicName: addTopicForm.topicName,
      partitions: addTopicForm.partitions,
      replicationFactor: addTopicForm.replicationFactor,
    };
    await KafkaApi.createTopic(newTopic);
    ElMessage.success("新增成功");
    addTopicDialogFormVisible.value = false;
    methods.listTopic();
  } finally {
    addTopicConfirmLoading.value = false;
  }
};
const topicDetailRef = ref();
const handleTopicDetailClick = (row: any) => {
  unref(topicDetailRef).showDetail(row);
};


let tableHeaderColorObj = reactive({
      background: bg_color, color: title_color, textAlign: "left"
    }
);


</script>

<template>
  <div class="flex space-x-2 m-2 text-neutral-400">
    <el-config-provider :locale="locale">
      <div
          class="w-1/2 h-[350px] outline outline-1 outline-neutral-400 dark:outline-neutral-700 rounded-md bg-neutral-50 dark:bg-neutral-800"
          v-loading="clusterObj.loading">
        <div class="grid grid-cols-6 rounded-t-md bg-neutral-300 dark:bg-neutral-700 pr-3 pl-3 h-[32px]">
          <el-link type="primary" :underline="false" class="col-start-1 col-end-3 justify-self-start">
            {{ '集群ID: ' + clusterObj.clusterId }}
          </el-link>
          <el-link class="col-start-6 col-end-6 justify-self-end" type="primary" :underline="false"
                   @click="methods.getCluster">
            <icon-refresh class="mr-1"/>
            刷新
          </el-link>
        </div>

        <div>
          <div class="">
            <el-table :data="clusterObj.tableData" :header-cell-style="tableHeaderColorObj" class="min-h-300px"
                      style="width: 100%;" max-height="249" ref="tableRef">
              <el-table-column prop="id" label="节点id" width="150">
              </el-table-column>
              <el-table-column prop="host" label="ip" width="200"/>
              <el-table-column prop="port" label="端口" width="150"/>
              <el-table-column prop="status" label="状态">
                <template #default="scope">
                  <el-tag
                      v-show="scope.row.status==='controller'"
                      :key="scope.row.status"
                      class="ml-1 mb-1"
                      :disable-transitions="false"
                  >
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>

            </el-table>

          </div>


        </div>
      </div>
      <div
          class="w-1/2 h-[350px] outline outline-1 outline-neutral-400 dark:outline-neutral-700 rounded-md bg-neutral-50 dark:bg-neutral-800">
        <div class="grid grid-cols-6 rounded-t-md bg-neutral-300 dark:bg-neutral-700 pr-3 pl-3 h-[32px]">
          <el-link type="primary" :underline="false" class="col-start-1 col-end-1 justify-self-start">Topic信息
          </el-link>
          <el-link type="primary" :underline="false" class="col-start-2 col-end-2 justify-self-start"
                   @click="handleTopicAddClick">
            <icon-new class="mr-1"/>
            新增Topic
          </el-link>

          <el-link class="col-start-6 col-end-6 justify-self-end" type="primary" :underline="false"
                   @click="methods.listTopic">
            <icon-refresh class="mr-1"/>
            刷新
          </el-link>
        </div>

        <div class="flex flex-col justify-between h-[318px]">
          <div>
            <el-table
                :data="topicObj.tableList"
                :header-cell-style="tableHeaderColorObj"
                v-loading="topicObj.loading"
                style="width: 100%; "
                ref="tableRef"
            >
              <el-table-column prop="name" label="Topic名称"/>
              <el-table-column prop="uuid" label="UUID"/>

              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-link
                      type="primary" :underline="false"
                      @click="handleTopicDetailClick(scope.row)"
                  >
                    <div class="font-normal flex items-center">
                      <i-ep-setting class="text-xs mr-1px "/>
                      详情管理
                    </div>
                  </el-link>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="flex justify-end mt-3 mb-3">
            <el-pagination
                v-model:currentPage="topicObj.currentPage"
                v-model:page-size="topicObj.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="topicObj.total"
                :page-sizes="[5, 10, 20, 50]"
                :default-page-size="5"
            >
            </el-pagination>
          </div>
        </div>


      </div>


      <TopicDetail ref="topicDetailRef" @refreshTopicList="methods.listTopic"></TopicDetail>

    </el-config-provider>
  </div>

  <el-dialog v-model="addTopicDialogFormVisible" title="新增Topic" width="600px">
    <el-form :model="addTopicForm">
      <el-form-item label="Topic名称" label-width="80px">
        <el-input v-model="addTopicForm.topicName" autocomplete="off"/>
      </el-form-item>
      <el-form-item label="分区数" label-width="80px">
        <el-input-number v-model="addTopicForm.partitions" :min="1" :max="10" label=""></el-input-number>
      </el-form-item>
      <el-form-item label="副本因子" label-width="80px">
        <el-input-number v-model="addTopicForm.replicationFactor" :min="1" :max="3" label=""></el-input-number>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addTopicDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTopicAdd" :loading="addTopicConfirmLoading">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style>

</style>