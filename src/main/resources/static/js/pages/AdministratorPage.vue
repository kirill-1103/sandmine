<template>
  <div class="background-parent">
    <div class="background">
      <span class="default-size">ФИО: {{ fullName }}</span>
      <br/>
      <span class="default-size">Телефон: {{ phone }}</span>
      <br/>
      <span class="default-size">Почта: {{ email }}</span>
      <br/>
      <span class="default-size">Должность: {{ role }}</span>
      <br/>
      <span class="default-size">Доступ в зоны: {{ zones }}</span>
      <br/>
      <span class="default-size">Паспортные данные: {{ passportData }}</span>
      <br/>
      <span
          class="default-size span_password"
          v-if="revealPassword"
          @click="togglePasswordVisibility"
      >Пароль: {{ password }}</span
      >
      <span
          class="default-size span_password"
          v-else
          @click="togglePasswordVisibility"
      >Пароль: {{ "*".repeat(password.length) }}</span
      >
      <br/>
      <button class="button" @click="navigateShifts">Мои смены</button>
      <br/>
      <button class="button" @click="navigateMineStats">Учёт добычи</button>
      <br/>
      <button class="button" @click="navigateStaffList">
        Список персонала
      </button>
      <br/>
      <button class="button" @click="navigateStaffManagement">
        Управление персоналом
      </button>
      <br/>
      <button class="button" @click="navigateStaffShifts">
        Смены персонала
      </button>
      <button v-if="this.$store.state.userRole==='admin-admin'" class="button" @click="navigateImportExport">
        Данные
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import {
  AUTHORIZATION_PAGE_NAME,
  SHIFTS_LIST_PAGE_NAME,
  PRODUCTION_PAGE_NAME,
  STAFF_LIST_PAGE_NAME,
  FIND_STAFF_PAGE_NAME,
  STAFF_SHIFTS_PAGE_NAME, IMPORT_EXPORT_PAGE_NAME
} from '../router/component_names'

export default {
  name: "AdministratorPage",
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

          //vuex
          this.$store.state.userName = this.fullName;
          this.$store.state.userRole = this.role;
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
    navigateMineStats() {
      this.$router.push({name: PRODUCTION_PAGE_NAME})
    },
    navigateStaffList() {
      this.$router.push({name: STAFF_LIST_PAGE_NAME})
    },
    navigateStaffManagement() {
      this.$router.push({name: FIND_STAFF_PAGE_NAME})
    },
    navigateStaffShifts() {
      this.$router.push({name: STAFF_SHIFTS_PAGE_NAME})
    },
    navigateImportExport(){
      this.$router.push({name: IMPORT_EXPORT_PAGE_NAME})
    },
    parse_zones(zones) {
      let zonesResult = zones
      if (zonesResult) {
        let zonesString = zonesResult[0]
        zonesResult.shift()
        for (let zone of zonesResult) {
          zonesString += ", " + zone
        }
        return zonesString
      }
      return null
    }
  }
}
</script>

<style scoped>
@import "../../css/text.css";
@import "../../css/main.css";

.span_password:hover {
  color: #78787a;
}
</style>
