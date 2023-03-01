<template>
  <q-chip :color="getMethodColor" text-color="white"
    >{{ props.method.toUpperCase() }}
  </q-chip>
</template>

<script setup lang="ts">
  import { computed } from "vue";
  import { OpenApiMethod } from "src/service/entity/OpenApiAllDetails";

  const props = defineProps({
    method: String,
  });

  const getMethodColor = computed(() => {
    const method: OpenApiMethod = props.method as OpenApiMethod;
    switch (method) {
      case "get":
        return "blue";
      case "post":
        return "green";
      case "put":
        return "orange";
      case "delete":
        return "red";
      case "options":
        return "blue-10";
      case "head":
        return "purple";
      case "patch":
        return "pink-4";
      default:
        return "dark";
    }
  });
</script>

<style scoped></style>
