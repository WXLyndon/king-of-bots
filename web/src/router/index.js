import { createRouter, createWebHistory } from "vue-router";
import BattleIndexView from "../views/battle/BattleIndexView";
import RecordIndexView from "../views/record/RecordIndexView";
import RankingIndexView from "../views/ranking/RankingIndexView";
import UserBotIndexView from "../views/user/bot/UserBotIndexView";
import NotFound from "../views/error/NotFound";
import UserAccountLoginView from "../views/user/account/UserAccountLoginView";
import UserAccountRegisterView from "../views/user/account/UserAccountRegisterView";

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
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
  },
  { path: "/404/", name: "not_found", component: NotFound },
  { path: "/:catchAll(.*)", redirect: "/404/" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
