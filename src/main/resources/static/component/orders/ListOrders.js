import axios from 'axios';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
	.then(response => response.data)

export default {
	props: ['orders'],
	template: template
}
