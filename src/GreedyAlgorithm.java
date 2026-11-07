
import java.util.ArrayList;
import java.util.List;

public class GreedyAlgorithm {

    public static class DeliveryOption {
        private final String name;
        private final double distance;

        public DeliveryOption(String name, double distance) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Location name is required."
                );
            }

            if (!Double.isFinite(distance) || distance < 0) {
                throw new IllegalArgumentException(
                    "Distance must be a non-negative number."
                );
            }

            this.name = name;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public double getDistance() {
            return distance;
        }
    }

    public static DeliveryOption selectNearest(
            List<DeliveryOption> options) {

        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException(
                "At least one delivery option is required."
            );
        }

        DeliveryOption nearest = options.get(0);

        for (int i = 1; i < options.size(); i++) {
            DeliveryOption current = options.get(i);

            if (current.getDistance() < nearest.getDistance()) {
                nearest = current;
            }
        }

        return nearest;
    }
}
