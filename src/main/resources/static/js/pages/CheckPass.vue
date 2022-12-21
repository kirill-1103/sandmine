<template>
  <div class="background-parent">
    <div class="background">
      <label class="input-hint-text-small">ID пропуска</label>
      <br/>
      <input
          placeholder="ID пропуска"
          type="number"
          v-model="passID"
          class="with-bot-margin-medium"
      />
      <br/>
      <button class="button" @click="checkPass">Проверить</button>
    </div>
  </div>
</template>

<script>
import {USER_PASS_PAGE_NAME} from "../router/component_names";

export default {
  name: "CheckPassPage",
  data() {
    return {
      passID: 0
    }
  },
  methods: {
    checkPass() {
      this.$router.push({name: USER_PASS_PAGE_NAME, params: {id: this.$route.params.id, passId: this.passID}})
    }
  }
}
</script>

<style scoped>
@import "../../css/text.css";
@import "../../css/button.css";
@import "../../css/input.css";
@import "../../css/main.css";
</style>
