
public class SearchSort {

    private SearchSort() {
        // Utility class
    }

    // =====================================================
    // 1. LINEAR SEARCH
    // Searches each order until the Order ID is found.
    // Time complexity: O(n)
    // =====================================================

    public static SearchResult linearSearch(
            Order[] orders,
            String orderId) {

        int comparisons = 0;

        if (orders == null || orderId == null) {
            return new SearchResult(null, comparisons);
        }

        for (Order order : orders) {

            if (order == null) {
                continue;
            }

            comparisons++;

            if (order.getOrderId()
                    .equalsIgnoreCase(orderId.trim())) {

                return new SearchResult(
                        order,
                        comparisons
                );
            }
        }

        return new SearchResult(null, comparisons);
    }


    // =====================================================
    // 2. BINARY SEARCH
    // The input array must be sorted by Order ID.
    // Time complexity: O(log n)
    // =====================================================

    public static SearchResult binarySearch(
            Order[] sortedOrders,
            String orderId) {

        int comparisons = 0;

        if (sortedOrders == null || orderId == null) {
            return new SearchResult(null, comparisons);
        }

        int left = 0;
        int right = sortedOrders.length - 1;

        String target = orderId.trim();

        while (left <= right) {

            int middle = left + (right - left) / 2;

            Order order = sortedOrders[middle];

            if (order == null) {
                return new SearchResult(null, comparisons);
            }

            comparisons++;

            int result = order.getOrderId()
                    .compareToIgnoreCase(target);

            if (result == 0) {
                return new SearchResult(
                        order,
                        comparisons
                );
            }

            if (result < 0) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return new SearchResult(null, comparisons);
    }


    // =====================================================
    // 3. MERGE SORT BY ORDER ID
    // Sorts a copy; original order storage is unchanged.
    // Time complexity: O(n log n)
    // =====================================================

    public static Order[] mergeSortByOrderId(
            Order[] orders) {

        if (orders == null) {
            return new Order[0];
        }

        Order[] result = orders.clone();

        if (result.length > 1) {
            mergeSortByOrderId(
                    result,
                    0,
                    result.length - 1
            );
        }

        return result;
    }


    private static void mergeSortByOrderId(
            Order[] orders,
            int left,
            int right) {

        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSortByOrderId(orders, left, middle);

        mergeSortByOrderId(
                orders,
                middle + 1,
                right
        );

        mergeByOrderId(
                orders,
                left,
                middle,
                right
        );
    }


    private static void mergeByOrderId(
            Order[] orders,
            int left,
            int middle,
            int right) {

        Order[] temporary =
                new Order[right - left + 1];

        int i = left;
        int j = middle + 1;
        int k = 0;

        while (i <= middle && j <= right) {

            int comparison =
                    orders[i].getOrderId()
                            .compareToIgnoreCase(
                                    orders[j].getOrderId()
                            );

            if (comparison <= 0) {
                temporary[k++] = orders[i++];
            } else {
                temporary[k++] = orders[j++];
            }
        }

        while (i <= middle) {
            temporary[k++] = orders[i++];
        }

        while (j <= right) {
            temporary[k++] = orders[j++];
        }

        for (int index = 0;
             index < temporary.length;
             index++) {

            orders[left + index] = temporary[index];
        }
    }


    // =====================================================
    // 4. MERGE SORT BY TOTAL AMOUNT
    // Highest order total appears first.
    // Time complexity: O(n log n)
    // =====================================================

    public static Order[] mergeSortByTotal(
            Order[] orders) {

        if (orders == null) {
            return new Order[0];
        }

        Order[] result = orders.clone();

        if (result.length > 1) {
            mergeSortByTotal(
                    result,
                    0,
                    result.length - 1
            );
        }

        return result;
    }


    private static void mergeSortByTotal(
            Order[] orders,
            int left,
            int right) {

        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSortByTotal(orders, left, middle);

        mergeSortByTotal(
                orders,
                middle + 1,
                right
        );

        mergeByTotal(
                orders,
                left,
                middle,
                right
        );
    }


    private static void mergeByTotal(
            Order[] orders,
            int left,
            int middle,
            int right) {

        Order[] temporary =
                new Order[right - left + 1];

        int i = left;
        int j = middle + 1;
        int k = 0;

        while (i <= middle && j <= right) {

            if (orders[i].getTotalAmount()
                    >= orders[j].getTotalAmount()) {

                temporary[k++] = orders[i++];
            } else {
                temporary[k++] = orders[j++];
            }
        }

        while (i <= middle) {
            temporary[k++] = orders[i++];
        }

        while (j <= right) {
            temporary[k++] = orders[j++];
        }

        for (int index = 0;
             index < temporary.length;
             index++) {

            orders[left + index] = temporary[index];
        }
    }


    // =====================================================
    // SEARCH RESULT
    // Stores the matching order and comparisons performed.
    // =====================================================

    public static class SearchResult {

        private final Order order;
        private final int comparisons;

        public SearchResult(
                Order order,
                int comparisons) {

            this.order = order;
            this.comparisons = comparisons;
        }

        public Order getOrder() {
            return order;
        }

        public int getComparisons() {
            return comparisons;
        }

        public boolean isFound() {
            return order != null;
        }
    }
}
