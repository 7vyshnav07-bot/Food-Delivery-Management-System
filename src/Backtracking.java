import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Backtracking solver for assigning each delivery order a unique allowed slot. */
public final class Backtracking {
    private Backtracking() { }

    public static final class OrderSlotRequest {
        private final String orderName;
        private final List<String> allowedSlots;

        public OrderSlotRequest(String orderName, List<String> allowedSlots) {
            this.orderName = orderName;
            this.allowedSlots = new ArrayList<>(allowedSlots);
        }
        public String getOrderName() { return orderName; }
        public List<String> getAllowedSlots() { return new ArrayList<>(allowedSlots); }
    }

    public static final class Assignment {
        private final String orderName;
        private final String slot;

        public Assignment(String orderName, String slot) {
            this.orderName = orderName;
            this.slot = slot;
        }
        public String getOrderName() { return orderName; }
        public String getSlot() { return slot; }
    }

    /** Returns a complete conflict-free assignment, or null if none exists. */
    public static List<Assignment> findAssignment(List<OrderSlotRequest> orders) {
        List<Assignment> current = new ArrayList<>();
        Set<String> usedSlots = new HashSet<>();
        if (assignNext(orders, 0, usedSlots, current)) return current;
        return null;
    }

    private static boolean assignNext(List<OrderSlotRequest> orders, int index,
                                      Set<String> usedSlots, List<Assignment> current) {
        if (index == orders.size()) return true;
        OrderSlotRequest order = orders.get(index);
        for (String slot : order.getAllowedSlots()) {
            String key = slot.toLowerCase(java.util.Locale.ROOT);
            if (usedSlots.contains(key)) continue;
            usedSlots.add(key);
            current.add(new Assignment(order.getOrderName(), slot));
            if (assignNext(orders, index + 1, usedSlots, current)) return true;
            current.remove(current.size() - 1);
            usedSlots.remove(key);
        }
        return false;
    }
}
