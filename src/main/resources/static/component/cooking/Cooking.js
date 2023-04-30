import axios from 'axios';
import DatesUtil from "../../plugin/DatesUtil.js";

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	data() {
		const [d1, d2] = DatesUtil.getCurrentRangeWeek()
		const from = this.$route.query.from || d1.toISOString()
		const to = this.$route.query.to || d2.toISOString()
		return {
			from: from,
			to: to,
			cookingProducts: []
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
		this.loadCookingProducts()
	},
	methods: {
		loadCookingProducts() {
			const dateFrom = this.from
			const dateTo = this.to
			if (dateTo === undefined) return
			this.$router.replace({
				query: { from: dateFrom, to: dateTo }
			})
			axios
				.get('/api/cooking', { params: { from: dateFrom, to: dateTo } })
				.then(response => {
					this.cookingProducts = response.data
						.sort((o1, o2) => o1.productName.localeCompare(o2.productName))
				})
		}
	},
	template: template
}
