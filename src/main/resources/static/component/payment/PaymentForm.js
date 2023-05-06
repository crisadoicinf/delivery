import axios from 'axios';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

const riders = await axios.get('/api/delivery/riders')
	.then(response => response.data)
const bankAccounts = await axios.get('/api/bank-accounts')
	.then(response => response.data)

export default {
	props: ['payment'],
	emits: ['cancel', 'save'],
	template: template,
	data() {
		return {
			riders: riders,
			bankAccounts: bankAccounts,
			transferenceTypes: [
				{ value: "cash", name: "Cash" },
				{ value: "transference", name: "Transference" }
			]
		}
	}
}
