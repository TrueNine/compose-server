<template>
  <q-form class="row justify-center">
    <q-input
      style="padding-inline: 0.5rem"
      class="col-2"
      label="参数名"
      v-model="key"
    />
    <q-select
      @popup-hide="selectAndClean"
      label="值类型"
      class="col-2"
      :options="typeOptions"
      v-model="activeType"
    />
    <q-input
      style="padding-inline: 0.5rem"
      class="col-6"
      v-if="!valTypeIsBoolean"
      :type="valType()"
      :label="activeType === `String` ? `参数值（字符串）` : `参数（数值）`"
      v-model="val"
    />
    <q-select
      label="参数值（布尔类型）"
      class="col-6"
      :options="boolOptions"
      v-model="val"
      v-else
    />
    <q-btn @click="addEvent" class="col-1" icon="add" />
    <q-btn
      :disable="props.delDisable"
      @click="delEvent"
      class="col-1"
      icon="delete"
    />
  </q-form>
</template>

<script setup lang="ts">
  import { computed, PropType, ref, watch } from "vue";

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
