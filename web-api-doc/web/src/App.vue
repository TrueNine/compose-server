<template>
  <router-view/>
</template>

<script lang="ts" setup>
import {useQuasar} from "quasar";

let darMode = useQuasar();
darMode.dark.mode = "auto";
</script>
