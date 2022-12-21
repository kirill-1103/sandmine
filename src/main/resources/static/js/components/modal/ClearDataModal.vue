<template>
  <Modal :title="title" v-if="show_modal">
    <template #body>

    </template>
    <template #footer>
      <button class="button with-top-margin-medium" @click="submit_">Подтвердить</button>
      <button class="button with-top-margin-medium" @click="close_">Отмена</button>
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
    "show_modal"
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
      this.submit()
      this.close()
    },
  },
  data(){
    return{
      title:"Подтвердите удаление всех данных"
    }
  }
}
</script>

<style>
</style>