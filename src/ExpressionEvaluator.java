public class ExpressionEvaluator {

    private StackDouble values;
    private StackString operators;

    public ExpressionEvaluator() {
        values = new StackDouble();
        operators = new StackString();
    }

    private int precedence(String operator) {

        if (operator.equals("+") || operator.equals("-")) {
            return 1;
        }

        if (operator.equals("*") || operator.equals("/")) {
            return 2;
        }

        return 0;
    }

    private void applyOperation() {

        String operator = operators.pop();

        double right = values.pop();
        double left = values.pop();

        double result;

        switch (operator) {

            case "+":
                result = left + right;
                break;

            case "-":
                result = left - right;
                break;

            case "*":
                result = left * right;
                break;

            case "/":

                if (right == 0) {
                    throw new IllegalArgumentException(
                            "Cannot divide by zero."
                    );
                }

                result = left / right;
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid operator."
                );
        }

        values.push(result);
    }

    public double evaluate(String expression) {

        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty."
            );
        }

        expression = expression
                .replace("(", " ( ")
                .replace(")", " ) ");

        String[] tokens = expression.trim().split("\\s+");

        for (String token : tokens) {

            try {

                double number = Double.parseDouble(token);

                values.push(number);

                continue;

            } catch (NumberFormatException ignored) {
            }

            if (token.equals("(")) {

                operators.push(token);

            } else if (token.equals(")")) {

                while (
                        !operators.isEmpty()
                                && !operators.peek().equals("(")
                ) {

                    applyOperation();
                }

                if (operators.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Invalid expression."
                    );
                }

                operators.pop();

            } else if (
                    token.equals("+")
                            || token.equals("-")
                            || token.equals("*")
                            || token.equals("/")
            ) {

                while (
                        !operators.isEmpty()
                                && !operators.peek().equals("(")
                                && precedence(operators.peek())
                                >= precedence(token)
                ) {

                    applyOperation();
                }

                operators.push(token);

            } else {

                throw new IllegalArgumentException(
                        "Invalid value: " + token
                );
            }
        }

        while (!operators.isEmpty()) {

            if (operators.peek().equals("(")) {

                throw new IllegalArgumentException(
                        "Invalid expression."
                );
            }

            applyOperation();
        }

        if (values.isEmpty()) {

            throw new IllegalArgumentException(
                    "Invalid expression."
            );
        }

        double result = values.pop();

        if (!values.isEmpty()) {

            throw new IllegalArgumentException(
                    "Invalid expression."
            );
        }

        return result;
    }


    /*
     * Stack for decimal numbers.
     * Implemented using linked nodes.
     */
    private static class StackDouble {

        private DoubleNode top;

        public void push(double value) {

            DoubleNode node = new DoubleNode(value);

            node.next = top;

            top = node;
        }

        public double pop() {

            if (top == null) {
                throw new IllegalArgumentException(
                        "Invalid expression."
                );
            }

            double value = top.data;

            top = top.next;

            return value;
        }

        public boolean isEmpty() {

            return top == null;
        }
    }


    /*
     * Stack for operators.
     * Implemented using linked nodes.
     */
    private static class StackString {

        private StringNode top;

        public void push(String value) {

            StringNode node = new StringNode(value);

            node.next = top;

            top = node;
        }

        public String pop() {

            if (top == null) {
                return null;
            }

            String value = top.data;

            top = top.next;

            return value;
        }

        public String peek() {

            if (top == null) {
                return null;
            }

            return top.data;
        }

        public boolean isEmpty() {

            return top == null;
        }
    }


    private static class DoubleNode {

        double data;
        DoubleNode next;

        DoubleNode(double data) {
            this.data = data;
        }
    }


    private static class StringNode {

        String data;
        StringNode next;

        StringNode(String data) {
            this.data = data;
        }
    }
}