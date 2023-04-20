
export function formatPhoneNumber(input) {
	const digits = input.replace(/\D/g, '');
	const [phone, countryCode, areaCode, line] = digits.match(/^(\d{1,3})(\d{1,3})?(\d+)?/)
	let formattedNumber = `+${countryCode}`;
	formattedNumber += areaCode ? ` ${areaCode}` : "";
	formattedNumber += line ? ` ${line}` : "";
	return formattedNumber
}

export function formatCurrency(value) {
	return new Intl.NumberFormat('en-UK', {
		style: 'currency',
		currency: 'EUR'
	}).format(value);
}


export default {
	install: (app, options) => {
			app.config.globalProperties.$formatCurrency = formatCurrency,
			app.config.globalProperties.$formatPhoneNumber = formatPhoneNumber,
			app.config.globalProperties.$handleCursorInput = function handleCursorInput(e) {
				const position = e.target.selectionStart
				if (position !== e.target.value.length) {
					setTimeout(() => {
						e.target.setSelectionRange(position, position)
					}, 0)
				}
			}
	}
}
