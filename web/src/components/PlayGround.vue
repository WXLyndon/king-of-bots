<template>
  <div class="container">
    <div class="row">
      <div class="col-2">
        <div class="card" style="margin: 80px auto">
          <div class="card-body">
            <img :src="a_photo" alt="" style="width: 100%" />
            <div class="playground-username" style="color: blue">
              {{ a_username }} (Blue)
            </div>
          </div>
        </div>
      </div>
      <div class="col-8">
        <div class="playground"><GameMap /></div>
      </div>
      <div class="col-2">
        <div class="card" style="margin: 80px auto">
          <div class="card-body">
            <img :src="b_photo" alt="" style="width: 100%" />
            <div class="playground-username" style="color: red">
              {{ b_username }} (Red)
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import GameMap from "./GameMap.vue";
import { useStore } from "vuex";
import { ref } from "vue";

export default {
  components: {
    GameMap,
  },

  setup() {
    const store = useStore();
    let a_username = ref("");
    let a_photo = ref("");
    let b_username = ref("");
    let b_photo = ref("");

    if (store.state.record.is_record) {
      a_username.value = store.state.record.a_username;
      a_photo.value = store.state.record.a_photo;
      b_username.value = store.state.record.b_username;
      b_photo.value = store.state.record.b_photo;
    } else {
      const userId = parseInt(store.state.user.id);
      const aId = store.state.battle.a_id;
      const bId = store.state.battle.b_id;

      if (userId === aId) {
        a_username.value = store.state.user.username;
        a_photo.value = store.state.user.photo;
        b_username.value = store.state.battle.opponent_username;
        b_photo.value = store.state.battle.opponent_photo;
      } else if (userId === bId) {
        a_username.value = store.state.battle.opponent_username;
        a_photo.value = store.state.battle.opponent_photo;
        b_username.value = store.state.user.username;
        b_photo.value = store.state.user.photo;
      }
    }

    return { a_username, a_photo, b_username, b_photo };
  },
};
</script>

<style scoped>
div.playground {
  height: 100%;
  margin: 40px auto;
}

div.playground-username {
  margin-top: 2vh;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
}
</style>