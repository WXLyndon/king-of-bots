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
          }, 200);
          store.commit("updateGame", data.game);
        } else if (data.event === "move") {
          console.log(data);
          const game = store.state.battle.game_object;
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);
        } else if (data.event === "result") {
          console.log(data);
          const game = store.state.battle.game_object;
          const [snake0, snake1] = game.snakes;

          if (data.loser === "all" || data.loser === "A") {
            snake0.status = "die";
          }

          if (data.loser === "all" || data.loser === "B") {
            snake1.status = "die";
          }
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