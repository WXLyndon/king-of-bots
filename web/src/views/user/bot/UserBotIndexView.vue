<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card" style="margin-top: 20px">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="" style="width: 100%" />
          </div>
        </div>
      </div>
      <div class="col-9">
        <div class="card" style="margin-top: 20px">
          <div class="card-header">
            <span style="font-size: 130%">My bots</span>
            <button
              type="button"
              class="btn btn-primary float-end"
              data-bs-toggle="modal"
              data-bs-target="#add-bot-btn"
            >
              Create New Bot
            </button>

            <!-- Modal -->
            <div class="modal fade" id="add-bot-btn" tabindex="-1">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title" id="CreateNewBotModalLabel">
                      Create New Bot
                    </h5>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-nickname" class="form-label"
                        >Nickname</label
                      >
                      <input
                        v-model="new_bot.nickname"
                        type="text"
                        class="form-control"
                        id="add-bot-nickname"
                        placeholder="Please enter this bot's nickname."
                      />
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-description" class="form-label"
                        >Description</label
                      >
                      <textarea
                        v-model="new_bot.description"
                        class="form-control"
                        id="add-bot-description"
                        rows="3"
                        placeholder="Please enter this bot's description."
                      ></textarea>
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">Code</label>
                      <VAceEditor
                        v-model:value="new_bot.code"
                        @init="editorInit"
                        lang="java"
                        theme="textmate"
                        style="height: 300px"
                      />
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div class="error-message" id="error-message">
                      {{ new_bot.error_message }}
                    </div>

                    <button
                      type="button"
                      class="btn btn-secondary"
                      data-bs-dismiss="modal"
                    >
                      Close
                    </button>
                    <button
                      type="button"
                      class="btn btn-primary"
                      @click="add_bot"
                    >
                      Create
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="card-body">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Nickname</th>
                  <th>Description</th>
                  <th>Created Time</th>
                  <th>Modified Time</th>
                  <th>Operation</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                  <td>{{ bot.nickname }}</td>
                  <td>{{ bot.description }}</td>
                  <td>{{ bot.createTime }}</td>
                  <td>{{ bot.modifyTime }}</td>
                  <td>
                    <button
                      type="button"
                      class="btn btn-secondary"
                      style="margin-right: 5px"
                      data-bs-toggle="modal"
                      :data-bs-target="'#update-bot-modal-' + bot.id"
                    >
                      Edit
                    </button>

                    <!-- Modal -->
                    <div
                      class="modal fade"
                      :id="'update-bot-modal-' + bot.id"
                      tabindex="-1"
                    >
                      <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h5 class="modal-title" id="UpdateNewBotModalLabel">
                              Update Bot: {{ bot.nickname }}
                            </h5>
                            <button
                              type="button"
                              class="btn-close"
                              data-bs-dismiss="modal"
                              aria-label="Close"
                            ></button>
                          </div>
                          <div class="modal-body">
                            <div class="mb-3">
                              <label
                                for="update-bot-nickname"
                                class="form-label"
                                >Nickname</label
                              >
                              <input
                                v-model="bot.nickname"
                                type="text"
                                class="form-control"
                                id="update-bot-nickname"
                                placeholder="Please enter this bot's nickname."
                              />
                            </div>
                            <div class="mb-3">
                              <label
                                for="update-bot-description"
                                class="form-label"
                                >Description</label
                              >
                              <textarea
                                v-model="bot.description"
                                class="form-control"
                                id="update-bot-description"
                                rows="3"
                                placeholder="Please enter this bot's description."
                              ></textarea>
                            </div>
                            <div class="mb-3">
                              <label for="update-bot-code" class="form-label"
                                >Code</label
                              >
                              <VAceEditor
                                v-model:value="bot.code"
                                @init="editorInit"
                                lang="java"
                                theme="textmate"
                                style="height: 300px"
                                :options="{
                                  fontSize: 25,
                                  enableLiveAutocompletion: true,
                                  enableBasicAutocompletion: true,
                                  enableSnippets: true,
                                }"
                              />
                            </div>
                          </div>
                          <div class="modal-footer">
                            <div class="error-message" id="error-message">
                              {{ bot.error_message }}
                            </div>

                            <button
                              type="button"
                              class="btn btn-secondary"
                              data-bs-dismiss="modal"
                              @click="refresh_bots"
                            >
                              Close
                            </button>
                            <button
                              type="button"
                              class="btn btn-primary"
                              @click="update_bot(bot)"
                            >
                              Save Update
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>

                    <button
                      type="button"
                      class="btn btn-danger"
                      @click="remove_bot(bot)"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from "vue";
import $ from "jquery";
import { useStore } from "vuex";
import { Modal } from "bootstrap/dist/js/bootstrap.js";
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";

export default {
  components: {
    VAceEditor,
  },

  setup() {
    const store = useStore();
    let bots = ref([]);

    ace.config.set(
      "basePath",
      "https://cdn.jsdelivr.net/npm/ace-builds@" +
        require("ace-builds").version +
        "/src-noconflict/"
    );

    const new_bot = reactive({
      nickname: "",
      description: "",
      code: "",
      error_message: "",
    });

    const refresh_bots = () => {
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/getlist/",
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success: (resp) => {
          bots.value = resp;
        },
      });
    };

    refresh_bots();

    const add_bot = () => {
      new_bot.error_message = "";
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/add/",
        type: "POST",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          nickname: new_bot.nickname,
          description: new_bot.description,
          code: new_bot.code,
        },
        success: (resp) => {
          if (resp.error_message === "success") {
            new_bot.nickname = "";
            new_bot.description = "";
            new_bot.code = "";
            Modal.getInstance("#add-bot-btn").hide();
            refresh_bots();
          } else {
            new_bot.error_message = resp.error_message;
          }
        },
        error: (err) => {
          new_bot.error_message = err.error_message;
        },
      });
    };

    const update_bot = (bot) => {
      new_bot.error_message = "";
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/update/",
        type: "POST",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          bot_id: bot.id,
          nickname: bot.nickname,
          description: bot.description,
          code: bot.code,
        },
        success: (resp) => {
          if (resp.error_message === "success") {
            Modal.getInstance("#update-bot-modal-" + bot.id).hide();
            refresh_bots();
          } else {
            bot.error_message = resp.error_message;
          }
        },
        error: (err) => {
          bot.error_message = err.error_message;
        },
      });
    };

    const remove_bot = (bot) => {
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/remove/",
        type: "POST",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          bot_id: bot.id,
        },
        success: (resp) => {
          if (resp.error_message == "success") {
            refresh_bots();
          }
        },
      });
    };

    return {
      bots,
      new_bot,
      add_bot,
      update_bot,
      remove_bot,
      refresh_bots,
    };
  },
};
</script>

<style scoped>
div.error-message {
  color: red;
}
</style>