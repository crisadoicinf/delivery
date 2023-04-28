import axios from 'axios';
import ListOrders from "./ListOrders.js";
import WhatsAppModal from "./WhatsAppModal.js"

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	components: { ListOrders, WhatsAppModal },
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
				.then(response => {
					const orders = response.data;
					orders.sort((o1, o2) => o1.customerName.localeCompare(o2.customerName))
					this.orders = orders
				})
		},
		viewOrder(order) {
			this.$router.push({ path: '/receive/orders/' + order.id })
		},
		openWhatsappModal(order) {
			console.log(order)
			this.$refs.whatsAppModal.show(order.customerPhone)
		}
	},
	template: template
}
