import axios from 'axios';
import PromptModal from "../PromptModal.js";
import ConfirmModal from "../ConfirmModal.js";

export default {
  components: {PromptModal, ConfirmModal},
  data() {
    return {
      riders: [
        {id: 1, name: "Cristian"},
        {id: 2, name: "Michi"}
      ],
      saving: false,
      deliveryDatePicker: null,
      deliveryDateFromPicker: null,
      deliveryDateRangePicker: null,
      order: {
        customerName: null,
        customerPhone: null,
        note: null,
        deliveryAddress: null,
        deliveryRiderId: null,
        deliveryPrice: 1.5,
        discount: 0,
        items: []
      }
    }
  },
  computed: {
    orderId() {
      return this.$route.params.orderId;
    },
    computedTotalPrice() {
      const totalItems = this.order.items.reduce((acc, item) => acc + item.productPrice * item.quantity, 0)
      return Math.max(0, totalItems + this.order.deliveryPrice - this.order.discount)
    }
  },
  mounted() {
    this.deliveryDatePicker = flatpickr(this.$refs.deliveryDatePicker, {
      altInput: true,
      altFormat: "D d of M, Y",
      dateFormat: "Z",
      allowInput: true
    });
    this.deliveryDateFromPicker = flatpickr(this.$refs.deliveryDateFromPicker, {
      altInput: true,
      enableTime: true,
      noCalendar: true,
      time_24hr: true,
      altFormat: "H:i",
      dateFormat: "Z",
      allowInput: true
    });
    this.deliveryDateToPicker = flatpickr(this.$refs.deliveryDateToPicker, {
      altInput: true,
      enableTime: true,
      noCalendar: true,
      time_24hr: true,
      altFormat: "H:i",
      dateFormat: "Z",
      allowInput: true
    });
    const productsRequest = axios.get('/api/orders/products')
      .then(response => response.data)
      .then(products => {
        this.order.items = products
          .sort((p1, p2) => p1.name.localeCompare(p2.name))
          .map(product => {
            return {
              productId: product.id,
              productName: product.name,
              productPrice: product.price,
              quantity: 0,
              note: ""
            }
          })
      })
    if (this.orderId !== undefined) {
      axios.all([
        axios.get('/api/orders/' + this.orderId).then(response => response.data),
        productsRequest
      ]).then(([order]) => this.setOrder(order))
    }
  },
  methods: {
    setOrder(order) {
      this.$el.querySelectorAll(".is-invalid").forEach(el => el.classList.remove("is-invalid"))
      this.order.items.forEach(item => {
        item.quantity = 0
        item.note = ""
        const newItem = order.items.find(newItem => newItem.productId === item.productId)
        if (newItem) {
          item.quantity = newItem.quantity
          item.note = newItem.note
        }
      })
      order.items = this.order.items
      this.order = order
      this.deliveryDatePicker.setDate(order.deliveryDate, true)
      this.deliveryDateFromPicker.setDate(order.deliveryDate, true)
      this.deliveryDateToPicker.setDate(order.deliveryDateRange, true)
    },
    openItemNoteModal(item) {
      this.$refs.promptModal
        .prompt(item.productName, item.note)
        .then(value => item.note = value)
        .catch(() => {
        })
    },
    saveOrder() {
      this.saving = true
      this.$el.querySelectorAll(":invalid").forEach(el => el.classList.add("is-invalid"))
      const form = this.$refs.formOrder
      if (!form.checkValidity()) return;
      const order = Object.assign({}, this.order)
      order.items = order.items
        .filter(item => item.quantity > 0)
        .map(item => {
          return {
            productId: item.productId,
            quantity: item.quantity,
            note: item.note,
          }
        })
      const deliveryDate = this.deliveryDatePicker.selectedDates[0]
      const deliveryDateFrom = this.deliveryDateFromPicker.selectedDates[0]
      const deliveryDateTo = this.deliveryDateToPicker.selectedDates[0]
      if (deliveryDate) {
        order.deliveryDate = deliveryDate.toISOString()
        if (deliveryDateFrom) {
          deliveryDate.setHours(deliveryDateFrom.getHours(), deliveryDateFrom.getMinutes())
          order.deliveryDate = deliveryDate.toISOString()
        }
        if (deliveryDateTo) {
          const range = new Date(deliveryDate.getTime())
          range.setHours(deliveryDateTo.getHours(), deliveryDateTo.getMinutes())
          order.deliveryDateRange = range.toISOString()
        }
      }
      if (this.orderId === undefined) {
        axios.post("/api/orders", order)
          .then(response => {
            this.setOrder(response.data)
            this.$router.replace({path: '/receive/orders/' + response.data.id})
          })
          .then(() => this.saving = false)
      } else {
        axios.put("/api/orders/" + this.orderId, order)
          .then(response => this.setOrder(response.data))
          .then(() => this.saving = false)
      }
    },
    deleteOrder() {
      this.$refs.confirmModal
        .confirm(null, "Delete order?")
        .then(() => axios.delete("/api/orders/" + this.orderId))
        .then(() => this.$router.push({path: '/receive/orders', replace: true}))
        .catch(() => {
        })
    }
  },
  template: `
<div>
    <div class="sticky-top bg-dark text-white">
        <div class="d-flex justify-content-between">
            <div>
                <button type="button" class="btn p-2">
                    <router-link class="list-group-item list-group-item-action" to="/receive/orders">
                        <span class="material-symbols-outlined text-white">chevron_left</span>
                    </router-link>
                </button>
            </div>
            <div class="text-center my-2 flex-fill">
                Order #1
            </div>
            <button @click="deleteOrder" :style="{visibility:orderId===undefined?'hidden':'visible'}" type="button" class="btn p-2">
                <span class="material-symbols-outlined text-white">delete</span>
            </button>
        </div>
    </div>

    <form ref="formOrder" @submit.prevent="saveOrder" novalidate>
        <div class="container d-grid gap-2 py-3">
            <label for="customerName" class="form-label">Name</label>
            <input v-model="order.customerName" type="text" required class="form-control" id="customerName">

            <label for="customerPhone" class="form-label">Phone</label>
            <input v-model="order.customerPhone" type="tel" class="form-control" id="customerPhone">

            <label class="form-label">Menu</label>
            <div class="list-group">
                <div class="list-group-item" v-for="item in order.items" :key="item.productId">
                    <div class="d-flex gap-1">
                        <div class="flex-fill">{{ item.productName }}</div>
                        <div>{{ $formatCurrency(item.productPrice) }}</div>
                    </div>
                    <div class="d-flex w-100 gap-2 align-items-center">
                        <small class="text-body-secondary flex-fill">
                            <div v-if="item.quantity>0" @click="openItemNoteModal(item)">
                                <span :class="{'fst-italic':item.note==''}">{{ item.note=='' ? '&lt;add a note&gt;': item.note }}</span>
                                <span class="material-symbols-outlined" style="font-size: 90%">edit</span>
                            </div>
                        </small>
                        <div class="d-flex gap-1 text-body-secondary">
                            <span @click="item.quantity= Math.max(0,item.quantity-1)" class="material-symbols-outlined">do_not_disturb_on</span>
                            <small :class="{'text-primary': item.quantity>0 }">x{{ item.quantity }}</small>
                            <span @click="item.quantity++" class="material-symbols-outlined">add_circle</span>
                        </div>
                    </div>
                </div>
            </div>

            <hr/>

            <div class="d-grid gap-2">

                <label for="deliveryDate" class="form-label">Delivery Date</label>
                <input ref="deliveryDatePicker" type="text" class="form-control" id="deliveryDate" required>
                <div class="row align-items-center">
                    <div class="col-2">
                        <label for="deliveryDateFrom" class="form-label">From</label>
                    </div>
                    <div class="col-4">
                        <input ref="deliveryDateFromPicker" type="text" class="form-control" id="deliveryDateFrom" required>
                    </div>
                    <div class="col-2">
                        <label for="deliveryDateTo" class="form-label">To</label>
                    </div>
                    <div class="col-4">
                        <input ref="deliveryDateToPicker" type="text" class="form-control" id="deliveryDateTo">
                    </div>
                </div>

                <div class="row align-items-center">
                    <div class="col-7">
                        <label for="deliveryAddress" class="form-label">Delivery Address</label>
                    </div>
                    <div class="col-5">
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input v-model.number="order.deliveryPrice" type="number" step="any" class="form-control text-end"
                                   id="deliveryPrice">
                        </div>
                    </div>
                </div>
                <input v-model="order.deliveryAddress" type="text" class="form-control" id="deliveryAddress">

                <label for="deliveryRiderId" class="form-label">Delivery Rider</label>
                <select v-model="order.deliveryRiderId" id="deliveryRiderId" class="form-select">
                    <option :value="null">Select a rider</option>
                    <option v-for="rider in riders" :value="rider.id">
                        {{ rider.name }}
                    </option>
                </select>
            </div>

            <hr/>

            <div class="row align-items-center">
                <div class="col-7">
                    <label for="discount" class="form-label">Discount</label>
                </div>
                <div class="col-5">
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <input v-model.number="order.discount" type="number" step="any" class="form-control text-end"
                               id="discount">
                    </div>
                </div>
            </div>

            <label for="note" class="form-label">note</label>
            <textarea v-model="order.note" class="form-control" id="note" rows="3"></textarea>

            <br/>
            <br/>
            <br/>
        </div>

        <div class="fixed-bottom p-2">
            <button type="submit" class="btn btn-primary w-100" :disabled="saving">
                {{orderId===undefined ? 'Place Order' : 'Update Order'}} {{ $formatCurrency(computedTotalPrice) }}
            </button>
        </div>
    </form>

    <PromptModal ref="promptModal"/>
    <ConfirmModal ref="confirmModal"/>
</div>
`
}