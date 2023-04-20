import axios from 'axios';
import ListOrdersComponent from "../orders/ListOrdersComponent.js";

export default {
  components: {ListOrdersComponent},
  data() {
    return {orders: []}
  },
  mounted() {
    const cmp = this;
    flatpickr(this.$refs.selectDate, {
      altInput: true,
      altFormat: "D d of M",
      dateFormat: "Z",
      onChange: function (selectedDates) {
        cmp.loadOrders(selectedDates[0])
      }
    })
      .setDate(new Date(), true);
  },
  methods: {
    loadOrders(date) {
      axios
        .get('/api/delivery/orders', {params: {date: date}})
        .then(response => this.orders = response.data)
    },
    viewOrder(order) {
      console.log(order)
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