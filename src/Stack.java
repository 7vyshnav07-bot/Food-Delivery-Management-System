
public class Stack {

    private Node top;
    private int count;

    public Stack() {
        top = null;
        count = 0;
    }

    public void push(Order order) {

        Node newNode = new Node(order);

        newNode.next = top;

        top = newNode;

        count++;
    }

    public Order pop() {

        if (top == null) {
            return null;
        }

        Order order = (Order) top.data;

        top = top.next;

        count--;

        return order;
    }

    public Order peek() {

        if (top == null) {
            return null;
        }

        return (Order) top.data;
    }

    public boolean isEmpty() {

        return top == null;
    }

    public int size() {

        return count;
    }

    public Order[] toArray() {

        Order[] orders = new Order[count];

        Node current = top;

        int index = 0;

        while (current != null) {

            orders[index] = (Order) current.data;

            index++;

            current = current.next;
        }

        return orders;
    }
}