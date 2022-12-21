<template>
  <div class="background-parent">
    <div class="background">
      <span class="default-size"> ФИО: {{ name }} </span> <br>
      <span class="default-size">Телефон: {{ phone_number }} </span> <br>
      <span class="default-size">Почта: {{ email }} </span> <br>
      <span class="default-size">Должность: {{ employee_position }} </span> <br>
      <span class="default-size">Доступ в зоны: {{ zones }}</span> <br>
      <span class="default-size">Паспортные данные: {{ passport }} </span> <br>

      <span class="default-size" v-if="pass" style="color:green">Есть доступ</span>
      <span class="default-size" v-else style="color:red">Нет доступа</span>
    </div>
  </div>
</template>

<script>
import axios from "axios"

export default {
  data() {
    return {
      name: "",
      phone_number: "",
      email: "",
      employee_position: "",
      passport: "",
      pass: false,
      zones: []
    }
  },
  created() {
    let customConfig = {
      headers: {
        'Content-Type': 'application/json'
      }
    }

    axios.post(
        "/api/worker/check",
        JSON.stringify({passId: this.$route.params.passId, wardenId: this.$route.params.id}),
        customConfig
    )
        .then(response => {
          console.log(response)
          let data = response.data
          this.name = data.surname + " " + data.name + " " + data.patronymic
          this.phone_number = data.phoneNumber
          this.email = data.email
          this.employee_position = data.role
          this.passport = data.passport.slice(0, 4) + " " + data.passport.slice(4)
          this.pass = data.allowed
          this.parse_zones(data.zonesWithAccess)
        })
        .catch(e => {
          console.log(e)
        })
  },
  methods: {
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

<style>

</style>