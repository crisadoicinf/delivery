export default {
  data() {
    return {
      title: "",
      value: "",
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
    prompt(title, value) {
      this.title = title
      this.value = value
      this.modal.show();
      return new Promise((resolve, reject) => {
        this.resolve = resolve
        this.reject = reject
      })
    }
  },
  template: `
<div class="modal" data-bs-backdrop="static" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <form @submit.prevent="resolve(value)">
                <div class="modal-body">
                    <label for="promptValue" class="form-label">{{ title }}</label>
                    <input v-model="value" autofocus type="text" class="form-control" id="promptValue">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn" data-bs-dismiss="modal" @click="reject">CLOSE</button>
                    <button type="submit" class="btn text-primary" data-bs-dismiss="modal">ACCEPT</button>
                </div>
            </form>
        </div>
    </div>
</div>
`
}