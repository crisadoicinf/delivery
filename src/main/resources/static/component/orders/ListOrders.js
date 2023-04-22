import axios from 'axios';

const template = await axios.get(import.meta.url + "/../ListOrders.html")
	.then(response => response.data)

export default {
	props: ['orders'],
	template: template
}
