<template>
  <PlayGround v-if="$store.state.battle.status === 'playing'" />
  <MatchGround v-if="$store.state.battle.status === 'matching'" />
</template>

<script>
import PlayGround from "@/components/PlayGround.vue";
import MatchGround from "@/components/MatchGround.vue";
import { onMounted, onUnmounted } from "vue";
import { useStore } from "vuex";

export default {
  components: { PlayGround, MatchGround },

  setup() {
    const store = useStore();
    store.dispatch("getInfo", {
      success() {},
    });

    const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;

    let socket = null;

    onMounted(() => {
      store.commit("updateOpponent", {
        username: "My Opponent",
        photo:
          "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      });
      socket = new WebSocket(socketUrl);

      socket.onopen = () => {
        console.log("Connected!");
        store.commit("updateSocket", socket);
      };

      socket.onmessage = (message) => {
        const data = JSON.parse(message.data);
        if (data.event === "start-matching") {
          // Matching successfully
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          setTimeout(() => {
            store.commit("updateStatus", "playing");
          }, 2000);
          store.commit("updateGamemap", data.gamemap);
        }
      };

      socket.onclose = () => {
        console.log("Disconnected!");
      };
    });

    onUnmounted(() => {
      socket.close();
      store.commit("updateStatus", "matching");
    });
  },
};
</script>

<style scoped>
</style>