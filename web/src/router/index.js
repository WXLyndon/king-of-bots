import { createRouter, createWebHistory } from "vue-router";
import BattleIndexView from "../views/battle/BattleIndexView.vue";
import RecordIndexView from "../views/record/RecordIndexView.vue";
import RankingIndexView from "../views/ranking/RankingIndexView.vue";
import UserBotIndexView from "../views/user/bot/UserBotIndexView.vue";
import NotFound from "../views/error/NotFound.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: BattleIndexView,
  },
  {
    path: "/battle/",
    name: "battle_index",
    component: BattleIndexView,
  },

  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
  },

  {
    path: "/ranking/",
    name: "ranking_index",
    component: RankingIndexView,
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
  },
  { path: "/404/", name: "not_found", component: NotFound },
  { path: "/:catchAll(.*)", redirect: "/404/" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
