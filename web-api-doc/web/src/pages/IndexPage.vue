<template>
  <q-layout view="lHh Lpr lFf">
    <!-- header -->
    <q-header elevated>
      <q-toolbar>
        <q-btn @click="toggleLeftDrawer" flat dense round icon="menu" aria-label="Menu" />
        <q-toolbar-title>OPENAPI-UI</q-toolbar-title>
        <q-btn @click="dartSwitch" color="secondary" round :icon="themeTag ? `bi-moon` : `bi-brightness-high`" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <!-- 信息栏 -->
      <q-card v-if="activeGroupKey === `homePage`">
        <q-card-section>
          <q-toolbar-title class="text-h3">{{ openApiInfo.title }}</q-toolbar-title>
        </q-card-section>
        <q-separator />
        <q-card-section>
          <div class="row col-2">
            <span class="col">
              <strong class="text-h5">接口数量: </strong>
              <span class="text-h6">{{ openApiCount }}</span>
            </span>
            <span class="col">
              <strong class="text-h5">API协议: </strong>
              <q-chip text-color="white" :label="openApiInfo.license.name" color="primary"> </q-chip>
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
          <q-separator />
          <div class="row">
            <span class="text-dark-separator">{{ openApiInfo.description }}</span>
          </div>
        </q-card-section>
      </q-card>

      <q-page>
        <div v-if="activeGroupKey !== `` && activeGroupKey !== `homePage`">
          <open-api-item
            :key="item"
            v-for="item in openApiPaths[activeGroupKey]"
            :method="item.method"
            :function-name="item.operationId"
            :base-uri="currentUrl"
            :uri="item.uri"
            :summary="item.summary"
            :desc="item.description"
          ></open-api-item>
        </div>
      </q-page>

      <!-- 左侧抽屉菜单 -->
      <q-drawer class="shadow-17" v-model="openDrawer" show-if-above>
        <q-list>
          <q-item-label v-if="openDrawer" header>API分组</q-item-label>

          <!-- api 分组列表 -->
          <q-item @click="activeGroupKey = k" :active="activeGroupKey === k" v-ripple clickable :key="k" v-for="k in openApiGroupKeys">
            <q-item-section avatar>
              <q-avatar rounded color="primary" text-color="white" icon="details" />
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

<script setup lang="ts">
  import { ref } from "vue";
  import { OpenApiInfo, OpenApiRequest } from "src/service/entity/OpenApiAllDetails";
  import { OpenApiDetailsService } from "src/service/OpenApi3Service";
  import OpenApiItem from "components/openapi/OpenApiItem.vue";
  import { useQuasar } from "quasar";

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

<style scoped lang="scss">
  @use "src/css/app.scss";

  .list-active-bg {
    background-color: $primary;
    color: white;
  }
</style>
