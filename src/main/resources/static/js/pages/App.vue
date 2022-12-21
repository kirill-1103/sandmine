<template>
  <div id="app">
    <div class = "head">
      <span style="width:100%;text-align: center" v-if="this.$store.state.userRole">{{this.$store.state.userName}}  -  {{this.rolesMap[this.$store.state.userRole]}}</span>
    </div>

    <button class="back-button" @click="back" v-if="check_visibility()"><span style="font-size: 40px"
                                                                              class="material-icons">arrow_back</span>
    </button>
    <router-view>
    </router-view>
  </div>
</template>

<script>
import {AUTHORIZATION_PAGE_NAME} from '../router/component_names'
import axios from "axios";
import Cookies from 'js-cookie'

export default {
  methods: {
    back() {
      this.$router.back();
    },
    check_visibility() {
      return this.$route.name != AUTHORIZATION_PAGE_NAME
    }
  },
updated() {
  if(this.$route.name === AUTHORIZATION_PAGE_NAME){
      this.$store.state.userName = "";
      this.$store.state.userRole = "";
    }else{
    this.$store.state.userName = localStorage.getItem("name");
    this.$store.state.userRole = localStorage.getItem("role")
  }
  },

  data(){
    return {
      rolesMap : {"admin-admin":"Администратор","admin":"Администратор", "warden":"Охранник","worker":"Работник"}
    }
  }
}
</script>

<style>
.back-button {
  position: fixed;
  background: none;
  border: none;
  top: 4%;
  left: 5%;
  width: 3%;
  z-index: 1;
}

.head{
  width: 100%;
  text-align: center;
  font-size: 30px;
}

.back-button:hover {
  color: rgb(255, 150, 2);
}
</style>