<template>
  <div class="div-for-table">
    <div class="div_left_top">
      <span class="default-size">Смены {{ fullName }}</span>
    </div>
    <div class="div-search-button">
      <button class="button one_line_buttons with-bot-margin-large" style="width: 250px; float: right"
              @click="show_search()">Поиск
      </button>
    </div>
    <Table :data-for-table="dataForTable">
    </Table>
    <ShiftsListSearchModal v-if="show_search_modal"
                           :submit="submit_search"
                           :close="close_search"
                           :options_zones="options_zones">

    </ShiftsListSearchModal>
    <ShiftsListParamsModal v-if="show_filter_params_modal"
                           :data="filter_params"
                           :close="close_filter_params">

    </ShiftsListParamsModal>
    <div class="div-search-button">
      <button class="button" style="float: right" @click="show_filter_params()">Текущие параметры
        поиска
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import {AUTHORIZATION_PAGE_NAME} from '../router/component_names'
import Table from "../components/table/Table.vue";
import Modal from "../components/modal/Modal.vue";
import ShiftsListParamsModal from "../components/modal/ShiftsListParamsModal.vue";
import ShiftsListSearchModal from "../components/modal/ShiftsListSearchModal.vue";

export default {
  name: "StaffListPage",
  components: {ShiftsListSearchModal, ShiftsListParamsModal, Modal, Table},
  data() {
    return {
      fullName: "",
      dataForTable: [{columnNames: ["Дата смены", "Зона", "Присутствовал"], moreInformationColumn: -1}, [], []],
      show_search_modal: false,
      show_filter_params_modal: false,
      filter_params: {date_from: "30.01.2001"},
      options_zones: [],
      zones_mapping: [],
      test: [{id: 0, name: "asdada"}, {id: 1, name: "assadsdad2312311dada"}, {id: 2, name: "asd12312123ada"}],
      zonesMap: new Map()
    }
  },
  created() {
    axios.get("/api/shifts/" + this.$route.params.id)
        .then(response => {
          let data = response.data
          console.log(data)
          axios.get("/api/zone/all")
              .then(response=>{
                let zones = response.data;
                for(let zone of zones){
                  this.zonesMap.set(zone.zoneId,zone.name)
                }
                this.fullName = data.surname + " " + data.name[0] + ". " + data.patronymic[0] + "."

                this.dataForTable = [{
                  columnNames: ["Дата смены", "Зона", "Присутствовал"],
                  moreInformationColumn: -1
                }, [], []]
                if (data.shifts) {
                  for (let i = 0; i < data.shifts.length; i++) {
                    let id = i;
                    this.dataForTable[1].push([
                      data.shifts[i].date,
                      this.zonesMap.get(data.shifts[i].zoneId),
                      data.shifts[i].attended ? "Да" : "Нет",
                    ],);
                    this.dataForTable[2].push([
                      id, -1, -1 //последний айди - айди строки
                    ])
                  }
                }
              })
        })
        .catch(e => {
          this.$router.push({name: AUTHORIZATION_PAGE_NAME})
        })
  },
  methods: {
    show_search() {
      axios.get("/api/zone/all")
          .then(response => {
            let data = response.data
            console.log(data)

            this.options_zones = []
            this.zones_mapping = []
            for (let i = 0; i < data.length; i++) {
              this.options_zones.push(data[i].name)
              this.zones_mapping.push({zoneId: data[i].zoneId, name: data[i].name})
            }

            this.show_search_modal = true
          })
          .catch(e => {
            console.log(e)
          })
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

      let attended = this.filter_params.attended;
      if(attended != null){
        attended = this.filter_params.attended === "Да"
      }
      let filterParams = {
        workerId: this.$route.params.id,
        dateFrom: this.filter_params.date_from,
        dateTo: this.filter_params.date_to,
        attended: attended,
        zoneIds: filterZoneIds,
      }


      console.log(filterParams)

      let customConfig = {
        headers: {
          'Content-Type': 'application/json'
        }
      };
      axios.post(
          "/api/shifts/filter",
          JSON.stringify(filterParams),
          customConfig
      )
          .then(response => {
            let data = response.data
            console.log("FILTER RESPONSE")
            console.log(data)

            this.dataForTable = [{
              columnNames: ["Дата смены", "Зона", "Присутствовал"],
              moreInformationColumn: -1
            }, [], []]
            for (let i = 0; i < data.length; i++) {
              let id = i;
              this.dataForTable[1].push([
                data[i].date,
                this.zonesMap.get(data[i].zoneId),
                data[i].attended ? "Да" : "Нет",
              ],);
              this.dataForTable[2].push([
                id, -1, -1 //последний айди - айди строки
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
    }
  }
}
</script>

<style scoped>

.div_left_top {
  float: left;
  display: inline-block;
  width: 50%;
  margin-top: 15px;
}
</style>