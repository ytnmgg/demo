<script setup lang="ts">
import {
  ElDrawer,
  ElTooltip,
  ElMessage,
  ElInput,
  ElButton,
  ElLoading,
  ElSelect,
  ElOption,
  ElSwitch,
  ElPopconfirm,
} from "element-plus";
import * as UserApi from "@/api/user";
import { makeOptionsFromMap } from "@/utils/convert";
import type { SysUser } from "@/api/user/types";
import {
  userSex,
  userTypes,
  transferUserType,
  transferUserStatusBool,
  transferUserSex,
} from "@/api/user/util";

import { useClipboard } from "@vueuse/core";

import { ref, reactive, computed } from "vue";

const { copy } = useClipboard();
const copyWithMessage = (msg) => {
  copy(msg);
  ElMessage.success("拷贝成功");
};

const drawerRef = ref<HTMLElement | null>(null);

const show = ref(false);
let changed = false;

const detail = reactive({
  data: {
    userId: "",
    userType: "",
    userName: "",
    createTime: "",
    updateTime: "",
    nickName: "",
    email: "",
    phonenumber: "",
    sex: "",
    avatar: "",
    status: "",
    statusBool: false,
  },
});

const detailToUpdate = reactive({
  data: {
    userId: "",
    userType: "",
    userName: "",
    createTime: "",
    updateTime: "",
    nickName: "",
    email: "",
    phonenumber: "",
    sex: "",
    avatar: "",
    status: "",
  },
});

const showDetail = (row: SysUser) => {
  show.value = true;
  detail.data = row;
};

const refreshDrawer = async () => {
  const loadingInstance = ElLoading.service({
    target: document.querySelector(".user-edit-drawer") as HTMLElement,
  });

  const user = await UserApi.getById(detail.data.userId);
  detail.data = user;
  detail.data.statusBool = transferUserStatusBool(detail.data.status);

  detailToUpdate.data = {
    ...user,
  };

  loadingInstance.close();
};

const showEdit = reactive({
  nickName: false,
  sex: false,
  userType: false,
  phonenumber: false,
  email: false,
});

const nickNameEditRef = ref<HTMLElement | null>(null);
const sexEditRef = ref<HTMLElement | null>(null);
const userTypeEditRef = ref<HTMLElement | null>(null);
const phonenumberEditRef = ref<HTMLElement | null>(null);
const emailEditRef = ref<HTMLElement | null>(null);

const save = async (el: HTMLElement, item: string) => {
  changed = true;
  const loadingInstance = ElLoading.service({
    target: el,
  });

  const newUser: SysUser = {
    userId: detail.data.userId,
    nickName: detailToUpdate.data.nickName,
    sex: detailToUpdate.data.sex,
    userType: detailToUpdate.data.userType,
    phonenumber: detailToUpdate.data.phonenumber,
    email: detailToUpdate.data.email,
  };

  await UserApi.updateUser(newUser);

  loadingInstance.close();
  showEdit[item as keyof typeof showEdit] = false;

  refreshDrawer();
};

const statusLoading = ref(false);
const statusChangeTitle = computed(() => {
  var t = detail.data.statusBool ? "停用" : "启用";
  return "确定" + t + "该用户？";
});
const onStatusSwitchChange = async () => {
  changed = true;
  statusLoading.value = true;

  detail.data.statusBool = !detail.data.statusBool;

  const newUser: SysUser = {
    userId: detail.data.userId,
    status: detail.data.statusBool ? "0" : "1",
  };
  await UserApi.updateUser(newUser);

  statusLoading.value = false;
  refreshDrawer();
};

const cancelSave = (item: string) => {
  detailToUpdate.data = {
    ...detail.data,
  };
  showEdit[item as keyof typeof showEdit] = false;
};

const onOpened = () => {
  refreshDrawer();
};

defineExpose({
  showDetail,
});

const emits = defineEmits(["refreshList"]);
const handelClosed = () => {
  if (changed) {
    changed = false;
    emits("refreshList");
  }
};
</script>

<template>
  <el-drawer
    ref="drawerRef"
    v-model="show"
    title="用户详情"
    @opened="onOpened"
    @closed="handelClosed"
  >
    <div class="user-edit-drawer">
      <div class="space-y-4">
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">姓名</div>

          <div class="text-gray-500">{{ detail.data.userName }}</div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">ID</div>
          <div class="text-gray-500">{{ detail.data.userId }}</div>
          <el-tooltip
            class="box-item"
            effect="dark"
            content="拷贝到剪贴板"
            placement="top"
          >
            <template #default="scope">
              <i-ep-copy-document
                class="cursor-pointer self-center text-gray-500 text-sm"
                @click="copyWithMessage(detail.data.userId)"
              />
            </template>
          </el-tooltip>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">创建时间</div>
          <div class="text-gray-500">
            {{ detail.data.createTime }}
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">修改时间</div>
          <div class="text-gray-500">
            {{ detail.data.updateTime }}
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">昵称</div>
          <div v-show="!showEdit.nickName" class="flex space-x-4">
            <div class="text-gray-500">
              {{ detail.data.nickName }}
            </div>

            <el-tooltip class="box-item" effect="dark" content="编辑" placement="top">
              <template #default="scope">
                <i-ep-edit
                  class="cursor-pointer self-center text-gray-500 text-sm"
                  @click="showEdit.nickName = true"
                />
              </template>
            </el-tooltip>
          </div>
          <div v-show="showEdit.nickName" ref="nickNameEditRef">
            <div class="flex space-x-4">
              <el-input v-model="detailToUpdate.data.nickName"></el-input>
              <el-button
                link
                type="primary"
                size="small"
                @click="save(nickNameEditRef!, 'nickName')"
                >保存</el-button
              >
              <el-button link type="primary" size="small" @click="cancelSave('nickName')"
                >取消</el-button
              >
            </div>
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">性别</div>
          <div v-show="!showEdit.sex" class="flex space-x-4">
            <div class="text-gray-500">
              {{ transferUserSex(detail.data.sex) }}
            </div>
            <el-tooltip class="box-item" effect="dark" content="编辑" placement="top">
              <template #default="scope">
                <i-ep-edit
                  class="cursor-pointer self-center text-gray-500 text-sm"
                  @click="showEdit.sex = true"
                />
              </template>
            </el-tooltip>
          </div>
          <div v-show="showEdit.sex" ref="sexEditRef">
            <div class="flex space-x-4">
              <el-select
                v-model="detailToUpdate.data.sex"
                placeholder=" "
                class="w-20"
                size="default"
              >
                <el-option
                  v-for="item in makeOptionsFromMap(userSex)"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <el-button
                link
                type="primary"
                size="small"
                @click="save(sexEditRef!, 'sex')"
                >保存</el-button
              >
              <el-button link type="primary" size="small" @click="cancelSave('sex')"
                >取消</el-button
              >
            </div>
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">用户类型</div>
          <div v-show="!showEdit.userType" class="flex space-x-4">
            <div class="text-gray-500">
              {{ transferUserType(detail.data.userType) }}
            </div>
            <el-tooltip class="box-item" effect="dark" content="编辑" placement="top">
              <template #default="scope">
                <i-ep-edit
                  class="cursor-pointer self-center text-gray-500 text-sm"
                  @click="showEdit.userType = true"
                />
              </template>
            </el-tooltip>
          </div>
          <div v-show="showEdit.userType" ref="userTypeEditRef">
            <div class="flex space-x-4">
              <el-select
                v-model="detailToUpdate.data.userType"
                placeholder=" "
                class="w-30"
                size="default"
              >
                <el-option
                  v-for="item in makeOptionsFromMap(userTypes)"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <el-button
                link
                type="primary"
                size="small"
                @click="save(userTypeEditRef!, 'userType')"
                >保存</el-button
              >
              <el-button link type="primary" size="small" @click="cancelSave('userType')"
                >取消</el-button
              >
            </div>
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">状态</div>
          <div class="flex space-x-4">
            <el-popconfirm
              confirm-button-text="确定"
              cancel-button-text="取消"
              :title="statusChangeTitle"
              @confirm="onStatusSwitchChange"
            >
              <template #reference>
                <el-switch
                  :value="detail.data.statusBool"
                  :loading="statusLoading"
                  width="60px"
                  inline-prompt
                  style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                  active-text="正常"
                  inactive-text="停用"
                />
              </template>
            </el-popconfirm>
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">电话号码</div>
          <div v-show="!showEdit.phonenumber" class="flex space-x-4">
            <div class="text-gray-500">
              {{ detail.data.phonenumber }}
            </div>
            <el-tooltip class="box-item" effect="dark" content="编辑" placement="top">
              <template #default="scope">
                <i-ep-edit
                  class="cursor-pointer self-center text-gray-500 text-sm"
                  @click="showEdit.phonenumber = true"
                />
              </template>
            </el-tooltip>
          </div>
          <div v-show="showEdit.phonenumber" ref="phonenumberEditRef">
            <div class="flex space-x-4">
              <el-input v-model="detailToUpdate.data.phonenumber"></el-input>
              <el-button
                link
                type="primary"
                size="small"
                @click="save(phonenumberEditRef!, 'phonenumber')"
                >保存</el-button
              >
              <el-button
                link
                type="primary"
                size="small"
                @click="cancelSave('phonenumber')"
                >取消</el-button
              >
            </div>
          </div>
        </div>
        <div class="flex space-x-4">
          <div class="text-gray-800 min-w-100px">邮箱</div>
          <div v-show="!showEdit.email" class="flex space-x-4">
            <div class="text-gray-500">
              {{ detail.data.email }}
            </div>
            <el-tooltip class="box-item" effect="dark" content="编辑" placement="top">
              <template #default="scope">
                <i-ep-edit
                  class="cursor-pointer self-center text-gray-500 text-sm"
                  @click="showEdit.email = true"
                />
              </template>
            </el-tooltip>
          </div>
          <div v-show="showEdit.email" ref="emailEditRef">
            <div class="flex space-x-4">
              <el-input v-model="detailToUpdate.data.email"></el-input>
              <el-button
                link
                type="primary"
                size="small"
                @click="save(emailEditRef!, 'email')"
                >保存</el-button
              >
              <el-button link type="primary" size="small" @click="cancelSave('email')"
                >取消</el-button
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<style></style>
