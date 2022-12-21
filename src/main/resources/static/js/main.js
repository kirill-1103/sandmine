import {createApp} from 'vue'
import App from "./pages/App.vue"
import router from 'router/router'
import {createStore} from "vuex"
import createPersistedState from "vuex-persistedstate"

const store = createStore({
    state(){
        return{
            userName: "",
            userRole: ""
        }
    },
    plugins : [createPersistedState({paths:['data']})]
})

const app = createApp(App).use(router).use(store)
app.mount('#app')
