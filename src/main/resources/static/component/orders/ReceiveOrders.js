import axios from 'axios';
import DatesUtil from "../../plugin/DatesUtil.js";
import ListOrders from "./ListOrders.js";

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	components: { ListOrders },
	data() {
		const [from, to] = DatesUtil.getCurrentRangeWeek()
		return {
			from: new Date(this.$route.query.from || from.toISOString()),
			to: new Date(this.$route.query.to || to.toISOString()),
			orders: []
		}
	},
	mounted() {
		const cmp = this;
		flatpickr(this.$refs.selectDate, {
			altInput: true,
			altFormat: "D d of M",
			mode: "range",
			dateFormat: "Z",
			defaultDate: [this.from, this.to],
			onChange: function(selectedDates) {
				if (selectedDates.length === 2) {
					cmp.loadOrders(selectedDates[0], selectedDates[1])
				}
			}
		})
		this.loadOrders(this.from, this.to)
	},
	methods: {
		loadOrders(dateFrom, dateTo) {
			this.$router.replace({
				query: { from: dateFrom.toISOString(), to: dateTo.toISOString() }
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
