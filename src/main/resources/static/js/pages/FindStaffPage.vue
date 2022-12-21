<template>
  <div class="background-parent">
    <div class="background">
      <label>Введите почту или телефон работника</label>
      <br>
      <input class="with-bot-margin-medium" v-model = find_text>
      <button class="button with-bot-margin-medium" @click="edit()">Редактировать</button>
      <button class="button with-light-background" @click="add()">Добавить</button>
    </div>
  </div>
</template>

<script>
import {ADD_STAFF_PAGE_NAME, EDIT_STAFF_PAGE_NAME, WORKER_ACC_PAGE_NAME} from "../router/component_names";
import axios from "axios";

export default {
  name: "FindStaffPage",
  data(){
    return{
      find_text:"",
      config : {headers: {'Content-Type': 'application/json'}}
    }
  },
  methods:{
    edit(){
      let isPhone = false;
      let isEmail = false;

      let regex_phone = /^(\+7|7|8)?[\s\-]?\(?[489][0-9]{2}\)?[\s\-]?[0-9]{3}[\s\-]?[0-9]{2}[\s\-]?[0-9]{2}$/;
      let regex_email = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/iu;

      let text = this.find_text;
      if(regex_phone.test(text)){
        isPhone = true
        axios.post("/api/worker/phone",text,this.config)
            .then(response => {
              let worker = response.data;
              if(!worker){
                alert("Работника с таким номером не существует.")
              }else {
                this.$router.push({name: EDIT_STAFF_PAGE_NAME, params: {id: worker.workerId}})
              }
            })
      }
      if(regex_email.test(text)){
        isEmail=true;
        axios.post("/api/worker/email",text,this.config)
            .then(response => {
              let worker = response.data;
              console.log(response)
              if(!worker){
                alert("Работника с такой почтой не существует.")
              }else {
                this.$router.push({name: EDIT_STAFF_PAGE_NAME, params: {id: worker.workerId}})
              }
            })
      }
      if(!(isEmail || isPhone)){
        alert("Введите почту или номер телефона работника")
      }

    },
    add(){
      this.$router.push({name: ADD_STAFF_PAGE_NAME});
    }
  }
}
</script>

<style scoped>
@import '../../css/main.css';
@import '../../css/input.css';
@import '../../css/button.css';


</style>