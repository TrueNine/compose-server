<template>
  <!-- 复制代码 -->
  <q-btn v-if="code !== `` && code !== `{}`" icon="save"
         style="margin-block: 1rem"
         @click="copyCode">复制响应内容
  </q-btn>
  <!-- 响应结果 -->
  <div class="row code-box">
    <pre class="col-7 code-panel">
        <code ref="codeElement" v-html="code"/>
    </pre>
    <div class="col-5 bg-dark shadow-14">
      <q-list class="response-box">
        <q-item-label>响应结果状态</q-item-label>

        <q-item v-if="response.code">
          <q-item-section> 响应码:</q-item-section>
          <q-item-section>
            <span></span>
            <q-chip :color="response.code > 399 ? `red` : `green`"
                    text-color="dark">{{ props.response.code }}
            </q-chip>
          </q-item-section>
          <q-item-section> 耗时:</q-item-section>
          <q-item-section
          ><span>
              <q-chip :color="props.time > 5 ? `yellow` : `green`"
                      outline>{{ props.time }} ms</q-chip>
            </span></q-item-section
          >
        </q-item>
        <q-item v-else>
          <q-item-section>
            <div class="disabled">没有进行请求</div>
          </q-item-section>
        </q-item>
        <!-- 响应头折叠 -->
        <q-item>
          <q-item-section>
            <q-expansion-item>
              <template #header>
                <q-item>
                  <q-item-section> 响应头</q-item-section>
                  <q-item-section>
                    <span>
                      <q-chip color="green" outline>{{
                          headerSize(props.response.headers)
                        }}</q-chip>
                    </span>
                  </q-item-section>
                </q-item>
              </template>
              <q-card class="header-card-box">
                <q-card-section>
                  <q-list>
                    <q-item v-for="(v, k) in props.response.headers" :key="k"
                            clickable>
                      <q-item-section> {{ wrapperHeader(k) }}</q-item-section>
                      <q-item-section>{{ v }}</q-item-section>
                    </q-item>
                  </q-list>
                </q-card-section>
              </q-card>
            </q-expansion-item>
          </q-item-section>
        </q-item>
      </q-list>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {PropType, ref, watch} from "vue";
import {formatCodeToHtml, Lang} from "src/plugins/Hijs";
import {OpenApiResponse} from "src/service/entity/OpenApiAllDetails";
import {wrapperHeader} from "components/code/LoverCaseHeaderToUpper";

const props = defineProps({
  response: {
    type: Object as PropType<OpenApiResponse>,
    required: true,
  },
  time: {
    type: Number as PropType<number>,
    required: true,
    default: 0,
  },
});

// 复制代码
const codeElement = ref<HTMLElement>(null as unknown as HTMLElement);
const copyCode = () => {
  const text = codeElement.value.textContent;
  window.navigator.clipboard.writeText(text ? text.trim() : "");
};

// 请求头数量统计
const headerSize = (headers: Record<string, unknown> | undefined): number => {
  if (headers) {
    return Object.keys(headers).length;
  } else {
    return 0;
  }
};

// 响应数据渲染
const code = ref("");
const parseLang = (mime?: string): Lang => {
  switch (mime) {
    case "application/json":
      return "json";
    case "text/html":
    case "text/xml":
      return "xml";
    case "text/javascript":
      return "javascript";
    default:
      return "json";
  }
};

watch(
  () => props.response,
  (r) => {
    code.value = formatCodeToHtml(r.data, parseLang(r.type));
  },
  {deep: true}
);
</script>

<style lang="scss" scoped>
@use "src/css/quasar.variables";

.code-box {
  width: 100%;
  border-radius: 5px;
  word-break: break-all;
  padding: 1rem;
  background-color: #2b2b2b;
  color: white;

  .header-card-box {
    background: #2b2b2b;
  }
}

.response-box {
  padding: 1rem;
}
</style>
