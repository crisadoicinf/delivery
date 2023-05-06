import axios from 'axios';
import ConfirmModal from '../ConfirmModal.js';
import PaymentForm from '../payment/PaymentForm.js';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	template: template,
	components: { ConfirmModal, PaymentForm },
	emits: ['deletePayment', 'savePayment'],
	props: ['orderId'],
	data() {
		return { payments: [], paymentForms: [] }
	},
	methods: {
		setPayments(payments) {
			this.paymentForms = payments.map(payment => {
				return {
					editing: false,
					value: Object.assign({}, payment)
				}
			});
			this.payments = payments;
		},
		toggleFormPayment(payment) {
			const pi = this.payments.indexOf(payment)
			const formPayment = this.paymentForms[pi]
			if (formPayment.editing) this.hideFormPayment(payment)
			else this.showFormPayment(payment)
		},
		showFormPayment(payment) {
			this.payments.forEach((p, pi) => {
				const formPayment = this.paymentForms[pi]
				formPayment.editing = false
				if (p === payment) {
					formPayment.editing = true
					Object.assign(formPayment.value, payment)
				}
			});
		},
		hideFormPayment(payment) {
			const pi = this.payments.indexOf(payment)
			const formPayment = this.paymentForms[pi]
			formPayment.editing = false
			if (payment.id === null) {
				this.payments.splice(pi)
				this.paymentForms.splice(pi)
			}
		},
		addPayment(payment) {
			this.paymentForms.push({
				editing: true,
				value: Object.assign({}, payment)
			})
			this.payments.push(payment);
		},
		deletePayment(payment) {
			this.$refs.confirmModal
				.confirm(null, "Delete payment?")
				.then(() => axios.delete("/api/orders/" + this.orderId + "/payments/" + payment.id))
				.then(() => {
					const pi = this.payments.indexOf(payment)
					this.payments.splice(pi, 1)
					this.paymentForms.splice(pi, 1)
				})
		},
		savePayment(payment) {
			const pi = this.payments.indexOf(payment)
			const formPayment = this.paymentForms[pi]
			const request = formPayment.value.id === null ?
				axios.post("/api/orders/" + this.orderId + "/payments", formPayment.value)
				: axios.put("/api/orders/" + this.orderId + "/payments/" + formPayment.value.id, formPayment.value)
			request.then(response => response.data)
				.then(pay => {
					Object.assign(payment, pay)
					Object.assign(formPayment.value, pay)
					this.hideFormPayment(payment)
				})
		}
	}
}
