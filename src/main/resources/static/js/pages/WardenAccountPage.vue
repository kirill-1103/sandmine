<template>
  <div class="background-parent">
    <button class="button" @click="check_pass" style="float: right; width:30%; margin-right:2%; margin-top:2%">
      Проверить пропуск
    </button>
    <div class="background">
      <span class="default-size"> ФИО: {{ name }} </span> <br>
      <span class="default-size">Телефон: {{ phone_number }} </span> <br>
      <span class="default-size">Почта: {{ email }} </span> <br>
      <span class="default-size">Должность: {{ employee_position }} </span> <br>
      <span class="default-size">Паспортные данные: {{ passport }} </span> <br>
      <span class="default-size">Доступ в зоны: {{ zone }} </span> <br>
      <span class="default-size span_password" v-if="revealPassword"
            @click="togglePasswordVisibility">Пароль: {{ password }}</span>
      <span class="default-size span_password" v-else
            @click="togglePasswordVisibility">Пароль: {{ "*".repeat(password.length) }}</span>
      <br>

      <button class="button small_button" @click="shifts">Смены</button>
    </div>
  </div>
</template>

<style>

</style>

<script>
import axios from 'axios'
import {AUTHORIZATION_PAGE_NAME, CHECK_PASS_PAGE_NAME, SHIFTS_LIST_PAGE_NAME} from "../router/component_names";

export default {
  data() {
    return {
      name: "",
      phone_number: "",
      email: "",
      employee_position: "",
      passport: "",
      password: "",
      zone: "",
      id: this.$route.params.id,
      revealPassword: false
    }
  },
  created() {
    axios.get("/api/worker/" + this.$route.params.id)
        .then(response => {
          let data = response.data
          this.name = data.surname + " " + data.name + " " + data.patronymic
          this.phone_number = data.phoneNumber
          this.email = data.email
          this.employee_position = data.role
          this.passport = data.passport.slice(0, 4) + " " + data.passport.slice(4)
          this.password = data.password
          this.parse_zones(data.zonesWithAccess)
        })
        .catch(e => {
          this.$router.push({name: AUTHORIZATION_PAGE_NAME})
        })
  },
  methods: {
    togglePasswordVisibility() {
      this.revealPassword = !this.revealPassword
    },
    shifts() {
      this.$router.push({name: SHIFTS_LIST_PAGE_NAME, params: {id: this.id}})
    },
    check_pass() {
      this.$router.push({name: CHECK_PASS_PAGE_NAME, params: {id: this.id}});
    },
    parse_zones(zonesIds) {
      axios.get("/api/zone/all")
          .then(response => {
            let map = new Map()
            response.data.forEach((it) => {
              map.set(it.zoneId, it.name)
            })

            if (zonesIds) {
              let zonesString = map.get(zonesIds[0])
              zonesIds.shift()
              for (let zoneId of zonesIds) {
                zonesString += ", " + map.get(zoneId)
              }
              this.zone = zonesString
            }
          })
    }
  }
}
</script>

<style>
.span_password:hover {
  color: #78787a;
}

.small_button {
  width: 40%;
}
</style>