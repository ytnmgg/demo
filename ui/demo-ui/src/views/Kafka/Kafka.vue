<script setup lang="ts">
import {computed, onMounted, reactive, ref, unref, watch} from "vue";
import {
  ElButton,
  ElCard,
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


const locale = ref(zhCn);

const clusterObj = reactive<any>({
  tableData: [],
  clusterId: "",
  // 加载中
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

const handleModifyClick = (row: any) => {
  // unref(roleCreateRef).showDialog(row);
};

const handleDeleteClick = async (row: any) => {
  // ElMessageBox.confirm("确定删除当前角色码? 该操作不可逆！", "警告", {
  //   type: "warning",
  // })
  //     .then(async () => {
  //       await RoleApi.deleteById({ roleId: row.roleId });
  //       ElMessage.success("删除角色成功");
  //       refreshRoleList();
  //     })
  //     .catch(() => {
  //       // catch error
  //     });
};

//
// const refreshList = () => {
//   methods.getList();
// };
//
// const addDialogFormVisible = ref(false);
// const addForm = reactive({
//   permissionName: "",
//   permissionKey: "",
// });
//
// const addConfirmLoading = ref(false);
</script>

<template>
  <div class="flex m-2 text-neutral-400">
    <el-config-provider :locale="locale">
      <div class="m-1 p-3 w-1/2 bg-neutral-800">
        <div class="mb-3">集群信息</div>
        <div>
          <el-card class="box-card"
                   v-loading="clusterObj.loading"
          >
            <div slot="header" class="clearfix mb-3 text-neutral-400">
              <span>{{ '集群：' + clusterObj.clusterId }}</span>
              <el-link style="float: right; padding: 3px 0" type="primary" :underline="false"
                       @click="methods.getCluster">刷新
              </el-link>
            </div>
            <el-table :data="clusterObj.tableData" style="width: 100%" ref="tableRef">
              <el-table-column prop="id" label="节点id" width="200"/>
              <el-table-column prop="host" label="ip" width="200"/>
              <el-table-column prop="port" label="端口" width="200"/>
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

          </el-card>


        </div>
      </div>
      <div class="m-1 p-3 w-1/2 bg-neutral-800">
        <div class="mb-3">Topic信息</div>
        <div>
          <el-card class="box-card"
          >
            <div class="flex items-center justify-between mb-3">
              <div>
                <el-button plain type="primary" @click="handleTopicAddClick">
                  <i-ep-circle-plus class="mr-5px"/>
                  新增
                </el-button>
              </div>

              <div>
                <el-link type="primary" :underline="false"
                         @click="methods.listTopic()">刷新
                </el-link>
              </div>
            </div>
            <div>
              <div>
                <el-table
                    :data="topicObj.tableList"
                    v-loading="topicObj.loading"
                    style="width: 100%"
                    ref="tableRef"
                >
                  <el-table-column prop="name" label="Topic名称"/>
                  <el-table-column prop="uuid" label="UUID"/>

                  <el-table-column fixed="right" label="操作" width="120">
                    <template #default="scope">
                      <el-button
                          link
                          type="primary"
                          size="small"
                          @click="handleTopicDetailClick(scope.row)"
                      >
                        <div class="font-normal flex items-center">
                          <i-ep-setting class="text-xs mr-1px "/>
                          详情管理
                        </div>
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <div class="flex justify-end mt-3">
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


          </el-card>

          <TopicDetail ref="topicDetailRef" @refreshTopicList="methods.listTopic"></TopicDetail>

        </div>
      </div>
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