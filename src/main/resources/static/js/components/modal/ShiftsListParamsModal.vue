<template>
  <Modal :title="title">
    <template #body>
      <div style="text-align: center" class="with-bot-margin-medium">
        <span class="default-size" v-if="is_empty()">Нет параметров  <br> </span>
        <span class="default-size" v-if="data.date_from">Дата с: {{ data.date_from }} <br> </span>
        <span class="default-size" v-if="data.date_to">Дата по: {{ data.date_to }} <br> </span>
        <span class="default-size" v-if="data.zones">Зоны: {{ this.zones }}  <br> </span>
        <span class="default-size" v-if="data.attended">Присуттвовал: {{ data.attended }}  <br> </span>
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
  name: "ShiftsListParamsModal",
  components: {Modal},
  props: [
    "close",
    "data"
  ],
  data() {
    return {
      title: 'Параметры поиска',
      zones: this.parse_zones()
    }
  },
  methods: {
    is_empty() {
      return this.data.size
    },
    parse_zones() {
      if (!this.data.zones) {
        return null
      }
      let zones = this.data.zones.slice()
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