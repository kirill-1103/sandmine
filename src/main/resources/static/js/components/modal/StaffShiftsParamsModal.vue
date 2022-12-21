<template>
  <Modal :title="title">
    <template #body>
      <div style="text-align: center" class="with-bot-margin-medium">
        <span class="default-size" v-if="isEmpty()">Нет параметров  <br> </span>
        <span class="default-size" v-if="data.surname">Фамилия: {{ data.surname }} <br> </span>
        <span class="default-size" v-if="data.name">Имя: {{ data.name }} <br> </span>
        <span class="default-size" v-if="data.patronymic">Отчество: {{ data.patronymic }} <br> </span>
        <span class="default-size" v-if="data.phone">Телефон: {{ data.phone }} <br> </span>
        <span class="default-size" v-if="data.role">Должность: {{ data.role }}  <br> </span>
        <span class="default-size" v-if="zones">Зоны: {{ zones }} <br> </span>
        <span class="default-size" v-if="data.dateStart">Дата, от: {{ data.dateStart }} <br> </span>
        <span class="default-size" v-if="data.dateEnd">Дата, до: {{ data.dateEnd }} </span>
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
      title: "Параметры поиска",
      zones: this.parseZones()
    }
  },
  methods: {
    isEmpty() {
      return this.data.size
    },
    parseZones() {
      if (this.data.zones) {
        let zones = this.data.zones.slice()
        if (zones) {
          let zonesString = zones[0]
          zones.shift()
          for (let zone of zones) {
            zonesString += ", " + zone
          }
          return zonesString
        }
      }
      return null
    }
  }
}
</script>

<style scoped>
</style>