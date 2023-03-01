<template>
  <key-val-json-input
    v-for="(item, index) in entities"
    :key="index"
    v-model:kv="entities[index]"
    :del-disable="index !== entities.length - 1"
    @add="add"
    @del="del(index)"
  />
</template>
<script lang="ts" setup>
import KeyValJsonInput from "components/input/KeyValJsonInput.vue";
import {PropType, ref, watch} from "vue";

const emits = defineEmits(["update:obj"]);
const props = defineProps({
  obj: {
    type: Object as PropType<{ [k in string]: unknown }>,
  },
});

const entities = ref<{ [k in string]: unknown }[]>([{}]);

const add = () => {
  entities.value.push({});
};

const del = (index: number) => {
  if (entities.value.length > 1) {
    entities.value.splice(index, 1);
  }
};

const mergedObject = () => {
  let a: { [k in string]: unknown } = {};
  entities.value.forEach((e) => {
    a = Object.assign(a, e);
  });
  return a;
};

watch(
  () => [...entities.value],
  () => {
    emits("update:obj", mergedObject());
  }
);
</script>
