<template>
  <q-expansion-item>
    <template #header>
      <q-item-section avatar>
        <open-api-method-color-tag :method="props.method"/>
      </q-item-section>
      <q-item-section>
        <strong>{{ props.uri }}</strong>
      </q-item-section>
      <q-item-section>
        {{ renderFunctionName }}
      </q-item-section>
      <q-item-section>
        <q-chip :disable="!props.summary" square
        >{{ props.summary ? props.summary : `...` }}
          <q-tooltip v-if="props.desc">
            {{ props.desc ? props.desc : `...` }}
          </q-tooltip>
        </q-chip>
      </q-item-section>
    </template>
    <!-- 请求器 -->
    <request-and-response :method="props.method" :uri="props.uri"/>
  </q-expansion-item>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import "highlight.js/styles/atom-one-dark.css";
import OpenApiMethodColorTag
  from "components/openapi/OpenApiMethodColorTag.vue";
import RequestAndResponse from "components/req/RequestAndResponse.vue";

const props = defineProps({
  baseUri: String,
  method: String,
  desc: String,
  uri: String,
  summary: String,
  functionName: String,
});

const renderFunctionName = computed(() => {
  return `${props.functionName}`.substring(0, `${props.functionName}`.indexOf("_") !== -1 ? `${props.functionName}`.indexOf("_") : undefined);
});
</script>
