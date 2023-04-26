export default {
	mounted(el, binding) {
		el._flatpickr = flatpickr(el, Object.assign({}, binding.value))
	},
	updated(el) {
		el._flatpickr.setDate(el.value, false)
	}
}