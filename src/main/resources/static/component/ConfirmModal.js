export default {
  data() {
    return {
      title: "",
      message: "",
      resolve: () => {
      },
      reject: () => {
      }
    }
  },
  mounted() {
    this.modal = new bootstrap.Modal(this.$el);
  },
  methods: {
    confirm(title, message) {
      this.title = title
      this.message = message
      this.modal.show();
      return new Promise((resolve, reject) => {
        this.resolve = resolve
        this.reject = reject
      })
    }
  },
  template: `
<div class="modal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <form @submit.prevent="resolve(value)">
              <div v-if="title!=null" class="modal-header">
                <h5 class="modal-title">{{title}}</h5>
              </div>
                <div class="modal-body">
                    {{ message }}
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn" data-bs-dismiss="modal" @click="reject">CANCEL</button>
                    <button type="submit" class="btn text-primary" data-bs-dismiss="modal">ACCEPT</button>
                </div>
            </form>
        </div>
    </div>
</div>
`
}