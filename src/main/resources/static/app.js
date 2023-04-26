import { createApp } from 'vue'
import router from './Route.js'
import FormatterPlugin from "./plugin/FormatterPlugin.js";
import Flatpickr from './plugin/Flatpickr.js';

flatpickr.l10ns.default.firstDayOfWeek = 1;

const app = createApp({})
app.use(router)
app.use(FormatterPlugin)

app.directive('flatpickr', Flatpickr)
app.mount('#app')

