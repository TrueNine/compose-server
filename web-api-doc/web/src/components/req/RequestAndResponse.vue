<template>
  <q-tabs align="left" v-model="activeTab">
    <q-tab name="Params" label="Params" />
    <q-tab name="Body" label="Body" />
    <q-tab name="Header" label="Header" />
    <q-tab name="Cookies" label="Cookies" />
    <q-tab name="Authentication" label="Authentication" />
  </q-tabs>

  <q-tab-panels keep-alive v-model="activeTab">
    <!-- query参数 -->
    <q-tab-panel name="Params">
      <key-val-json-input-pair v-model:obj="reqObj" />
    </q-tab-panel>
    <q-tab-panel name="Body">
      <div class="q-gutter-sm">
        <q-radio v-model="activeBody" val="none" label="无Body" />
        <q-radio v-model="activeBody" val="application/json" label="JSON" />
        <q-radio v-model="activeBody" val="x-www-form-urlencoded" label="X-Www-Form-Urlencoded" />
        <q-radio v-model="activeBody" val="application/octet-stream" label="binary" />
      </div>
    </q-tab-panel>
    <q-tab-panel name="Header"> 33333333333</q-tab-panel>
    <q-tab-panel name="Cookies"> 444444444444</q-tab-panel>
    <q-tab-panel name="Authentication">555555555555555</q-tab-panel>
    <q-tab-panel name="Binary">555555555555555</q-tab-panel>
  </q-tab-panels>
  <!-- 请求按钮 -->
  <div style="margin-block: 2rem" class="row justify-center z-marginals">
    <q-btn :loading="activeRequestLoading" @click="request" style="width: 50%" class="text-h6" color="primary">请求</q-btn>
  </div>
  <!-- 响应区域 -->
  <q-card>
    <q-card-section class="row">
      <response-code-view :time="timeConsuming" :response="reqObj" />
    </q-card-section>
  </q-card>
</template>

<script setup lang="ts">
  import KeyValJsonInputPair from "components/input/KeyValJsonInputPair.vue";
  import { PropType, ref } from "vue";

  import { req } from "components/req/ApiRequest";
  import { OpenApiMethod } from "src/service/entity/OpenApiAllDetails";
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
<style scoped lang="scss"></style>
