<template>
  <div class="background-parent">
    <div class="background">
      <span class="default-size">ФИО: {{ fullName }}</span>
      <br>
      <span class="default-size">Телефон: {{ phone }}</span>
      <br>
      <span class="default-size">Почта: {{ email }}</span>
      <br>
      <span class="default-size">Должность: {{ role }}</span>
      <br>
      <span class="default-size">Доступ в зоны: {{ zones }}</span>
      <br>
      <span class="default-size">Паспортные данные: {{ passportData }}</span>
      <br>
      <span class="default-size span_password" v-if="revealPassword"
            @click="togglePasswordVisibility">Пароль: {{ password }}</span>
      <span class="default-size span_password" v-else
            @click="togglePasswordVisibility">Пароль: {{ "*".repeat(password.length) }}</span>
      <br>
      <button class="button small_button" @click="navigateShifts">Смены</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import {
  AUTHORIZATION_PAGE_NAME,
  SHIFTS_LIST_PAGE_NAME
} from '../router/component_names'

export default {
  name: "StaffPage",
  data() {
    return {
      fullName: "",
      phone: "",
      email: "",
      role: "",
      zones: "",
      passportData: "",
      password: "",
      revealPassword: false
    }
  },
  created() {
    axios.get("/api/worker/" + this.$route.params.id)
        .then(response => {
          let data = response.data
          console.log(response)
          this.fullName = data.surname + " " + data.name + " " + data.patronymic
          this.phone = data.phoneNumber
          this.email = data.email
          this.role = data.role
          this.passportData = data.passport
          this.password = data.password
          this.zones = this.parse_zones(data.zonesWithAccess)
        })
        .catch(e => {
          this.$router.push({name: AUTHORIZATION_PAGE_NAME})
        })
  },
  methods: {
    togglePasswordVisibility() {
      this.revealPassword = !this.revealPassword
    },
    navigateShifts() {
      this.$router.push({name: SHIFTS_LIST_PAGE_NAME, params: {id: this.$route.params.id}})
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
            this.zones = zonesString
          }
        })
    }
  }
}
</script>

<style scoped>
@import '../../css/text.css';
@import '../../css/main.css';

.span_password:hover {
  color: #78787a;
}

.small_button {
  width: 40%;
}
</style>

