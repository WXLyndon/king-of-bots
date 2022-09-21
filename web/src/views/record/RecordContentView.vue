<template>
  <PlayGround v-if="$store.state.record.is_record === true" />
</template>
  
  <script>
import PlayGround from "@/components/PlayGround.vue";
import { useStore } from "vuex";
import router from "../../router/index";

export default {
  components: { PlayGround },

  setup() {
    const store = useStore();

    if (store.state.record.a_steps === "") {
      router.replace({ name: "record_index" });
    }
  },
};
</script>
  
<style scoped>
</style>