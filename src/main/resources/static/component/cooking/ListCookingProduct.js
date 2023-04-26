import axios from 'axios';
import DatesUtil from "../../plugin/DatesUtil.js";

export default {
    data() {
        return {
            products: []
        }
    },
    mounted() {
        const cmp = this;
        flatpickr(this.$refs.selectDate, {
            altInput: true,
            altFormat: "D d of M",
            mode: "range",
            dateFormat: "Z",
            onChange: function (selectedDates) {
                if (selectedDates.length === 2) {
                    cmp.loadProducts(selectedDates[0], selectedDates[1])
                }
            }
        })
            .setDate(DatesUtil.getCurrentRangeWeek(), true);
    },
    methods: {
        loadProducts(dateFrom, dateTo) {
            axios
                .get('/api/cooking/products/', { params: { from: dateFrom, to: dateTo } })
                .then(response => this.products = response.data)
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
                <div>Cooking Products</div>
            </div>
            <div style="visibility: hidden">
                <button type="button" class="btn p-2">
                    <span class="material-symbols-outlined">chevron_left</span>
                </button>
            </div>
        </div>
        <div class="px-1 pb-2">
            <input ref="selectDate" type="text" class="form-control text-center py-0 mt-1">
        </div>
    </div>

<div class="container px-1">
    <div class="list-group">
        <div class="list-group-item" v-for="product in products" :key="product.id">
            <div class="d-flex justify-content-between mb-2">
                <div class="d-flex gap-1 align-items-center">
                    <h5 class="m-0">{{ order.customerName }}</h5>
                </div>
                <div class="d-flex gap-1 align-items-center">
                    <span v-if="!order.delivered" class="text-danger fst-italic pe-1" style="font-size: 80%">&lt;not delivered&gt;</span>
                    #{{ order.id }}
                </div>
            </div>
            
            <div class="text-body-secondary d-flex flex-column " style="font-size: 80%">
            
                <div class="d-flex justify-content-between">
                <div class="d-flex gap-1 align-items-center">
                    <span class="material-symbols-outlined">deployed_code</span>
                    <span v-if="order.totalItems==0" class="text-danger fst-italic">&lt;not items&gt;</span>
                    <span v-else>{{ order.totalItems }} item{{order.totalItems>1?'s':''}}</span> 
                    <span>- €{{ order.totalPrice }}</span>
                    <span v-if="!order.paid" class="text-danger fst-italic">&lt;not paid&gt;</span>
                </div>
                <div class="d-flex gap-1 align-items-center">
                        <span class="material-symbols-outlined ">call</span>
                        <span v-if="order.customerPhone==null" class="text-danger fst-italic pe-1">&lt;not phone contact&gt;</span>
                        <span v-else>{{order.customerPhone}}</span>
                </div>
            </div>
            
                <div class="d-flex justify-content-between">
                  <div class="d-flex gap-1 align-items-center">
                    <span class="material-symbols-outlined">pin_drop</span>
                    <small v-if="order.deliveryAddress!=null">{{ order.deliveryAddress }}</small>
                    <span v-else class="text-danger fst-italic">&lt;no delivery address&gt;</span>
                  </div>
                  <div class="d-flex gap-1 align-items-center">
                      <span class="material-symbols-outlined">directions_bike</span>
                      <span v-if="order.deliveryRiderName==null" class="text-danger fst-italic">&lt;no rider&gt;</span>
                      <span v-else>{{order.deliveryRiderName}}</span>
                  </div>
              </div>
            
            </div>
        </div>
    </div>
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