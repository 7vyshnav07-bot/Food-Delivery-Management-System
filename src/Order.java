public class Order {

    private String orderId;
    private String customerName;
    private String phone;
    private String restaurant;
    private String foodItem;
    private int quantity;
    private double foodPrice;
    private double deliveryCharge;
    private String address;
    private double distance;
    private String paymentMethod;
    private String status;
    private boolean rescueOrder;
    private double customerSavings;

    public Order(
            String orderId,
            String customerName,
            String phone,
            String restaurant,
            String foodItem,
            int quantity,
            double foodPrice,
            double deliveryCharge,
            String address,
            double distance,
            String paymentMethod) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.phone = phone;
        this.restaurant = restaurant;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.foodPrice = foodPrice;
        this.deliveryCharge = deliveryCharge;
        this.address = address;
        this.distance = distance;
        this.paymentMethod = paymentMethod;
        this.status = "Pending";
        this.rescueOrder = false;
        this.customerSavings = 0;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance() {
        return distance;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public boolean isRescueOrder() { return rescueOrder; }
    public double getCustomerSavings() { return customerSavings; }
    public void markRescueOrder(double savings) {
        this.rescueOrder = true;
        this.customerSavings = savings;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return (quantity * foodPrice) + deliveryCharge;
    }
}
