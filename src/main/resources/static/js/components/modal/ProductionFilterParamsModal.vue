<template>
  <Modal :title="title" v-if="show_modal">
    <template #body>
      <div>
        <label for="date_from">Дата от</label>
        <br>
        <input class="input" id='date_from' name="date_from" type="date" v-model="date_from">

        <label for="date_from">Дата, до</label>
        <br>
        <input class="input" id='date_to' name="date_to" type="date" v-model="date_to">

        <label for="weight_from">Вес, т, от</label>
        <br>
        <input class="input" id="weight_from" name="weight_from" type="number" v-model="weight_from">

        <label for="weight_to">Вес, т, до</label>
        <br>
        <input class="input" id="weight_to" name="weight_to" type="number" v-model="weight_to">

        <label for="last_editor">Последний редактор</label>
        <br>
        <Multiselect
            id="last_editor"
            mode="multiple"
            name="last_editor"
            :close-on-select="true"
            :options=options_editor
            :multiple="true"
            :clear-on-select="false"
            :preserve-search="false"
            placeholder="Последний редактор"
            :preselect-first="true"
            :searchable="true"
            v-model="last_editor"/>
        <!--            track-by="name"-->
        <!--            label="name"-->
        <!--            slot-scope="{ values, search, isOpen }"-->
        <!--        >-->
        <!--          <template slot="singlelabel">{{ value }}</template>-->
        <!--        </Multiselect>-->


        <label for="date_edit">Дата редактирования</label>
        <br>
        <input class="input" id='date_edit' name="date_edit" type="date" v-model="date_edit">

        <label for="zones">Зоны</label>
        <br>
        <!--        <input class="input" id="zones" name="zones" type="text">-->
        <Multiselect
            name="zones"
            id="zones"
            mode="multiple"
            :close-on-select="false"
            :options=options_zone
            :multiple="true"
            :clear-on-select="false"
            :preserve-search="false"
            placeholder="Зоны"
            :preselect-first="true"
            v-model="zones"
        />

      </div>
    </template>
    <template #footer>
      <!--      можно удалить если не понравится-->
      <button class="button" @click="clear_">Очистить</button>
      <!--                   -->
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
    "options_editor",
    "options_zone",
    "value_editor",
    "value_zone",
    "show_modal",
    "submit",
    "close"
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
        weight_from: this.weight_from,
        weight_to: this.weight_to,
        last_editor: this.last_editor,
        date_edit: this.date_edit,
        zones: this.zones,
        title: "Поиск"
      }
      this.submit(data)
      this.close()
    },
    clear_() {
      this.date_from = null;
      this.date_to = null;
      this.weight_from = null;
      this.weight_to = null;
      this.last_editor = null;
      this.date_edit = null;
      this.zones = null;
    }
  },
  data() {
    return {
      date_from: null,
      date_to: null,
      weight_from: null,
      weight_to: null,
      last_editor: null,
      date_edit: null,
      zones: null
    }
  }
}
</script>

<style>

</style>
