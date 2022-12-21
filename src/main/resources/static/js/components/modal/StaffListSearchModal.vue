<template>
  <Modal :title="title">
    <template #body>
      <div>
        <label for="surname">Фамилия</label>
        <br>
        <input class="input" id='surname' name="surname" v-model="surname">

        <label for="name">Имя</label>
        <br>
        <input class="input" id='name' name="name" v-model="name">

        <label for="patronymic">Отчество</label>
        <br>
        <input class="input" id='patronymic' name="patronymic" v-model="patronymic">

        <label for="phone">Телефон</label>
        <br>
        <input class="input" id='phone' name="phone" v-model="phone">

        <label for="role">Должность</label>
        <br>
        <Multiselect
            class="with-bot-margin-medium"
            id="roles"
            label="roles"
            name="roles"
            mode="multiple"
            :close-on-select="false"
            :options=options_roles
            :clear-on-select="false"
            :preserve-search="true"
            placeholder="Должность"
            :preselect-first="true"
            v-model="roles"
            :multiple="true">
        </Multiselect>

        <label for="zones">Зоны доступа</label>
        <br>
        <Multiselect
            class="with-bot-margin-medium"
            id="zones"
            label="zones"
            name="zones"
            mode="multiple"
            :close-on-select="false"
            :options=options_zones
            :clear-on-select="false"
            :preserve-search="true"
            placeholder="Зоны доступа"
            :preselect-first="true"
            v-model="zones"
            :multiple="true">
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
    "options_zones",
    "options_roles"
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
        name: this.name,
        surname: this.surname,
        patronymic: this.patronymic,
        phone: this.phone,
        roles: this.roles,
        zones: this.zones
      }
      this.submit(data)
      this.close()
    },
    clear_() {
      this.name = null
      this.surname = null
      this.patronymic = null
      this.phone = null
      this.roles = null
      this.zones = null
    }
  },
  data() {
    return {
      name: null,
      surname: null,
      patronymic: null,
      phone: null,
      roles: null,
      zones: null,
    }
  }
}
</script>

<style>

</style>