import axios from 'axios';
import DatesUtil from "../../plugin/DatesUtil.js";
import ListOrdersComponent from "./ListOrdersComponent.js";

export default {
  components: {ListOrdersComponent},
  data() {
	const [from, to] = DatesUtil.getCurrentRangeWeek()
    return {
		from:new Date(this.$route.query.from || from.toISOString()),
		to:new Date(this.$route.query.to || to.toISOString()),
		orders: []
	}
  },
  mounted() {
    const cmp = this;
    flatpickr(this.$refs.selectDate, {
      altInput: true,
      altFormat: "D d of M",
      mode: "range",
      dateFormat: "Z",
      defaultDate:[this.from,this.to],
      onChange: function (selectedDates) {
        if (selectedDates.length === 2) {
          cmp.loadOrders(selectedDates[0], selectedDates[1])
        }
      }
    })
    this.loadOrders(this.from,this.to)
  },
  methods: {
	loadOrders(dateFrom, dateTo) {
	  this.$router.replace({
		  query: {from: dateFrom.toISOString(), to: dateTo.toISOString()}
	  })
      axios
        .get('/api/orders', {params: {from: dateFrom, to: dateTo}})
        .then(response => {
          const orders = response.data;
          orders.sort((o1, o2) => o1.customerName.localeCompare(o2.customerName))
          this.orders = orders
        })
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
                <div>{{orders.length}} Order{{orders.length>1?'s':''}}</div>
            </div>
            <div>
                <button type="button" class="btn p-2">
                    <span class="material-symbols-outlined">search</span>
                </button>
            </div>
        </div>
        <div class="px-1 pb-2">
            <input ref="selectDate" type="text" class="form-control text-center py-0 mt-1">
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