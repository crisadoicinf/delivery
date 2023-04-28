import axios from 'axios';

const template = await axios.get(import.meta.url.replace(/\.js$/, ".html"))
    .then(response => response.data)

export default {
    template: template,
    data() {
        return {
            phone: "",
            text: "",
            modal: null,
            messages: [
                'hola', 'llegue', 'voy en camino', 'si', 'ok', 'esta bien'
            ]
        }
    },
    mounted() {
        this.modal = new bootstrap.Modal(this.$el);
    },
    computed: {
        chatLink() {
            return 'https://api.whatsapp.com/send/?'
                + 'phone=' + encodeURIComponent(this.phone.replace('+', ''))
                + '&text=' + encodeURIComponent(this.text)
        }
    },
    methods: {
        show(phone) {
            this.phone = phone
            this.modal.show();
        }
    }
}