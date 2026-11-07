public class LinkedList {

    private Node head;

    public LinkedList() {
        head = null;
    }

    public void insert(Order order) {

        Node newNode = new Node(order);

        if (head == null) {
            head = newNode;
            return;
        }

        Node current = head;

        while (current.next != null) {
            current = current.next;
        }

        current.next = newNode;
    }

    public Order delete(String orderId) {

        if (head == null) {
            return null;
        }

        Order firstOrder = (Order) head.data;

        if (firstOrder.getOrderId().equalsIgnoreCase(orderId)) {

            head = head.next;

            return firstOrder;
        }

        Node current = head;

        while (current.next != null) {

            Order order = (Order) current.next.data;

            if (order.getOrderId().equalsIgnoreCase(orderId)) {

                Order deletedOrder = order;

                current.next = current.next.next;

                return deletedOrder;
            }

            current = current.next;
        }

        return null;
    }

    public Order search(String orderId) {

        Node current = head;

        while (current != null) {

            Order order = (Order) current.data;

            if (order.getOrderId().equalsIgnoreCase(orderId)) {
                return order;
            }

            current = current.next;
        }

        return null;
    }

    public Order[] traverse() {

        int size = size();

        Order[] orders = new Order[size];

        Node current = head;

        int index = 0;

        while (current != null) {

            orders[index] = (Order) current.data;

            index++;

            current = current.next;
        }

        return orders;
    }

    public int size() {

        int count = 0;

        Node current = head;

        while (current != null) {

            count++;

            current = current.next;
        }

        return count;
    }
}