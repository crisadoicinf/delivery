import axios from 'axios';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	props: ['orders'],
	emits: ['select', 'chat', 'delivered'],
	template: template,
	methods: {
		getTelephoneLink(order) {
			const customerPhone = order.customerPhone
			if (customerPhone === null) return ''
			return 'tel:' + customerPhone
		},
		getGoogleMapLink(order) {
			const deliveryAddress = order.deliveryAddress
			if (deliveryAddress === null) return ''
			return 'https://www.google.com/maps/search/' + encodeURIComponent(deliveryAddress)
		}
	}
}
