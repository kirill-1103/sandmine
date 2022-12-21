<template>
  <div class="background-parent">
    <div class="div-for-table">
      <div class="div-search-button">
        <button
            class="button one_line_buttons with-bot-margin-large"
            style="width: 250px; float: right"
            @click="showSearch()"
        >
          Поиск
        </button>
      </div>
      <Table :dataForTable="dataForTable" :more="navigateStaffAccount"></Table>
      <StaffShiftsSearchModal
          v-if="showSearchModal"
          :data="searchData"
          :options_zones="optionsZone"
          :submit="submitSearch"
          :close="closeSearch"
      ></StaffShiftsSearchModal>
      <StaffShiftsParamsModal
          v-if="showSearchParamsModal"
          :data="searchData"
          :close="closeSearchParams"
      ></StaffShiftsParamsModal>
      <div class="div-search-button">
        <button class="button" style="float: right" @click="showSearchParams()">
          Текущие параметры поиска
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import Table from "../components/table/Table.vue";
import Modal from "../components/modal/Modal.vue";
import StaffShiftsParamsModal from "../components/modal/StaffShiftsParamsModal.vue";
import StaffShiftsSearchModal from "../components/modal/StaffShiftsSearchModal.vue";
import axios from 'axios'
import {SUBORDINATE_PAGE_NAME} from "../router/component_names";

export default {
  name: "StaffShiftspage",
  components: {
    StaffShiftsSearchModal,
    StaffShiftsParamsModal,
    Modal,
    Table
  },
  data() {
    return {
      dataForTable: [{
        columnNames: ['ФИО', 'Телефон', 'Должность', 'Зона', 'Дата смены'],
        editColumn: -1,
        moreInformationColumn: 0
      }, [], []],
      showSearchModal: false,
      showSearchParamsModal: false,
      optionsZone: [],
      searchData: {
        surname: null,
        name: null,
        patronymic: null,
        phone: null,
        role: null,
        zones: null,
        dateStart: null,
        dateEnd: null,
      },
      zones: null
    }
  },
  async created() {
    const [workersWithShifts, zones] = await Promise.all([
      axios.get(`/api/shifts/all`),
      axios.get(`/api/zone/all`)
    ])
    console.log(workersWithShifts)
    zones.data.forEach(zone => this.optionsZone.push(zone.name))
    this.zones = zones.data
    this.apply_data(workersWithShifts.data)
  },
  methods: {
    navigateStaffAccount(id) {
      this.$router.push({name: SUBORDINATE_PAGE_NAME, params: {id: id}})
    },
    showSearch() {
      this.showSearchModal = true
    },
    closeSearch() {
      this.showSearchModal = false
    },
    submitSearch(searchData) {
      this.searchData = searchData
      let customConfig = {
        headers: {
          'Content-Type': 'application/json'
        }
      }

      let zoneIds = []
      if (searchData.zones) {
        searchData.zones.forEach(zone => {
          let temp = this.zones.find((it) => {
            return it.name === zone
          })?.zoneId
          if (temp) {
            zoneIds.push(temp)
          }
        })
      }

      axios.post(
          "/api/shifts/all/filter",
          JSON.stringify({
            surname: searchData.surname,
            name: searchData.name,
            patronymic: searchData.patronymic,
            phone: searchData.phone,
            role: searchData.role,
            zoneIds: zoneIds,
            dateFrom: searchData.dateStart,
            dateTo: searchData.dateEnd
          }),
          customConfig
      ).then(response => {
        this.apply_data(response.data)
      })
    },
    showSearchParams() {
      this.showSearchParamsModal = true
    },
    closeSearchParams() {
      this.showSearchParamsModal = false
    },
    parse_zone(zoneId, zonesMappings) {
      return zonesMappings
          .find(mapping =>
              mapping.zoneId === zoneId
          ).name
    },
    apply_data(data) {
      let dataForTable = [{
        columnNames: ['ФИО', 'Телефон', 'Должность', 'Зона', 'Дата смены'],
        editColumn: -1,
        moreInformationColumn: 0
      }, [], []]
      data.forEach((worker) => {
        dataForTable[1].push([
          worker.surname + " " + worker.name[0] + "." + worker.patronymic[0] + ".",
          worker.phoneNumber + " / " + worker.email,
          worker.role,
          this.parse_zone(worker.zoneId, this.zones),
          worker.date
        ])
        dataForTable[2].push([
          worker.workerId, -1, -1, -1, -1, -1
        ])
      })
      this.dataForTable = dataForTable
    }
  }
}
</script>

<style scoped>
@import "../../css/text.css";
@import "../../css/main.css";
</style>
