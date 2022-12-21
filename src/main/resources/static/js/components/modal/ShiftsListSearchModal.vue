<template>
  <Modal :title="title">
    <template #body>
      <div>
        <label for="date_from">Дата с</label>
        <br>
        <input class="input" id='date_from' name="date_from" type="date" v-model="date_from">

        <label for="date_to">Дата по</label>
        <br>
        <input class="input" id='date_to' name="date_to" type="date" v-model="date_to">

        <label for="zones">Зоны доступа</label>
        <br>
        <Multiselect
            class="with-bot-margin-medium"
            mode="multiple"
            :multiple="true"
            :close-on-select="false"
            :options=options_zones
            :clear-on-select="false"
            :preserve-search="true"
            placeholder="Зоны доступа"
            :preselect-first="true"
            v-model="zones">
        </Multiselect>

        <label for="attended">Присутствовал</label>
        <br>
        <Multiselect
            class="with-bot-margin-medium"
            :options=options
            :clear-on-select="true"
            :preserve-search="true"
            placeholder="Присутствовал"
            :preselect-first="true"
            v-model="attended">
        </Multiselect>
      </div>
    </template>
    <template #footer>
      <button class="button with-top-margin-medium" @click="submit_">Подтвердить</button>
      <button class="button with-top-margin-medium" @click="close_">Отмена</button>
      <button class="button with-light-background with-top-margin-medium" @click="clear_">Очистить</button>
    </template>
  </Modal>
</template>

<script>
import Modal from "./Modal.vue";
import Multiselect from "@vueform/multiselect";

export default {
  props: [
    "submit",
    "close",
    "options_zones"
  ],
  components: {
    Modal,
    Multiselect
  },
  methods: {
    close_() {
      this.close()
    },
    submit_() {
      let data = {
        date_from: this.date_from,
        date_to: this.date_to,
        attended: this.attended,
        zones: this.zones
      }
      this.submit(data)
      this.close()
    },
    clear_() {
      this.date_from = null
      this.date_to = null
      this.attended = null
    }
  },
  data() {
    return {
      date_from: null,
      date_to: null,
      attended: null,
      zones: null,
      options: ["Да", "Нет"],
      title: "Поиск"
    }
  }
}
</script>

<style>

</style>