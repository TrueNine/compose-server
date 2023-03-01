<template>
  <router-view />
</template>

<script setup lang="ts">
  import { useQuasar } from "quasar";

  let darMode = useQuasar();
  darMode.dark.mode = "auto";
</script>
