import axios from 'axios';
import ListOrders from "../orders/ListOrders.js";

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	components: { ListOrders },
	data() {
		const today = new Date()
		today.setHours(0, 0, 0, 0)
		return {
			date: this.$route.query.date || today.toISOString(),
			rider: this.$route.query.rider,
			riders: [],
			orders: []
		}
	},
	mounted() {
		axios.get('/api/delivery/riders')
			.then(response => {
				this.riders = response.data
				this.rider = this.rider || this.riders[0].id
			})
			.then(() => this.loadOrders())
	},
	methods: {
		loadOrders() {
			const date = this.date
			const riderId = this.rider
			this.$router.replace({ query: { date: date, riderId: riderId } })
			axios
				.get('/api/delivery/orders', { params: { date: date, riderId: riderId } })
				.then(response => this.orders = response.data)
		},
		viewOrder(order) {
			this.$router.push({ path: '/receive/orders/' + order.id })
		}
	},
	template: template
}
