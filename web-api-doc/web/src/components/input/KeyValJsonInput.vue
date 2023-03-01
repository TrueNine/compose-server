<template>
  <q-form class="row justify-center">
    <q-input
      v-model="key"
      class="col-2"
      label="参数名"
      style="padding-inline: 0.5rem"
    />
    <q-select
      v-model="activeType"
      :options="typeOptions"
      class="col-2"
      label="值类型"
      @popup-hide="selectAndClean"
    />
    <q-input
      v-if="!valTypeIsBoolean"
      v-model="val"
      :label="activeType === `String` ? `参数值（字符串）` : `参数（数值）`"
      :type="valType()"
      class="col-6"
      style="padding-inline: 0.5rem"
    />
    <q-select
      v-else
      v-model="val"
      :options="boolOptions"
      class="col-6"
      label="参数值（布尔类型）"
    />
    <q-btn class="col-1" icon="add" @click="addEvent"/>
    <q-btn
      :disable="props.delDisable"
      class="col-1"
      icon="delete"
      @click="delEvent"
    />
  </q-form>
</template>

<script lang="ts" setup>
import {computed, PropType, ref, watch} from "vue";

const emits = defineEmits<{
  (e: "update:kv", v: unknown): void;
  (e: "add"): void;
  (e: "del"): void;
}>();
const props = defineProps({
  fixedKey: {
    type: String,
    default: "",
  },
  kv: {
    type: Object as PropType<{ [k in string]: unknown }>,
    default: () => ({}),
  },
  delDisable: Boolean,
});

const key = ref("");
const val = ref("");
const activeType = ref("String");
const typeOptions = ref(["String", "Boolean", "Number"]);
const boolOptions = ref(["true", "false"]);

function typeWrapper() {
  switch (activeType.value) {
    case "Boolean":
      if (val.value.toLowerCase() === "true") {
        return true;
      } else if (val.value.toLowerCase() === "false") {
        return false;
      }
      break;
    case "Number":
      return val.value.includes(".")
        ? parseFloat(val.value)
        : parseInt(val.value);
    default:
      return val.value;
  }
}

const valTypeIsBoolean = computed(() => activeType.value === "Boolean");

function valType(): "text" | "number" {
  let typ: "text" | "number" = "text";
  if (activeType.value === "String") {
    typ = "text";
  }
  if (activeType.value !== "String") {
    typ = "number";
  }
  return typ;
}

function wrapper() {
  if (key.value && val.value) {
    return {
      [key.value]: typeWrapper(),
    };
  } else {
    return {};
  }
}

function addEvent() {
  emits("add");
}

function delEvent() {
  emits("del");
}

let oldType = activeType.value;

function selectAndClean() {
  if (oldType !== activeType.value) {
    val.value = "";
    oldType = activeType.value;
  }
}

watch(
  () => [key.value, val.value, activeType.value],
  () => {
    emits("update:kv", wrapper());
  }
);
</script>

<style scoped></style>
