<template>
  <div class="table_div">
    <table class="table_table head_table">
      <tr>
        <th style="height: 10px" v-for="colName in dataForTable[0].columnNames">{{ colName }}</th>
      </tr>
    </table>
    <div class="scroll-table-body">
      <table class="table_table">
        <tr v-for="[rowNum,rowData] in enumerate(dataForTable[1])"
            v-bind:id="'row_id_'+dataForTable[2][rowNum][dataForTable[2][rowNum].length-1]">
          <td v-for="[colNum, elem] in enumerate(rowData)" v-bind:class="'column_'+colNum"
              v-bind:id="rowNum+'_'+colNum+'_'+dataForTable[2][rowNum][colNum]">
            <div
                v-if="!(dataForTable[0].moreInformationColumn!=null && dataForTable[0].moreInformationColumn===colNum)">
              <button style=" background: transparent;border: none;"></button>
            </div>

            <div v-if="!(dataForTable[0].editColumn!=null && dataForTable[0].editColumn===colNum)">
              {{ elem }}
            </div>
            <div v-if="dataForTable[0].moreInformationColumn!=null && dataForTable[0].moreInformationColumn===colNum">
              <button class='button_in_table' v-on:click="more_(dataForTable[2][rowNum][colNum])">Подробнее</button>
            </div>
            <div v-if="dataForTable[0].editColumn!=null && dataForTable[0].editColumn===colNum">
              <button class='button_in_table' style=" float: right; width:10px; margin-top: -40px;margin-right: 0px"
                      v-on:click="edit_(dataForTable[2][rowNum][dataForTable[2][rowNum].length-1])"><span
                  style="font-size: 20px" class="material-icons">edit</span></button>
              {{ elem }}
            </div>
          </td>
        </tr>
      </table>
    </div>

  </div>

</template>

<script>

export default {
  props: ["dataForTable", "more", "edit"],
  methods: {
    edit_(id) {
      this.edit(id);
    },
    more_(id) {
      this.more(id);
    },
    * enumerate(array) {
      for (let i = 0; i < array.length; i += 1) {
        yield [i, array[i]];
      }
    }
  }
}
</script>

<style>
.table_div {
  word-wrap: break-word;
  position: page;
}

.table_table {
  border: none;
  margin: auto;
  text-align: center;
  /*border: 3px solid rgb(249,168,37) ;*/
  border-collapse: collapse;
  background: rgb(255, 238, 177);
  table-layout: fixed;
  width: 100%;
  word-wrap: break-word;
}

.head_table {
  border: 3px solid rgb(249, 168, 37);
}

.table_table td {
  text-align: center;
  padding: 30px 20px;
  vertical-align: middle;
  horiz-align: center;
  border: 2px solid rgb(249, 168, 37);
}

.table_table th {
  padding: 15px;
}

.table_table tr:first-child td {
  border-top: none;
}

.table_table th {
  background: rgb(255, 198, 110);
  border: none;
  text-align: left;
}

.scroll-table-body {
  height: 600px;
  overflow-x: auto;
  margin-top: 0;
  margin-bottom: 20px;
  border-bottom: 2px solid rgb(249, 168, 37);
}

.button_in_table {
  background: none;
  border: none;
  color: rgb(200, 100, 40);
  font-size: 12px;
}

.button_in_table:hover {
  color: rgb(200, 150, 40);;
}

::-webkit-scrollbar {
  width: 7px;
}

::-webkit-scrollbar-track {
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
}

::-webkit-scrollbar-thumb {
  box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
}
</style>