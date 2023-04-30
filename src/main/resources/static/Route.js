import { defineAsyncComponent } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'

const MainMenu = defineAsyncComponent(() => import('./component/MainMenu.js'))
const ReceiveOrders = defineAsyncComponent(() => import('./component/orders/ReceiveOrders.js'))
const ViewOrder = defineAsyncComponent(() => import('./component/orders/ViewOrder.js'))
const DeliverOrders = defineAsyncComponent(() => import('./component/delivery/DeliverOrders.js'))
const Cooking = defineAsyncComponent(() => import('./component/cooking/Cooking.js'))

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: MainMenu },
    { path: '/receive/orders', component: ReceiveOrders },
    { path: '/receive/orders/new', component: ViewOrder },
    { path: '/receive/orders/:orderId', component: ViewOrder },
    { path: '/delivery', component: DeliverOrders },
    { path: '/cooking', component: Cooking },
  ]
})

export default router