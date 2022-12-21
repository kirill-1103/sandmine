<template>
  <Modal :title="title" v-if="show_modal">
    <template #body>
      <label for="date">Дата</label>
      <br>
      <input class="input" id='date' name="date" type="date" v-model="date">

      <label for="zone">Зона</label>
      <br>
      <Multiselect
          name="zone"
          id="zone"
          :close-on-select="false"
          :options=options_zone
          :clear-on-select="false"
          :preserve-search="false"
          placeholder="Зоны"
          :preselect-first="true"
          v-model="zone"
      />

      <label for="weight">Вес</label>
      <br>
      <input class="input" id="weight" name="weight" type="number" v-model="weight">


    </template>
    <template #footer>
      <button class="button" @click="submit_">Подтвердить</button>
      <button class="button" @click="close_">Отмена</button>
    </template>
  </Modal>
</template>

<script>
import Modal from "./Modal.vue";
import Multiselect from "@vueform/multiselect";

export default {
  props: [
    "data",
    "show_modal",
    "close",
    'title',
    "submit_add",
    "options_zone",
    "data_for_edit",
    "submit_edit"
  ],
  components: {
    Modal, Multiselect
  },
  methods: {
    close_() {
      this.date = null;
      this.weight = null;
      this.zone = null;
      this.id = null;
      this.title = add_title;
      this.close()
    },
    submit_() {
      if (!this.checkData()) {
        return;
      }
      let new_data = {date: this.date, weight: this.weight, zone: this.zone, id: this.id}

      this.data_for_edit ? this.submit_edit(new_data) : this.submit_add(new_data)

      this.close_()
    },
    checkData() {
      if (!this.date || !this.zone || !this.weight) {
        alert('Заполнены не все данные')
        return false;
      }
      return true
    }

  },
  data() {
    return {
      zone: null,
      weight: null,
      date: null,
      id: null,
      title: add_title
    }
  },
  updated() {
    if (this.data_for_edit) {//если режим редактирования, то заполним данные
      this.zone = this.data_for_edit[2];
      this.weight = this.data_for_edit[1];
      this.date = this.data_for_edit[0].substring(0, 4) + "-" + this.data_for_edit[0].substring(5, 7) + "-" + this.data_for_edit[0].substring(8, 10)
      this.id = this.data_for_edit["id"]
      this.title = edit_title
    }
  }
}

let edit_title = "Редактирование"
let add_title = "Добавление"
</script>

<style>

</style>
