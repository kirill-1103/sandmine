<template>
  <div class="background-parent">
    <div class="background">
      <span class="default-size"> ФИО: {{ fullname }} </span> <br>
      <span class="default-size">Телефон: {{ phone_number }} </span> <br>
      <span class="default-size">Почта: {{ email }} </span> <br>
      <span class="default-size">Должность: {{ role }} </span> <br>
      <span class="default-size">Паспортные данные: {{ passport }} </span> <br>
      <span class="default-size span_password" v-if="revealPassword"
            @click="togglePasswordVisibility">Пароль: {{ password }}</span>
      <span class="default-size span_password" v-else
            @click="togglePasswordVisibility">Пароль: {{ "*".repeat(password.length) }}</span>
      <br>

      <div>
        <button class="button" style="float: right; width: 47%" @click="edit">Редактирование</button>
        <button class="button" style="float: left; width: 47%" @click="shifts">Смены</button>
      </div>
      <button class="button" style="width: 80%" @click="fire">Уволить</button>
    </div>

    <SubordinateAccountConfirmFireModal
      :submit="submit_fire"
      :close="close_fire"
      :show_modal="show_modal_fire"
    />

  </div>

</template>

<script>
import axios from "axios";
import {EDIT_STAFF_PAGE_NAME, FIND_STAFF_PAGE_NAME, SHIFTS_LIST_PAGE_NAME} from "../router/component_names";
import SubordinateAccountConfirmFireModal from "../components/modal/SubordinateAccountConfirmFireModal.vue";

export default {
  components: {SubordinateAccountConfirmFireModal},
  data() {
    return {
      fullname: "",
      name:"",
      surname:"",
      patronymic:"",
      phone_number: "",
      email: "",
      role:"",
      passport:"",
      password: "",
      id: null,
      revealPassword: false,
      worker:null,
      mapRole:{"admin":"Администратор","warden":"Охранник","worker":"Работник"},
      show_modal_fire:false
    }
  },
  methods: {
    edit() {
      this.$router.push({name: EDIT_STAFF_PAGE_NAME,params: {id: this.id}})
    },
    fire() {
      this.show_modal_fire = true;
    },
    shifts() {
      this.$router.push({name: SHIFTS_LIST_PAGE_NAME, params: {id: this.id}})
    },
    togglePasswordVisibility() {
      this.revealPassword = !this.revealPassword
    },
    close_fire(){
      this.show_modal_fire = false;
    },
    submit_fire(){
      this.show_modal_fire = false;
      let config = {headers: {'Content-Type': 'application/json'}}
      axios.post("/api/worker/remove",JSON.stringify(this.id),config)
      this.$router.go(-2);
    }
  },
  created() {
    this.id = this.$route.params.id;
    axios.get("/api/worker/"+this.id)
        .then(response=>{
          this.worker = response.data
          this.name = this.worker.name;
          this.surname = this.worker.surname;
          this.patronymic = this.worker.patronymic;
          this.email = this.worker.email;
          this.phone_number = this.worker.phoneNumber;
          this.role = this.mapRole[this.worker.role]
          this.passport = this.worker.passport.substring(0,4)+" "+this.worker.passport.substring(5)
          this.fullname = this.surname + " " + this.name +  " " + this.patronymic
          this.password = this.worker.password
        })
  }
}
</script>

<style>

</style>