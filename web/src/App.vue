<template>
  <NavBar />
  <router-view />
</template>

<script>
import NavBar from "./components/NavBar.vue";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";
// import $ from "jquery";

export default {
  components: {
    NavBar,
  },

  setup() {
    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/token/",
    //   type: "POST",
    //   data: {
    //     username: "lyndon",
    //     password: "950817",
    //   },
    //   success: (resp) => {
    //     console.log(resp);
    //   },
    //   error: (err) => {
    //     console.log(err);
    //   },
    // });
    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/info/",
    //   type: "GET",
    //   headers: {
    //     Authorization:
    //       "Bearer " +
    //       "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlMDg2MDBiYzUzMWE0M2ZkYTIzYjE4NWQzYTEzMTZiNyIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY2MTIxNzQzNSwiZXhwIjoxNjYyNDI3MDM1fQ._iHhBxtR_xMo-UMX4yIEcuiJL4YrizK8gBGgQdSMC9g",
    //   },
    //   success: (resp) => {
    //     console.log(resp);
    //   },
    //   error: (err) => {
    //     console.log(err);
    //   },
    // });
    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/register/",
    //   type: "POST",
    //   data: {
    //     username: "Sheldon1",
    //     password: "123456",
    //     confirmedPassword: "123456",
    //   },
    //   success: (resp) => {
    //     console.log(resp);
    //   },
    //   error: (err) => {
    //     console.log(err);
    //   },
    // });
  },
};
</script>

<style>
body {
  background-image: url("@/assets/images/background.jpg");
  background-size: cover;
}
</style>
