<template>
  <ContentField>
    <table class="table table-hover">
      <thead>
        <tr>
          <th>Player A</th>
          <th>Player B</th>
          <th>Battle Result</th>
          <th>Battle Time</th>
          <th>Operation</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="record in records" :key="record.record.id">
          <td>
            <img :src="record.a_photo" alt="" class="record-user-photo" />
            <!-- space -->
            &nbsp;
            <span class="record-user-username">{{ record.a_username }}</span>
          </td>
          <td>
            <img :src="record.b_photo" alt="" class="record-user-photo" />
            &nbsp;
            <span class="record-user-username">{{ record.b_username }}</span>
          </td>
          <td>{{ record.result }}</td>
          <td>{{ record.record.createTime }}</td>
          <td>
            <button type="button" class="btn btn-secondary">
              Watch Recording
            </button>
          </td>
        </tr>
      </tbody>
    </table></ContentField
  >
</template>

<script>
import ContentField from "@/components/ContentField.vue";
import { useStore } from "vuex";
import { ref } from "vue";
import $ from "jquery";

export default {
  components: {
    ContentField,
  },

  setup() {
    const store = useStore();
    let records = ref([]);
    let current_page = 1;
    let total_records = 0;

    const pull_page = (page) => {
      $.ajax({
        url: "http://127.0.0.1:3000/record/getlist/",
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        data: {
          page,
        },
        success(resp) {
          records.value = resp.records;
          total_records = resp.records_count;
        },
        error(err) {
          console.log(err);
        },
      });
    };

    pull_page(current_page);
    console.log(total_records);
    return { records };
  },
};
</script>

<style scoped>
img.record-user-photo {
  width: 4vh;
  border-radius: 50%;
}
</style>