export default {
  props: ['orders'],
  template: `
    <div class="list-group">
        <div @click="$emit('select', order)" class="list-group-item list-group-item-action" v-for="order in orders" :key="order.id">
            <div class="d-flex justify-content-between mb-2">
                <div class="d-flex gap-1 align-items-center">
                    <h5 class="m-0">{{ order.customerName }}</h5>
                </div>
                <div class="d-flex gap-1 align-items-center">
                    <span v-if="!order.delivered" class="text-danger fst-italic pe-1" style="font-size: 80%">&lt;not delivered&gt;</span>
                    <span v-else class="text-success fst-italic pe-1" style="font-size: 80%">delivered</span>
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
                    <span v-else class="text-success fst-italic">paid</span>
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
`
}