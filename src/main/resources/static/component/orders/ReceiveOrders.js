import axios from 'axios';
import DatesUtil from "../../plugin/DatesUtil.js";
import ListOrders from "./ListOrders.js";

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	components: { ListOrders },
	data() {
		const [d1, d2] = DatesUtil.getCurrentRangeWeek()
		const from = this.$route.query.from || d1.toISOString()
		const to = this.$route.query.to || d2.toISOString()
		return {
			from: from,
			to: to,
			orders: []
		}
	},
	computed: {
		dateRange: {
			get() {
				if (this.to === undefined) return this.from
				return this.from + ' to ' + this.to
			},
			set(value) {
				const [from, to] = value.split(" to ")
				this.from = from
				this.to = to
			},
		}
	},
	mounted() {
		this.loadOrders()
	},
	methods: {
		loadOrders() {
			const dateFrom = this.from
			const dateTo = this.to
			if (dateTo === undefined) return
			this.$router.replace({
				query: { from: dateFrom, to: dateTo }
			})
			axios
				.get('/api/orders', { params: { from: dateFrom, to: dateTo } })
				.then(response => {
					const orders = response.data;
					orders.sort((o1, o2) => o1.customerName.localeCompare(o2.customerName))
					this.orders = orders
				})
		},
		viewOrder(order) {
			this.$router.push({ path: '/receive/orders/' + order.id })
		}
	},
	template: template
}
