import { createRouter, createWebHistory } from "vue-router";
import BattleIndexView from "../views/battle/BattleIndexView";
import RecordIndexView from "../views/record/RecordIndexView";
import RankingIndexView from "../views/ranking/RankingIndexView";
import UserBotIndexView from "../views/user/bot/UserBotIndexView";
import NotFound from "../views/error/NotFound";
import UserAccountLoginView from "../views/user/account/UserAccountLoginView";
import UserAccountRegisterView from "../views/user/account/UserAccountRegisterView";
import store from "@/store/index";

const routes = [
  {
    path: "/",
    name: "home",
    component: BattleIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/battle/",
    name: "battle_index",
    component: BattleIndexView,
    meta: {
      requestAuth: true,
    },
  },

  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    },
  },

  {
    path: "/ranking/",
    name: "ranking_index",
    component: RankingIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    },
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    },
  },
  {
    path: "/404/",
    name: "not_found",
    component: NotFound,
    meta: {
      requestAuth: false,
    },
  },
  { path: "/:catchAll(.*)", redirect: "/404/" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.meta.requestAuth && !store.state.user.is_login) {
    next({ name: "user_account_login" });
  } else {
    next();
  }
});

export default router;
