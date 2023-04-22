import axios from 'axios';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	data() {
		return {
			title: "",
			message: "",
			resolve: () => {
			},
			reject: () => {
			}
		}
	},
	mounted() {
		this.modal = new bootstrap.Modal(this.$el);
	},
	methods: {
		confirm(title, message) {
			this.title = title
			this.message = message
			this.modal.show();
			return new Promise((resolve, reject) => {
				this.resolve = resolve
				this.reject = reject
			})
		}
	},
	template: template
}
