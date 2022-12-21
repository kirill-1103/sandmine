<template>
  <div class="div-for-table">
    <div class="button-div">
      <div class="div-search-button">
        <button class="button btn" style="float:right;width:250px" @click="show_search_modal()">Поиск</button>
        <div>
          <button class="button_params" style="float:right" @click="showModal_params=true">
            Текущие параметры поиска
          </button>
        </div>
      </div>
      <div style="float: left; margin-top:40px">
        <button class="button btn" @click="show_add_modal()">Добавить</button>
      </div>
    </div>
    <Table :dataForTable="data_for_table" :edit="edit"></Table>
  </div>
  <ModalSearch :options_editor="options_editor"
               :options_zone="options_zone"
               :value_editor="value_editor"
               :value_zone="value_zone"
               :show_modal="showModal_search"
               :submit="submit_search"
               :close="close_search"
  />
  <ModalParams :close="close_params"
               :show_modal="showModal_params"
               :data="params"
  />
  <!--  если data_for_edit null , то происходит добавление новой записи. Иначе - редактирование старой.-->
  <ModalAddProduction :close="close_add"
                      :submit_add="submit_add"
                      :show_modal="showModal_add"
                      :options_zone="options_zone"
                      :data_for_edit="data_for_edit"
                      :submit_edit="submit_edit_production"
  />

</template>

<script>
import Table from "../components/table/Table.vue";
import ModalSearch from "../components/modal/ProductionFilterParamsModal.vue"
import ModalParams from "../components/modal/ProductionParamsModal.vue";
import ModalAddProduction from "../components/modal/ProductionAddingModal.vue";
import axios from "axios";

export default {
  components: {
    Table,
    ModalSearch,
    ModalParams,
    ModalAddProduction
  },
  created() {
    axios.get("/api/mine_stats/all")
        .then(response => {
          let data = response.data;
          this.fill_table(data, true);
        })
    axios.get("/api/worker/admins")
        .then(response => {
          let admins = response.data;
          for (let admin of admins) {
            this.admins.set(admin.workerId, admin);
            this.options_editor.push(admin.surname + " " + admin.name[0] + ". " + admin.patronymic[0] + ".");
            this.value_editor.push(admin.workerId)
          }
        })
  },
  data() {
    return {
      data_for_table: start_data,
      showModal_search: false,
      showModal_params: false,
      showModal_add: false,
      value_editor: [],
      value_zone: [],
      options_editor: [],
      options_zone: [],
      params: {empty: true},
      data_for_edit: null,
      zones: new Map(),
      workers: new Map(),
      user_id: this.$route.params.id,
      data_from_db: null,
      admins: new Map()
    }
  },
  methods: {
    close_search() {
      this.showModal_search = false;
    },
    close_params() {
      this.showModal_params = false;
    },
    close_add() {
      this.showModal_add = false;
      this.data_for_edit = null;
    },
    edit(id) {//при нажатии кнопки редактирования в Table сюда придет id
      let production = this.get_production_by_id(id)
      if (!production) {
        console.log('ERROR! PRODUCTION IS NULL IN PRODUCTION PAGE! HELP ME!')
      }
      this.data_for_edit = production
      this.data_for_edit['id'] = id
      this.showModal_add = true
    },
    submit_edit_production(data) {//после редактирования информации о добыче сюда придет отредактированная информация
      let mine_stat = {
        id: data.id,
        zoneId: this.get_zone_id(data.zone),
        editorId: Number(this.user_id),
        date: data.date,
        weight: data.weight,
        lastEditTime: new Date(),
      }
      let config = {headers: {'Content-Type': 'application/json'}}
      axios.post("/api/mine_stats/edit",
          JSON.stringify(mine_stat),
          config
      ).then(response => {
        this.edit_mine_stats(response.data);
        this.check_filter()
      })
    },
    show_add_modal() {
      this.showModal_add = true;
    },
    show_search_modal() {
      this.showModal_search = true
    },
    submit_search(data) {//сюда приходят данные из модалки с поиском
      this.params = data;
      console.log(data)
      let zones = [];
      if (data.zones) {
        for (let zone of data.zones) {
          zones.push(this.get_zone_id(zone));
        }
      }

      let adminIds = []
      if (data.last_editor) {
        for (let admin of this.admins) {
          for (let editor of data.last_editor) {
            let surname = admin[1].surname;
            let name = admin[1].name;
            let patronymic = admin[1].patronymic;
            let fio = surname + " " + name[0] + ". " + patronymic[0] + ".";
            if (fio === editor) {
              adminIds.push(admin[0])
            }
          }
        }
      }
      let filter_params = {
        dateEdit: this.params.date_edit,
        dateFrom: this.params.date_from,
        dateTo: this.params.date_to,
        lastEditorIds: adminIds,//!!!
        weightFrom: this.params.weight_from,
        weightTo: this.params.weight_to,
        zoneIds: zones
      }
      // console.log(filter_params)
      this.add_params();
      let customConfig = {
        headers: {
          'Content-Type': 'application/json'
        }
      };
      axios.post(
          "/api/mine_stats/filter",
          JSON.stringify(filter_params),
          customConfig
      )
          .then(response => {
            let data = response.data;
            this.fill_table(data, true);
          })
    },
    submit_add(data) {//сюда приходят данные из модалки с добавлением
      let mine_stat = {
        id: null,
        zoneId: this.get_zone_id(data.zone),
        editorId: Number(this.user_id),
        date: data.date,
        weight: data.weight,
        lastEditTime: new Date(),
      }
      let config = {headers: {'Content-Type': 'application/json'}}
      axios.post("/api/mine_stats/create",
          JSON.stringify(mine_stat),
          config
      ).then(response => {
        this.add_mine_stats(response.data)
        this.check_filter()
      })
    },
    add_params() {//добавляются параметры для отображения в "Текущих параметрах поиска"
      let c = 0
      for (let i in this.params) {
        if (this.params[i] != null) {
          if (typeof (this.params[i]) === 'string') {
            if (this.params[i].length !== 0) {
              c += 1;
            }
          } else {
            c += 1;
          }
        }
      }
      if (c === 1 && (this.params['zones'] && this.params['zones'].length === 0 || this.params['last_editor'] && this.params['last_editor'].length === 0)) {
        c = 0;
      }
      this.params['empty'] = c === 0;
    },
    get_production_by_id(id) {
      for (let p_ind = 0; p_ind < this.data_for_table[1].length; p_ind++) {
        if (this.data_for_table[2][p_ind][this.data_for_table[2][p_ind].length - 1] === id) {
          return this.data_for_table[1][p_ind];
        }
      }
      return null;
    },
    fill_table(data, clear = false) {
      if (clear) {
        this.data_for_table[1] = [];
        this.data_for_table[2] = [];
        this.zones = new Map();
      }
      this.data_from_db = data;
      for (let stats of data) {
        this.zones.set(stats.zoneId, null)
      }
      axios.get("/api/zone/all")
          .then(response => {//получили зоны
            let zones = response.data;
            this.fill_options_zone(zones);
            for (let i of zones) {
              this.zones.set(i['zoneId'], i['name']);
            }
            let config = {headers: {'Content-Type': 'application/json'}}
            axios.post(
                "/api/worker/ids",
                JSON.stringify(this.get_workers_ids(data)),
                config
            )
                .then(response => {//получили работников
                  let workers = response.data;
                  for (let worker of workers) {
                    this.workers.set(worker.workerId, worker);
                  }

                  for (let stats of data) {
                    this.add_mine_stats(stats)
                  }
                })
          })
    },
    get_workers_ids(data) {
      let workers_ids = []
      for (let stats of data) {
        workers_ids.push(stats.editorId)
      }
      return workers_ids;
    },
    fill_options_zone(zones) {
      this.options_zone = [];
      for (let zone of zones) {
        this.options_zone.push(zone.name)
      }
    },
    get_zone_id(zone_name) {
      for (let zone of this.zones) {
        if (zone[1] === zone_name) {
          return zone[0];
        }
      }
    },
    add_mine_stats(stats) {
      let mine_stats_entity = this.get_my_stats_entity(stats);
      this.data_for_table[1].push(mine_stats_entity.mine_stats)
      this.data_for_table[2].push(mine_stats_entity.ids)
    },
    edit_mine_stats(stats) {
      let mine_stats_entity = this.get_my_stats_entity(stats);
      let index = this.get_index_by_stats_id(stats.id);
      this.data_for_table[1][index] = mine_stats_entity.mine_stats;
      this.data_for_table[2][index] = mine_stats_entity.ids;
    },
    get_index_by_stats_id(id) {
      let index = 0;
      for (let ids of this.data_for_table[2]) {
        if (ids[5] === id) {
          return index;
        }
        index++;
      }
    },
    get_my_stats_entity(stats) {
      let worker = this.workers.get(stats.editorId);
      // console.log("WORKER")
      // console.log(worker)
      // console.log(this.workers)
      // console.log(stats)
      return {
        mine_stats: [
          stats.date,
          stats.weight,
          this.zones.get(stats.zoneId),
          worker['surname'] + ' ' + worker['name'][0] + ". " + worker['patronymic'][0] + '.',
          stats.lastEditTime.substring(0, 10) + " " + stats.lastEditTime.substring(11, 19)
        ],
        ids: [
          -1, -1, stats.zoneId, stats.editorId, -1, stats.id
        ]
      }
    },
    check_filter() {
      let params = this.params;
      console.log('params')
      console.log(params)
      console.log('data for table')
      console.log(this.data_for_table)
      let row_ind = 0;
      while (true) {
        if (row_ind === this.data_for_table[1].length) {
          break;
        }

        if (params.date_from && this.data_for_table[1][row_ind][0] < params.date_from
            || params.date_to && this.data_for_table[1][row_ind][0] > params.date_to) {//date
          this.data_for_table[1].splice(row_ind, 1)
          this.data_for_table[2].splice(row_ind, 1)
          console.log('date')
          continue;
        }

        if (params.date_edit && this.data_for_table[1][row_ind][4].substring(0, 10) !== params.date_edit) {//date_edit
          this.data_for_table[1].splice(row_ind, 1)
          this.data_for_table[2].splice(row_ind, 1)
          console.log('date_edit')
          continue;
        }

        if (params.weight_from && this.data_for_table[1][row_ind][1] < params.weight_from ||
            params.weight_to && this.data_for_table[1][row_ind][1] > params.weight_to) {//weight
          this.data_for_table[1].splice(row_ind, 1)
          this.data_for_table[2].splice(row_ind, 1)
          console.log('weight')
          continue;
        }

        if (params.zones || params.last_editor) {//zones and editors
          let flag = false;
          if (params.last_editor && params.last_editor.length > 0) {
            for (let editor of params.last_editor) {
              if (editor === this.data_for_table[1][row_ind][3]) {
                flag = true;
                break;
              }
            }
          }
          if (params.zones && params.zones.length > 0) {
            for (let zone of params.zones) {
              if (zone === this.data_for_table[1][row_ind][2]) {
                flag = true;
                break;
              }
            }
          }
          if (flag) {
            row_ind++;
          } else {
            this.data_for_table[1].splice(row_ind, 1)
            this.data_for_table[2].splice(row_ind, 1)
          }

          continue;
        }
        row_ind++;
      }
    }


  }
}

let start_data = [
  {
    columnNames: ['Дата', 'Вес, Т', 'Зона', 'Последний редактор', 'Время редактирования'],
    editColumn: 1,
  }, [], []
]

let test_title = "Заголовок"

</script>

<style>
.btn {
  padding-bottom: 40px;
}

.button_params {
  background: none;
  border: none;
  color: rgb(200, 100, 40);
  font-size: 12px;
  width: 300px;
}

.button_params:hover {
  color: #F9C025;
}


.search-button {
  float: right;
  width: 250px;
}


</style>

<style src="@vueform/multiselect/themes/default.css">

</style>