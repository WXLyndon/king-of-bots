export default {
  state: {
    is_record: false,
    a_steps: "",
    b_steps: "",
    record_loser: "",
    a_photo: "",
    a_username: "",
    b_photo: "",
    b_username: "",
  },
  getters: {},
  mutations: {
    updateIsRecord(state, is_record) {
      state.is_record = is_record;
    },
    updateSteps(state, data) {
      state.a_steps = data.a_steps;
      state.b_steps = data.b_steps;
    },
    updateRecordLoser(state, record_loser) {
      state.record_loser = record_loser;
    },
    updatePlayerInfo(state, player_info) {
      state.a_photo = player_info.a_photo;
      state.a_username = player_info.a_username;
      state.b_photo = player_info.b_photo;
      state.b_username = player_info.b_username;
    },
  },
  actions: {},
  modules: {},
};
