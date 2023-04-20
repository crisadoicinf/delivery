import { createApp } from 'vue'
import router from './Route.js'
import FormatterPlugin from "./plugin/FormatterPlugin.js";

flatpickr.l10ns.default.firstDayOfWeek = 1;

const app = createApp({})
app.use(router)
app.use(FormatterPlugin)
app.mount('#app')

