import axios from 'axios';
import PromptModal from "../PromptModal.js";
import ConfirmModal from "../ConfirmModal.js";
import DatesUtil from "../../plugin/DatesUtil.js";
import { formatPhoneNumber } from "../../plugin/FormatterPlugin.js";
import PaymentsModal from './PaymentsModal.js';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	components: { PromptModal, ConfirmModal, PaymentsModal },
	data() {
		return {
			riders: [],
			saving: false,
			deliveryDateFromPicker: null,
			deliveryDateRangePicker: null,
			order: {
				customerName: null,
				customerPhone: null,
				note: null,
				delivered: false,
				deliveryDate: DatesUtil.getCurrentDeliveryDate().toISOString(),
				deliveryAddress: null,
				deliveryRiderId: null,
				deliveryPrice: 0,
				discount: 0,
				items: []
			}
		}
	},
	computed: {
		orderId() {
			return this.$route.params.orderId;
		},
		computedTotalPrice() {
			const totalItems = this.order.items.reduce((acc, item) => acc + item.productPrice * item.quantity, 0)
			return Math.max(0, totalItems + this.order.deliveryPrice - this.order.discount)
		},
		customerPhone: {
			get() {
				const value = this.order.customerPhone;
				return value ? formatPhoneNumber(value) : value
			},
			set(value) {
				this.order.customerPhone = '+' + formatPhoneNumber(value).replace(/\D/g, '')
			}
		},
		whatsappLink() {
			const customerPhone = this.order.customerPhone
			if (customerPhone === null) return ''
			return 'https://api.whatsapp.com/send/?phone=' + customerPhone.replace('+', '')
		},
		telephoneLink() {
			const customerPhone = this.order.customerPhone
			if (customerPhone === null) return ''
			return 'tel:' + customerPhone
		},
		googleMapSearch() {
			const deliveryAddress = this.order.deliveryAddress
			if (deliveryAddress === null) return ''
			return 'https://www.google.com/maps/search/' + encodeURIComponent(deliveryAddress)

		}
	},
	mounted() {
		this.deliveryDateFromPicker = flatpickr(this.$refs.deliveryDateFromPicker, {
			altInput: true,
			enableTime: true,
			noCalendar: true,
			time_24hr: true,
			altFormat: "H:i",
			dateFormat: "Z",
			allowInput: true,
			defaultDate: this.order.deliveryDate,
		});
		this.deliveryDateToPicker = flatpickr(this.$refs.deliveryDateToPicker, {
			altInput: true,
			enableTime: true,
			noCalendar: true,
			time_24hr: true,
			altFormat: "H:i",
			dateFormat: "Z",
			allowInput: true
		});
		const productsRequest = axios.get('/api/orders/products')
			.then(response => response.data)
			.then(products => {
				this.order.items = products
					.sort((p1, p2) => p1.name.localeCompare(p2.name))
					.map(product => {
						return {
							productId: product.id,
							productName: product.name,
							productPrice: product.price,
							quantity: 0,
							note: ""
						}
					})
			})
		const ridersRequest = axios.get('/api/delivery/riders')
			.then(response => this.riders = response.data)
		if (this.orderId !== undefined) {
			axios.all([
				axios.get('/api/orders/' + this.orderId).then(response => response.data),
				productsRequest,
				ridersRequest
			]).then(([order]) => this.setOrder(order))
		}
	},
	methods: {
		setOrder(order) {
			this.$el.querySelectorAll(".is-invalid").forEach(el => el.classList.remove("is-invalid"))
			this.order.items.forEach(item => {
				item.quantity = 0
				item.note = ""
				const newItem = order.items.find(newItem => newItem.productId === item.productId)
				if (newItem) {
					item.quantity = newItem.quantity
					item.note = newItem.note
				}
			})
			order.items = this.order.items
			this.order = order
			//this.deliveryDatePicker.setDate(order.deliveryDate, true)
			this.deliveryDateFromPicker.setDate(order.deliveryDate, true)
			this.deliveryDateToPicker.setDate(order.deliveryDateRange, true)
		},
		openItemNoteModal(item) {
			this.$refs.promptModal
				.prompt(item.productName, item.note)
				.then(value => item.note = value)
				.catch(() => {
				})
		},
		openPaymentModal(){
			this.$refs.paymentModal
				.show()
		},
		saveOrder() {
			this.$el.querySelectorAll(":invalid").forEach(el => el.classList.add("is-invalid"))
			const form = this.$refs.formOrder
			if (!form.checkValidity()) return;
			this.saving = true
			const order = Object.assign({}, this.order)
			order.items = order.items
				.filter(item => item.quantity > 0)
				.map(item => {
					return {
						productId: item.productId,
						quantity: item.quantity,
						note: item.note,
					}
				})
			const deliveryDate = new Date(order.deliveryDate)
			const deliveryDateFrom = this.deliveryDateFromPicker.selectedDates[0]
			const deliveryDateTo = this.deliveryDateToPicker.selectedDates[0]
			if (deliveryDate) {
				order.deliveryDate = deliveryDate.toISOString()
				if (deliveryDateFrom) {
					deliveryDate.setHours(deliveryDateFrom.getHours(), deliveryDateFrom.getMinutes())
					order.deliveryDate = deliveryDate.toISOString()
				}
				if (deliveryDateTo) {
					const range = new Date(deliveryDate.getTime())
					range.setHours(deliveryDateTo.getHours(), deliveryDateTo.getMinutes())
					order.deliveryDateRange = range.toISOString()
				}
			}
			if (this.orderId === undefined) {
				axios.post("/api/orders", order)
					.then(response => {
						this.setOrder(response.data)
						this.$router.replace({ path: '/receive/orders/' + response.data.id })
					})
					.then(() => this.saving = false)
					.catch(() => this.saving = false)
			} else {
				axios.put("/api/orders/" + this.orderId, order)
					.then(response => this.setOrder(response.data))
					.then(() => this.saving = false)
					.catch(() => this.saving = false)
			}
		},
		deleteOrder() {
			this.$refs.confirmModal
				.confirm(null, "Delete order?")
				.then(() => axios.delete("/api/orders/" + this.orderId))
				.then(() => this.$router.push({ path: '/receive/orders', replace: true }))
				.catch(() => {
				})
		},
		markAsDelivered() {
			axios.put("/api/delivery/orders/" + this.orderId + "?delivered=true")
				.then(() => this.order.delivered = true)
		}
	},
	template: template
}
