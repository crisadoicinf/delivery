import { createApp } from 'vue'
import router from './Route.js'
import FormatterPlugin from "./plugin/FormatterPlugin.js";
import axios from 'axios';

flatpickr.l10ns.default.firstDayOfWeek = 1;
axios
	.get('/test')
	.then(response => {
		console.log(response.data)
	})

const app = createApp({})
app.use(router)
app.use(FormatterPlugin)
app.mount('#app')

