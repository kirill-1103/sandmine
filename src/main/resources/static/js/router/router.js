import {createRouter, createWebHistory} from 'vue-router'
import ProductionPage from '../pages/ProductionPage.vue'
import ImportExportPage from "../pages/ImportExportPage.vue"
import StaffListPage from "../pages/StaffListPage.vue"
import FindStaffPage from "../pages/FindStaffPage.vue"
import StaffPage from "../pages/StaffPage.vue"
import ShiftsListPage from "../pages/ShiftsListPage.vue";
import SubordinatePage from "../pages/SubordinateAccountPage.vue"
import WardenAccPage from "../pages/WardenAccountPage.vue"
import UserPassPage from "../pages/UserPassInformationPage.vue"
import Authorization from "../pages/Authorization.vue"
import CheckPass from "../pages/CheckPass.vue"
import AdministratorPage from "../pages/AdministratorPage.vue"
import StaffShifts from "../pages/StaffShifts.vue"
import AddEditStaff from "../pages/AddEditStaff.vue"
import {
    ADMINISTRATOR_PAGE_NAME,
    ADMINISTRATOR_PAGE_PATH,
    AUTHORIZATION_PAGE_NAME,
    AUTHORIZATION_PAGE_PATH,
    CHECK_PASS_PAGE_NAME,
    CHECK_PASS_PAGE_PATH,
    EDIT_STAFF_PAGE_NAME,
    EDIT_STAFF_PAGE_PATH,
    FIND_STAFF_PAGE_NAME,
    FIND_STAFF_PAGE_PATH,
    IMPORT_EXPORT_PAGE_NAME,
    IMPORT_EXPORT_PAGE_PATH,
    PRODUCTION_PAGE_NAME,
    PRODUCTION_PAGE_PATH,
    SHIFTS_LIST_PAGE_NAME,
    SHIFTS_LIST_PAGE_PATH,
    STAFF_LIST_PAGE_NAME,
    STAFF_LIST_PAGE_PATH,
    STAFF_SHIFTS_PAGE_NAME,
    STAFF_SHIFTS_PAGE_PATH,
    SUBORDINATE_PAGE_NAME,
    SUBORDINATE_PAGE_PATH,
    USER_PASS_PAGE_NAME,
    USER_PASS_PAGE_PATH,
    WARDEN_ACC_PAGE_NAME,
    WARDEN_ACC_PAGE_PATH,
    WORKER_ACC_PAGE_NAME,
    WORKER_ACC_PAGE_PATH,
    ADD_STAFF_PAGE_NAME,
    ADD_STAFF_PAGE_PATH
} from "./component_names";

const routes = [
    {path: AUTHORIZATION_PAGE_PATH, name: AUTHORIZATION_PAGE_NAME, component: Authorization},
    {path: PRODUCTION_PAGE_PATH, name: PRODUCTION_PAGE_NAME, component: ProductionPage},
    {path: IMPORT_EXPORT_PAGE_PATH, name: IMPORT_EXPORT_PAGE_NAME, component: ImportExportPage},
    {path: STAFF_LIST_PAGE_PATH, name: STAFF_LIST_PAGE_NAME, component: StaffListPage},
    {path: FIND_STAFF_PAGE_PATH, name: FIND_STAFF_PAGE_NAME, component: FindStaffPage},
    {path: WORKER_ACC_PAGE_PATH, name: WORKER_ACC_PAGE_NAME, component: StaffPage},
    {path: SHIFTS_LIST_PAGE_PATH, name: SHIFTS_LIST_PAGE_NAME, component: ShiftsListPage},
    {path: SUBORDINATE_PAGE_PATH, name: SUBORDINATE_PAGE_NAME, component: SubordinatePage},
    {path: WARDEN_ACC_PAGE_PATH, name: WARDEN_ACC_PAGE_NAME, component: WardenAccPage},
    {path: USER_PASS_PAGE_PATH, name: USER_PASS_PAGE_NAME, component: UserPassPage},
    {path: CHECK_PASS_PAGE_PATH, name: CHECK_PASS_PAGE_NAME, component: CheckPass},
    {path: ADMINISTRATOR_PAGE_PATH, name: ADMINISTRATOR_PAGE_NAME, component: AdministratorPage},
    {path: STAFF_SHIFTS_PAGE_PATH, name: STAFF_SHIFTS_PAGE_NAME, component: StaffShifts},
    {path: EDIT_STAFF_PAGE_PATH, name: EDIT_STAFF_PAGE_NAME, component: AddEditStaff},
    {path: ADD_STAFF_PAGE_PATH, name: ADD_STAFF_PAGE_NAME, component: AddEditStaff}
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router

