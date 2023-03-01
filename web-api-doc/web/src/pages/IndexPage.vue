<template>
  <q-layout view="lHh Lpr lFf">
    <!-- header -->
    <q-header elevated>
      <q-toolbar>
        <q-btn aria-label="Menu" dense flat icon="menu" round
               @click="toggleLeftDrawer"/>
        <q-toolbar-title>OPENAPI-UI</q-toolbar-title>
        <q-btn :icon="themeTag ? `bi-moon` : `bi-brightness-high`" color="secondary" round
               @click="dartSwitch"/>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <!-- 信息栏 -->
      <q-card v-if="activeGroupKey === `homePage`">
        <q-card-section>
          <q-toolbar-title class="text-h3">{{
              openApiInfo.title
            }}
          </q-toolbar-title>
        </q-card-section>
        <q-separator/>
        <q-card-section>
          <div class="row col-2">
            <span class="col">
              <strong class="text-h5">接口数量: </strong>
              <span class="text-h6">{{ openApiCount }}</span>
            </span>
            <span class="col">
              <strong class="text-h5">API协议: </strong>
              <q-chip :label="openApiInfo.license.name" color="primary"
                      text-color="white"> </q-chip>
            </span>
          </div>

          <div class="row col-2">
            <span class="col">
              <strong class="text-h5">版本: </strong>
              <span class="text-h6">{{ openApiInfo.version }}</span>
            </span>
            <span class="col">
              <strong class="text-h5">服务条款: </strong>
              <span class="text-h6">
                {{ openApiInfo.termsOfService }}
              </span>
            </span>
          </div>
          <q-separator/>
          <div class="row">
            <span class="text-dark-separator">{{
                openApiInfo.description
              }}</span>
          </div>
        </q-card-section>
      </q-card>

      <q-page>
        <div v-if="activeGroupKey !== `` && activeGroupKey !== `homePage`">
          <open-api-item
            v-for="item in openApiPaths[activeGroupKey]"
            :key="item"
            :base-uri="currentUrl"
            :desc="item.description"
            :function-name="item.operationId"
            :method="item.method"
            :summary="item.summary"
            :uri="item.uri"
          ></open-api-item>
        </div>
      </q-page>

      <!-- 左侧抽屉菜单 -->
      <q-drawer v-model="openDrawer" class="shadow-17" show-if-above>
        <q-list>
          <q-item-label v-if="openDrawer" header>API分组</q-item-label>

          <!-- api 分组列表 -->
          <q-item v-for="k in openApiGroupKeys" :key="k"
                  v-ripple :active="activeGroupKey === k" clickable @click="activeGroupKey = k">
            <q-item-section avatar>
              <q-avatar color="primary" icon="details" rounded
                        text-color="white"/>
            </q-item-section>

            <q-item-section v-if="openDrawer">
              {{ homePageAlias(k) }}
            </q-item-section>
          </q-item>
        </q-list>
      </q-drawer>
    </q-page-container>
  </q-layout>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {
  OpenApiInfo,
  OpenApiRequest
} from "src/service/entity/OpenApiAllDetails";
import {OpenApiDetailsService} from "src/service/OpenApi3Service";
import OpenApiItem from "components/openapi/OpenApiItem.vue";
import {useQuasar} from "quasar";

const activeGroupKey = ref<string>("homePage");
const openApiGroupKeys = ref<string[]>(["homePage"]);
const openApiPaths = ref<{ [uri: string]: OpenApiRequest[] }>();
const openApiCount = ref<number>(0);
const openApiInfo = ref<OpenApiInfo>({
  license: {
    name: "",
    url: "",
  },
} as OpenApiInfo);

const currentUrl = window.location.origin;
OpenApiDetailsService.all().then((d) => {
  openApiPaths.value = OpenApiDetailsService.paths(d);
  openApiInfo.value = OpenApiDetailsService.info(d);
  Object.keys(openApiPaths.value).forEach((k) => {
    openApiGroupKeys.value.push(k);
    if (openApiPaths.value && openApiPaths.value[k]) {
      openApiCount.value += openApiPaths.value[k].length;
    }
  });
});

const openDrawer = ref<boolean>(false);
const toggleLeftDrawer = () => {
  openDrawer.value = !openDrawer.value;
};
const theme = useQuasar();
const themeTag = ref(theme.dark.isActive);
const dartSwitch = () => {
  theme.dark.toggle();
  themeTag.value = !themeTag.value;
};

const homePageAlias = (name: string): string => {
  return name === "homePage" ? "首页" : name;
};
</script>

<style lang="scss" scoped>
@use "src/css/app.scss";

.list-active-bg {
  background-color: $primary;
  color: white;
}
</style>
