import axios from 'axios';
import ListOrdersComponent from "../orders/ListOrdersComponent.js";

export default {
  components: {ListOrdersComponent},
  data() {
	const today = new Date()
	today.setHours(0,0,0,0)  
    return {
		date: new Date(this.$route.query.date || today.toISOString()),
		rider: this.$route.query.rider,
      	riders: [],
		orders: []
	}
  },
  mounted() {
    flatpickr(this.$refs.selectDate, {
      altInput: true,
      altFormat: "D d of M",
      dateFormat: "Z",
      defaultDate: this.date,
      onChange:  (selectedDates) =>{
		this.date = selectedDates[0]
        this.loadOrders()
      }
    })
    axios.get('/api/delivery/riders')
      .then(response => {
		  this.riders = response.data
		  this.rider = this.rider || this.riders[0].id
		})
      .then(()=> this.loadOrders())
  },
  methods: {
    loadOrders() {
		const date = this.date
		const riderId = this.rider
	  	this.$router.replace({query: {date: date.toISOString(), riderId: riderId}})
      	axios
        	.get('/api/delivery/orders', {params: {date: date, riderId: riderId}})
        	.then(response => this.orders = response.data)
    },
    viewOrder(order) {
      this.$router.push({path: '/receive/orders/' + order.id})
    }
  },
  template: `
<div>
    <div class="fixed">
        <div class="d-flex justify-content-between">
            <div>
                <button type="button" class="btn p-2">
                    <router-link class="list-group-item list-group-item-action" to="/">
                        <span class="material-symbols-outlined">chevron_left</span>
                    </router-link>
                </button>
            </div>
            <div class="text-center my-2 flex-fill">
                <div>Deliver {{orders.length}} Order{{orders.length>1?'s':''}}</div>
            </div>
            <div>
                <button type="button" class="btn p-2">
                    <span class="material-symbols-outlined">search</span>
                </button>
            </div>
        </div>
        <div class="d-flex mx-1 my-2">
            <input ref="selectDate" type="text" class="form-control py-0 text-center w-50">
            <select v-model="rider" @update:modelValue="loadOrders" class="form-select py-0 w-50">
                    <option v-for="rider in riders" :value="rider.id">
                        {{ rider.name }}
                    </option>
            </select>
        </div>
    </div>

<div class="container px-1">
    <ListOrdersComponent :orders="orders" @select="viewOrder"/>
    <br/>
    <br/>
</div>
    <div class="fixed-bottom p-2">
        <router-link to="/receive/orders/new" class="btn btn-primary w-100">
            <span class="material-symbols-outlined">add</span> Add order
        </router-link>
    </div>

</div>
`
}