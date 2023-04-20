import {defineAsyncComponent} from 'vue'
import {createRouter, createWebHistory} from 'vue-router'

const MainMenu = defineAsyncComponent(() => import('./component/MainMenu.js'))
const ListOrders = defineAsyncComponent(() => import('./component/orders/ListOrders.js'))
const ViewOrder = defineAsyncComponent(() => import('./component/orders/ViewOrder.js'))
const DeliveryOrdersPage = defineAsyncComponent(() => import('./component/delivery/DeliveryOrdersPage.js'))

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {path: '/', component: MainMenu},
    {path: '/receive/orders', component: ListOrders},
    {path: '/receive/orders/new', component: ViewOrder},
    {path: '/receive/orders/:orderId', component: ViewOrder},
    {path: '/delivery', component: DeliveryOrdersPage},
  ]
})

export default router