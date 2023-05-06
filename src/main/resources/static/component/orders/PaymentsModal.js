import axios from 'axios';
import ConfirmModal from '../ConfirmModal.js';
import PaymentsList from './PaymentsList.js';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	template: template,
	components: { ConfirmModal, PaymentsList },
	props: ['orderId', 'totalAmount'],
	data() {
		return { payments: [] }
	},
	computed: {
		totalPaid() {
			return this.payments.reduce((ac, cu) => ac + (cu.id === null ? 0 : cu.amount), 0)
		},
		remainAmount() {
			return Math.max(0, this.totalAmount - this.totalPaid)
		},
		isAddingPayment() {
			return this.payments.some(payment => payment.id === null)
		}
	},
	mounted() {
		this.modal = new bootstrap.Modal(this.$refs.modal);
		axios.get('/api/orders/' + this.orderId + '/payments')
			.then(response => response.data)
			.then(payments => {
				this.payments = payments.sort((p1, p2) => new Date(p1.date) - new Date(p2.date))
				this.$refs.paymentsList.setPayments(this.payments)
			})
	},
	methods: {
		show() {
			this.modal.show();
		},
		hide() {
			this.modal.hide();
		},
		addPayment() {
			this.$refs.paymentsList.addPayment({
				id: null,
				amount: this.remainAmount,
				type: "cash",
				recipientName: "",
				recipientType: ""
			});
		}
	}
}
