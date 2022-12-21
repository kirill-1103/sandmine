<template>
  <div class="background-parent">
    <div class="background">
      <label class="input-hint-text-small">ФИО</label>
      <br/>
      <input
          placeholder="ФИО"
          v-model="fullName"
          class="with-bot-margin-medium"
      />
      <br/>

      <label class="input-hint-text-small">Телефон</label>
      <br/>
      <input
          placeholder="Телефон"
          v-model="phone"
          class="with-bot-margin-medium"
      />
      <br/>

      <label class="input-hint-text-small">Почта</label>
      <br/>
      <input
          placeholder="Почта"
          v-model="email"
          class="with-bot-margin-medium"
      />
      <br/>

      <label class="input-hint-text-small">Серия и номер паспорта</label>
      <br/>
      <input
          placeholder="Серия и номер паспорта"
          v-model="passport"
          class="with-bot-margin-medium"
      />
      <br/>
      <div v-if="!this.$route.params.id">
        <label class="input-hint-text-small">ID пропуска</label>
        <br/>
        <input
            placeholder="ID пропуска"
            v-model="passID"
            class="with-bot-margin-medium"
        />
      </div>

      <label class="input-hint-text-small">Должность</label>
      <br/>
<!--      <input-->
<!--          placeholder="Должность"-->
<!--          v-model="role"-->
<!--          class="with-bot-margin-medium"-->
<!--      />-->
      <Multiselect
          id="multiselect2"
          v-show="show_multiselect"
          class="with-bot-margin-medium"
          :close-on-select="false"
          :options="this.optionsRole"
          :clear-on-select="false"
          :preserve-search="true"
          placeholder="Должность"
          :preselect-first="true"
          v-model="this.role"
      />
      <br/>

      <label class="input-hint-text-small">Зоны доступа</label>
      <br/>
      <Multiselect
          id="multiselect"
          v-show="show_multiselect"
          class="with-bot-margin-medium"
          mode="multiple"
          :close-on-select="false"
          :options="this.optionsZone"
          :clear-on-select="false"
          :preserve-search="true"
          placeholder="Зоны доступа"
          :preselect-first="true"
          v-model="this.zones"
          :multiple="true"
      >
      </Multiselect>

      <br/>

      <button class="button" @click="addOrEditStaff">
        Добавить (Изменить)
      </button>
      <button class="button" @click="cancel">Отмена</button>
    </div>
  </div>
</template>

<script>
import Multiselect from "@vueform/multiselect";
import axios from "axios";
import {FIND_STAFF_PAGE_NAME} from "../router/component_names";

export default {
  name: "AddEditStaffPage",
  components: {
    Multiselect
  },
  props: ["text"],
  data() {
    return {
      fullName: "",
      phone: "",
      email: "",
      passport: "",
      role: "",
      zones: [],
      optionsZone: null,
      all_zones: null,
      show_multiselect: true,
      worker:null,
      passID:null,
      optionsRole:["Администратор","Охранник","Работник"],
      mapRole:{"admin":"Администратор","warden":"Охранник","worker":"Работник"},
      mapRoleInverse:{"Администратор":"admin","Охранник":"warden","Работник":"worker"}
    }
  },
  created() {
    let id = this.$route.params.id;
    axios.get("/api/zone/all")
        .then(response => {//получили зоны
              let zones = response.data;
              this.all_zones = zones;
              if (id) {//edit
                axios.get("/api/worker/" + id)
                    .then(response => {
                      let worker = response.data;
                      this.worker = worker;
                      this.fill_values(worker);
                      this.fill_options_zone(zones, worker.zonesWithAccess);
                    })
              } else {//add
                this.fill_options_zone(zones);
              }
            }
        )
  },
  methods: {
    cancel() {
      this.$router.back()
    },
    addOrEditStaff() {
      let surname = this.fullName.substring(0, this.fullName.indexOf(' '));
      let other_part = this.fullName.substring(this.fullName.indexOf(' ') + 1, this.fullName.length)
      let name = other_part.substring(0, other_part.indexOf(' '))
      let patronymic = other_part.substring(other_part.indexOf(' ') + 1, other_part.length);
      if(!patronymic || !name || !surname ||  patronymic.indexOf(' ')!==-1){
        alert("Имя заполнено неверно. Формат: \"Фамилия Имя Отчество\"")
        return;
      }
      let data = {
        surname: surname,
        name: name,
        patronymic: patronymic,
        phoneNumber: this.phone,
        email: this.email,
        passport: this.passport,
        role: this.mapRoleInverse[this.role],
        zonesWithAccess: this.getZonesIds(this.zones)
      }
      if(!this.$route.params.id){//add
        data['passId'] = this.passID;
      }else{//edit
        data['password'] = this.worker.password;
        data['passId'] = this.worker.passId;
        data['workerId'] = this.worker.workerId;
      }
      if(!this.checkData(data)){
        return;
      }
      let config = {headers: {'Content-Type': 'application/json'}}

      if(this.$route.params.id) {//edit
        axios.post("/api/worker/edit", JSON.stringify(data), config)
            .then(response => {
              this.$router.back()
            })
      }else{//add
        axios.post("/api/worker/new",JSON.stringify(data),config)
            .then(response =>{
              this.$router.back()
            })
      }
    },
    fill_options_zone(zones, selected = null) {
      let options_zone = []
      for (let zone of zones) {
        options_zone.push(zone.name)
      }
      this.optionsZone = options_zone;
      this.show_multiselect = true;
      if (selected) {
        for (let zone_id of selected) {
          for (let zone of this.all_zones) {
            if (zone.zoneId === zone_id) {
              this.zones.push(zone.name)
            }
          }
        }
      }
    },
    fill_values(worker) {
      this.fullName = worker.surname + " " + worker.name + " " + worker.patronymic;
      this.phone = worker.phoneNumber
      this.email = worker.email
      this.passport = worker.passport
      this.role = this.mapRole[this.worker.role];
    },
    getZonesIds(names){
      let result = []
      for(let zone of this.all_zones){
        for(let name of names){
          if(zone.name === name){
            result.push(zone.zoneId)
          }
        }
      }
      console.log(this.all_zones)
      return result;
    },
    checkData(data){
      let regex_phone =  /^(\+7|7|8)?[\s\-]?\(?[489][0-9]{2}\)?[\s\-]?[0-9]{3}[\s\-]?[0-9]{2}[\s\-]?[0-9]{2}$/;
      let regex_email =  /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/iu;
      let regex_passport = /^[0-9]{10}$/iu;
      let regex_passId = /^[0-9]+$/iu
      for (let index in data) {
        if (!data[index]) {
          alert("Не все поля заполнены")
          return false;
        }
      }
      if(data.passport && !regex_passport.test(data.passport)){
        alert("Паспорт заполнен неверно. Должно быть 10 цифр.")
        return false;
      }
      if(data.phoneNumber && !regex_phone.test(data.phoneNumber)){
        alert("Номер телефона заполнен неверно.")
        return false;
      }
      if(data.email && !regex_email.test(data.email)){
        alert("Почта заполнена неверно.")
        return false
      }
      if(data.passId && !regex_passId.test(data.passId)){
        alert("ID пропуска должен состоять из цифр.");
        return false;
      }

      return true;
    }
  }
}

</script>

<style scoped>
@import "../../css/text.css";
@import "../../css/main.css";
</style>
