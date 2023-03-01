<template>
  <q-tabs v-model="activeTab" align="left">
    <q-tab label="Params" name="Params"/>
    <q-tab label="Body" name="Body"/>
    <q-tab label="Header" name="Header"/>
    <q-tab label="Cookies" name="Cookies"/>
    <q-tab label="Authentication" name="Authentication"/>
  </q-tabs>

  <q-tab-panels v-model="activeTab" keep-alive>
    <!-- query参数 -->
    <q-tab-panel name="Params">
      <key-val-json-input-pair v-model:obj="reqObj"/>
    </q-tab-panel>
    <q-tab-panel name="Body">
      <div class="q-gutter-sm">
        <q-radio v-model="activeBody" label="无Body" val="none"/>
        <q-radio v-model="activeBody" label="JSON" val="application/json"/>
        <q-radio v-model="activeBody" label="X-Www-Form-Urlencoded"
                 val="x-www-form-urlencoded"/>
        <q-radio v-model="activeBody" label="binary"
                 val="application/octet-stream"/>
      </div>
    </q-tab-panel>
    <q-tab-panel name="Header"> 33333333333</q-tab-panel>
    <q-tab-panel name="Cookies"> 444444444444</q-tab-panel>
    <q-tab-panel name="Authentication">555555555555555</q-tab-panel>
    <q-tab-panel name="Binary">555555555555555</q-tab-panel>
  </q-tab-panels>
  <!-- 请求按钮 -->
  <div class="row justify-center z-marginals" style="margin-block: 2rem">
    <q-btn :loading="activeRequestLoading" class="text-h6" color="primary"
           style="width: 50%" @click="request">请求
    </q-btn>
  </div>
  <!-- 响应区域 -->
  <q-card>
    <q-card-section class="row">
      <response-code-view :response="reqObj" :time="timeConsuming"/>
    </q-card-section>
  </q-card>
</template>

<script lang="ts" setup>
import KeyValJsonInputPair from "components/input/KeyValJsonInputPair.vue";
import {PropType, ref} from "vue";

import {req} from "components/req/ApiRequest";
import {OpenApiMethod} from "src/service/entity/OpenApiAllDetails";
import ResponseCodeView from "components/code/ResponseCodeView.vue";

const props = defineProps({
  method: {
    type: String as PropType<OpenApiMethod>,
    required: true,
  },
  uri: {
    type: String,
    required: true,
  },
});
const activeRequestLoading = ref(false);
const activeBody = ref("");
const reqObj = ref<unknown>({});

// 初始化输入方式
let defaultActiveMenu = "Header";
if (props.method) {
  defaultActiveMenu = props.method === "get" ? "Params" : "Body";
}

const activeTab = ref(defaultActiveMenu);
const timeConsuming = ref(0);

const request = () => {
  const startMillis = Date.now();
  activeRequestLoading.value = true;
  req({
    method: props.method,
    url: props.uri,
  })
    .then((d) => {
      activeRequestLoading.value = false;
      reqObj.value = d;
      timeConsuming.value = Date.now() - startMillis;
    })
    .catch((e) => {
      activeRequestLoading.value = false;
      reqObj.value = e;
      timeConsuming.value = Date.now() - startMillis;
    });
};
</script>
<style lang="scss" scoped></style>
