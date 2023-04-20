export default {
  getCurrentRangeWeek: () => {
    const currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0)
    const currentDay = currentDate.getDay();
    const monday = new Date(currentDate);
    monday.setDate(currentDate.getDate() - currentDay + 1);
    const sunday = new Date(currentDate);
    sunday.setDate(currentDate.getDate() - currentDay + 7);
    return [monday, sunday];
  },
  getCurrentDeliveryDate: () => {
    const currentDate = new Date();
    currentDate.setHours(14, 0, 0, 0)
    const currentDay = currentDate.getDay();
    const saturday = new Date(currentDate);
    saturday.setDate(currentDate.getDate() - currentDay + 6);
    return saturday;
  }
}