<template>
  <div class="div-for-table">
    <div class="div-search-button">
      <button class="button one_line_buttons with-bot-margin-large" style="width: 250px; float: right"
              @click="show_search()">Поиск
      </button>
    </div>
    <Table :data-for-table="dataForTable" :more="more_information">
    </Table>
    <StaffListSearchModal v-if="show_search_modal"
                          :options_zones="options_zones"
                          :options_roles="options_roles"
                          :title="title"
                          :submit="submit_search"
                          :close="close_search">

    </StaffListSearchModal>
    <StaffListParamsModal v-if="show_filter_params_modal"
                          :data="filter_params"
                          :close="close_filter_params">
    </StaffListParamsModal>
    <div class="div-search-button">
      <button class="button" style="float: right" @click="show_filter_params()">Текущие параметры
        поиска
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import {SUBORDINATE_PAGE_NAME} from '../router/component_names'
import Table from "../components/table/Table.vue";
import Modal from "../components/modal/Modal.vue";
import StaffListSearchModal from "../components/modal/StaffListSearchModal.vue";
import StaffListParamsModal from "../components/modal/StaffListParamsModal.vue";

export default {
  name: "StaffListPage",
  components: {StaffListParamsModal, StaffListSearchModal, Modal, Table},
  data() {
    return {
      dataForTable: [{columnNames: ["ФИО", 'Телефон', 'Должность', "Доступ в зоны"], moreInformationColumn: 0}, [], []],
      title: "Поиск",
      options_zones: [],
      options_roles: [],
      show_search_modal: false,
      show_filter_params_modal: false,
      filter_params: {},
      zones_mapping: []
    }
  },
  async created() {
    const [workers_response, worker_roles, zones_response] = await Promise.all([
      axios.get(`/api/worker/all`),
      axios.get(`/api/worker/roles`),
      axios.get(`/api/zone/all`)
    ]);

    this.zones_mapping = zones_response.data
    this.options_zones = []
    this.zones_mapping.forEach(zone_pair => {
      this.options_zones.push(zone_pair.name)
    })

    this.options_roles = worker_roles.data

    let workers = workers_response.data

    this.dataForTable = [{
      columnNames: ["ФИО", 'Телефон', 'Должность', "Доступ в зоны"],
      moreInformationColumn: 0
    }, [], []]

    for (let i = 0; i < workers.length; i++) {
      this.dataForTable[1].push([
        workers[i].surname + " " + workers[i].name + " " + workers[i].patronymic,
        workers[i].phoneNumber,
        workers[i].role,
        this.parse_zones(workers[i].zonesWithAccess)
      ],)
      this.dataForTable[2].push([
        workers[i].workerId, -1, -1, -1, -1, -1 //последний айди - айди строки
      ])
    }
  },
  methods: {
    show_search() {
      this.show_search_modal = true
    },
    close_search() {
      this.show_search_modal = false
    },
    submit_search(filter_params) {
      this.filter_params = filter_params

      let filterZoneIds = null
      if (this.filter_params.zones) {
        filterZoneIds = []
        this.filter_params.zones.forEach(zone => {
          let zone_pair = this.zones_mapping.find(zone_pair => {
            return zone_pair.name === zone
          })
          filterZoneIds.push(zone_pair.zoneId)
        })
      }

      let filterParams = {
        name: this.filter_params.name,
        surname: this.filter_params.surname,
        patronymic: this.filter_params.patronymic,
        phoneNumber: this.filter_params.phone,
        roles: this.filter_params.roles,
        zoneIds: filterZoneIds,
      }

      console.log(filterParams)

      let customConfig = {
        headers: {
          'Content-Type': 'application/json'
        }
      };
      axios.post(
          "/api/worker/filter",
          JSON.stringify(filterParams),
          customConfig
      )
          .then(response => {
            let workers = response.data
            console.log("FILTER RESPONSE")
            console.log(workers)

            this.dataForTable = [{
              columnNames: ["ФИО", 'Телефон', 'Должность', "Доступ в зоны"],
              moreInformationColumn: 0
            }, [], []]

            for (let i = 0; i < workers.length; i++) {
              this.dataForTable[1].push([
                workers[i].surname + " " + workers[i].name + " " + workers[i].patronymic,
                workers[i].phoneNumber,
                workers[i].role,
                this.parse_zones(workers[i].zonesWithAccess)
              ],)
              this.dataForTable[2].push([
                workers[i].workerId, -1, -1, -1, -1, -1 //последний айди - айди строки
              ])
            }
          })
          .catch(e => {
            console.log(e)
          })
    },
    show_filter_params() {
      this.show_filter_params_modal = true
    },
    close_filter_params() {
      this.show_filter_params_modal = false
    },
    more_information(worker_id) {
      this.$router.push({name: SUBORDINATE_PAGE_NAME, params: {id: worker_id}})
    },
    parse_zones(zones) {
      let mapped_zones = []
      if (zones) {
        zones.forEach(zone_id => {
          let zone_pair = this.zones_mapping.find(zone_pair => {
            return zone_pair.zoneId === zone_id
          })
          mapped_zones.push(zone_pair.name)
        })
      }

      if (mapped_zones) {
        let zonesString = mapped_zones[0]
        mapped_zones.shift()
        for (let zone of mapped_zones) {
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