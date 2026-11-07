public class Queue {

    private Node front;
    private Node rear;
    private int count;

    public Queue() {

        front = null;
        rear = null;
        count = 0;
    }

    public void enqueue(Order order) {

        Node newNode = new Node(order);

        if (rear == null) {

            front = newNode;
            rear = newNode;

        } else {

            rear.next = newNode;
            rear = newNode;
        }

        count++;
    }

    public Order dequeue() {

        if (front == null) {
            return null;
        }

        Order order = (Order) front.data;

        front = front.next;

        if (front == null) {
            rear = null;
        }

        count--;

        return order;
    }

    public Order peek() {

        if (front == null) {
            return null;
        }

        return (Order) front.data;
    }

    public boolean isEmpty() {

        return front == null;
    }

    public int size() {

        return count;
    }

    public Order[] toArray() {

        Order[] orders = new Order[count];

        Node current = front;

        int index = 0;

        while (current != null) {

            orders[index] = (Order) current.data;

            index++;

            current = current.next;
        }

        return orders;
    }
}