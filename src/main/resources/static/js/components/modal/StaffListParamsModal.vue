<template>
  <Modal :title="title">
    <template #body>
      <div style="text-align: center" class="with-bot-margin-medium">
        <span class="default-size" v-if="is_empty()">Нет параметров  <br> </span>
        <span class="default-size" v-if="data.surname">Фамилия: {{ data.surname }} <br> </span>
        <span class="default-size" v-if="data.name">Имя: {{ data.name }} <br> </span>
        <span class="default-size" v-if="data.patronymic">Отчество: {{ data.patronymic }} <br> </span>
        <span class="default-size" v-if="data.phone">Телефон: {{ data.phone }} <br> </span>
        <span class="default-size" v-if="roles">Должность: {{ roles }}  <br> </span>
        <span class="default-size" v-if="zones">Доступ в зоны: {{ zones }} </span>
      </div>
    </template>
    <template #footer>
      <button class="button" @click="close">Закрыть</button>
    </template>
  </Modal>
</template>

<script>
import Modal from "./Modal.vue";

export default {
  name: "StaffListParamsModal",
  components: {Modal},
  props: [
    "close",
    "data"
  ],
  data() {
    return {
      title: 'Параметры поиска',
      zones: this.parse_array(this.data.zones),
      roles: this.parse_array(this.data.roles),
    }
  },
  methods: {
    is_empty() {
      return this.data.size
    },
    parse_array(zones_to_parse) {
      console.log(zones_to_parse)
      if (!zones_to_parse) {
        return null
      }
      let zones = zones_to_parse.slice()
      if (zones) {
        let zonesString = zones[0]
        zones.shift()
        for (let zone of zones) {
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

</style>