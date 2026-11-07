import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 4 - Dynamic Programming.
 * Solves the 0/1 Knapsack problem using a bottom-up DP table.
 */
public class DynamicProgramming {

    public static class Item {
        private final String name;
        private final int weight;
        private final int value;

        public Item(String name, int weight, int value) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Item name is required.");
            }
            if (weight <= 0) {
                throw new IllegalArgumentException("Weight must be greater than zero.");
            }
            if (value < 0) {
                throw new IllegalArgumentException("Value cannot be negative.");
            }
            this.name = name.trim();
            this.weight = weight;
            this.value = value;
        }

        public String getName() { return name; }
        public int getWeight() { return weight; }
        public int getValue() { return value; }
    }

    public static class Result {
        private final List<Item> selectedItems;
        private final int totalWeight;
        private final int totalValue;

        private Result(List<Item> selectedItems, int totalWeight, int totalValue) {
            this.selectedItems = selectedItems;
            this.totalWeight = totalWeight;
            this.totalValue = totalValue;
        }

        public List<Item> getSelectedItems() { return selectedItems; }
        public int getTotalWeight() { return totalWeight; }
        public int getTotalValue() { return totalValue; }
    }

    public static Result solveKnapsack(List<Item> items, int capacity) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Provide at least one item.");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }

        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);
            for (int c = 0; c <= capacity; c++) {
                dp[i][c] = dp[i - 1][c];
                if (item.getWeight() <= c) {
                    int include = item.getValue()
                            + dp[i - 1][c - item.getWeight()];
                    if (include > dp[i][c]) {
                        dp[i][c] = include;
                    }
                }
            }
        }

        List<Item> selected = new ArrayList<>();
        int remainingCapacity = capacity;
        int totalWeight = 0;

        for (int i = n; i > 0; i--) {
            if (dp[i][remainingCapacity] != dp[i - 1][remainingCapacity]) {
                Item item = items.get(i - 1);
                selected.add(item);
                totalWeight += item.getWeight();
                remainingCapacity -= item.getWeight();
            }
        }

        Collections.reverse(selected);
        return new Result(selected, totalWeight, dp[n][capacity]);
    }
}
