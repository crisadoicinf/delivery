export default {
  install: (app, options) => {
    app.config.globalProperties.$formatCurrency = (value) => {
      return new Intl.NumberFormat('en-UK', {
        style: 'currency',
        currency: 'EUR'
      }).format(value);
    }
  }
}