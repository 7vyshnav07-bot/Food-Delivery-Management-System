import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Main {

    // =========================================================
    // PHASE 1 DATA STRUCTURES
    // =========================================================

    // Linked List - stores active orders
    private static final LinkedList orders =
            new LinkedList();

    // Queue - stores pending orders in FIFO order
    private static final Queue pendingOrders =
            new Queue();

    // Stack - stores cancelled orders
    private static final Stack cancelledOrders =
            new Stack();

    // Expression evaluator - stack-based expression handling
    private static final ExpressionEvaluator evaluator =
            new ExpressionEvaluator();


    // =========================================================
    // PHASE 2 DATA STRUCTURES
    // =========================================================

    // Binary Search Tree - fast order search
    private static final BinaryTree orderTree =
            new BinaryTree();

    // Graph - restaurant and delivery-area network
    private static final Graph deliveryGraph =
            new Graph();

    private static final Map<String, RescueDeal> rescueDeals = new LinkedHashMap<>();
    private static int nextRescueDealId = 1;
    private static class RescueDeal {
        String id, shop, item, deadline;
        double originalPrice, discountedPrice;
        int quantity;
        boolean active = true;
    }


    // =========================================================
    // SERVER
    // =========================================================

    private static final int PORT = 8080;


    // =========================================================
    // MAIN METHOD
    // =========================================================

    public static void main(String[] args) throws Exception {

        loadSampleOrders();

        initializeGraph();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT),
                0
        );

        server.createContext(
                "/",
                Main::handleRequest
        );

        server.setExecutor(null);

        System.out.println();
        System.out.println("======================================");
        System.out.println("          FOODFLOW IS RUNNING");
        System.out.println("======================================");
        System.out.println();

        System.out.println(
                "Open: http://localhost:" + PORT
        );

        System.out.println();

        System.out.println("Phase 1:");
        System.out.println("Linked List");
        System.out.println("Stack");
        System.out.println("Queue");
        System.out.println("Expression Evaluation");

        System.out.println();

        System.out.println("Phase 2:");
        System.out.println("Binary Search Tree");
        System.out.println("Inorder Traversal");
        System.out.println("Preorder Traversal");
        System.out.println("Postorder Traversal");
        System.out.println("BST Search");
        System.out.println("Graph - Adjacency List");
        System.out.println("Graph - BFS");

        System.out.println();

        System.out.println(
                "Press Ctrl + C to stop the server."
        );

        System.out.println();

        server.start();
    }


    // =========================================================
    // REQUEST HANDLER
    // =========================================================

    private static void handleRequest(
            HttpExchange exchange)
            throws IOException {

        String method =
                exchange.getRequestMethod();

        String path =
                exchange.getRequestURI().getPath();

        if (path.equals("/")) {
    path = "/login.html";
}

        // =====================================================
        // PHASE 1 API ROUTES
        // =====================================================
// =====================================================
// ADMIN LOGIN
// =====================================================

if (path.equals("/api/login")) {

    if (!method.equalsIgnoreCase("POST")) {

        sendJson(
                exchange,
                "{\"success\":false,\"message\":\"POST required\"}"
        );

        return;
    }


    String body =
            readBody(exchange);


    Map<String, String> data =
            parseFormData(body);


    String username =
            data.get("username");


    String password =
            data.get("password");


    // -------------------------------------------------
    // ADMIN CREDENTIALS
    // -------------------------------------------------

    String ADMIN_USERNAME = "admin";

    String ADMIN_PASSWORD = "admin123";


    if (ADMIN_USERNAME.equals(username)
            && ADMIN_PASSWORD.equals(password)) {

        sendJson(
                exchange,
                "{"
                        + "\"success\":true,"
                        + "\"message\":\"Login successful\""
                        + "}"
        );

    } else {

        sendJson(
                exchange,
                "{"
                        + "\"success\":false,"
                        + "\"message\":\"Invalid username or password.\""
                        + "}"
        );
    }


    return;
}
        // PREDEFINED CUSTOMER LOGIN
        if (path.equals("/api/customer/login")) {
            if (!method.equalsIgnoreCase("POST")) {
                sendJson(exchange, "{\"success\":false,\"message\":\"POST required\"}");
                return;
            }
            Map<String, String> data = parseFormData(readBody(exchange));
            boolean valid = "customer".equals(data.get("username"))
                    && "foodflow123".equals(data.get("password"));
            sendJson(exchange, valid
                    ? "{\"success\":true,\"message\":\"Login successful\"}"
                    : "{\"success\":false,\"message\":\"Invalid username or password.\"}");
            return;
        }

        // CUSTOMER MENU (temporary sample catalog)
        if (path.equals("/api/customer/menu")) {
            sendJson(exchange, getCustomerMenuJson());
            return;
        }

        if (path.equals("/api/rescue/deals")) {
            if ("GET".equalsIgnoreCase(method)) sendJson(exchange, rescueDealsJson());
            else if ("POST".equalsIgnoreCase(method)) saveRescueDeal(parseFormData(readBody(exchange)), exchange);
            else sendJson(exchange, "{\"success\":false,\"message\":\"GET or POST required\"}");
            return;
        }
        if (path.equals("/api/rescue/deactivate") && "POST".equalsIgnoreCase(method)) {
            Map<String, String> data = parseFormData(readBody(exchange));
            synchronized (rescueDeals) {
                RescueDeal deal = rescueDeals.get(data.get("dealId"));
                if (deal == null) sendJson(exchange, "{\"success\":false,\"message\":\"Deal not found.\"}");
                else { deal.active = false; sendJson(exchange, "{\"success\":true}"); }
            }
            return;
        }
        if (path.equals("/api/rescue/order") && "POST".equalsIgnoreCase(method)) {
            placeRescueOrder(parseFormData(readBody(exchange)), exchange);
            return;
        }
        if (path.equals("/api/rescue/impact")) {
            sendJson(exchange, rescueImpactJson());
            return;
        }

        // CUSTOMER ORDER: use the same addOrder path and shared data structures
        if (path.equals("/api/customer/order")) {
            if (!method.equalsIgnoreCase("POST")) {
                sendJson(exchange, "{\"success\":false,\"message\":\"POST required\"}");
                return;
            }
            addCustomerOrder(parseFormData(readBody(exchange)), exchange);
            return;
        }
        // GET ALL ORDERS
        if (path.equals("/api/orders")) {

            sendJson(
                    exchange,
                    getOrdersJson()
            );

            return;
        }


        // STATISTICS
        if (path.equals("/api/stats")) {

            sendJson(
                    exchange,
                    getStatsJson()
            );

            return;
        }


        // ADD ORDER
        if (path.equals("/api/add")) {

            if (!method.equalsIgnoreCase("POST")) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}"
                );

                return;
            }

            String body =
                    readBody(exchange);

            Map<String, String> data =
                    parseFormData(body);

            addOrder(
                    data,
                    exchange
            );

            return;
        }


        // DELETE / CANCEL ORDER
        if (path.equals("/api/delete")) {

            if (!method.equalsIgnoreCase("POST")) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}"
                );

                return;
            }

            String body =
                    readBody(exchange);

            Map<String, String> data =
                    parseFormData(body);

            deleteOrder(
                    data.get("orderId"),
                    exchange
            );

            return;
        }


        // PROCESS NEXT ORDER
        if (path.equals("/api/process")) {

            if (!method.equalsIgnoreCase("POST")) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}"
                );

                return;
            }

            processNextOrder(exchange);

            return;
        }


        // RETRIEVE CANCELLED ORDER
        if (path.equals("/api/cancelled/retrieve")) {

            if (!method.equalsIgnoreCase("POST")) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}"
                );

                return;
            }

            retrieveCancelledOrder(exchange);

            return;
        }


        // GET CANCELLED ORDERS
        if (path.equals("/api/cancelled")) {

            sendJson(
                    exchange,
                    getCancelledOrdersJson()
            );

            return;
        }


        // TRAVERSE LINKED LIST
        if (path.equals("/api/traverse")) {

            sendJson(
                    exchange,
                    getOrdersJson()
            );

            return;
        }


        // EXPRESSION EVALUATION
        if (path.equals("/api/evaluate")) {

            if (!method.equalsIgnoreCase("POST")) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}"
                );

                return;
            }

            String body =
                    readBody(exchange);

            Map<String, String> data =
                    parseFormData(body);

            evaluateExpression(
                    data.get("expression"),
                    exchange
            );

            return;
        }


        // =====================================================
        // PHASE 2 - BINARY SEARCH TREE
        // =====================================================

        // INORDER
        if (path.equals("/api/tree/inorder")) {

            sendJson(
                    exchange,
                    getTreeTraversalJson(
                            "Inorder",
                            orderTree.inorder()
                    )
            );

            return;
        }


        // PREORDER
        if (path.equals("/api/tree/preorder")) {

            sendJson(
                    exchange,
                    getTreeTraversalJson(
                            "Preorder",
                            orderTree.preorder()
                    )
            );

            return;
        }


        // POSTORDER
        if (path.equals("/api/tree/postorder")) {

            sendJson(
                    exchange,
                    getTreeTraversalJson(
                            "Postorder",
                            orderTree.postorder()
                    )
            );

            return;
        }


        // BST SEARCH
        if (path.equals("/api/tree/search")) {

            if (!method.equalsIgnoreCase("POST")) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}"
                );

                return;
            }

            String body =
                    readBody(exchange);

            Map<String, String> data =
                    parseFormData(body);

            searchTree(
                    data.get("orderId"),
                    exchange
            );

            return;
        }


        // =====================================================
        // PHASE 2 - GRAPH
        // =====================================================

        // GRAPH ADJACENCY LIST
        if (path.equals("/api/graph")) {

            sendJson(
                    exchange,
                    getGraphJson()
            );

            return;
        }


        // GRAPH BFS
        if (path.equals("/api/graph/bfs")) {

            String query =
                    exchange.getRequestURI()
                            .getRawQuery();

            Map<String, String> params =
                    parseQueryString(query);

            String start =
                    params.get("start");

            if (start == null ||
                    start.trim().isEmpty()) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"Start vertex is required.\"}"
                );

                return;
            }

            getBfsTraversal(
                    start,
                    exchange
            );

            return;
        }


        // =====================================================
        // PHASE 3 - SEARCH AND SORT
        // =====================================================

        if (path.equals("/api/search/linear")) {
            handleSearch(exchange, "linear");
            return;
        }

        if (path.equals("/api/search/binary")) {
            handleSearch(exchange, "binary");
            return;
        }

        // Compare Linear Search and Binary Search performance
        if (path.equals("/api/search/compare")) {
            handleSearchComparison(exchange);
            return;
        }

        if (path.equals("/api/sort")) {
            handleSort(exchange);
            return;
        }

        // =====================================================
        // PHASE 4 - GREEDY DELIVERY OPTION
        // =====================================================
        if (path.equals("/api/algorithm/greedy")) {
            handleGreedyDelivery(exchange);
            return;
        }

        // PHASE 4 - DYNAMIC PROGRAMMING (0/1 KNAPSACK)
        if (path.equals("/api/algorithm/dp")) {
            handleDynamicProgramming(exchange);
            return;
        }

        // PHASE 4 - BACKTRACKING (DELIVERY SLOT ASSIGNMENT)
        if (path.equals("/api/algorithm/backtracking")) {
            handleBacktracking(exchange);
            return;
        }

        // =====================================================
        // SERVE HTML / CSS / IMAGES
        // =====================================================

        serveStaticFile(
                exchange,
                path
        );
    }


    // =========================================================
    // LOAD SAMPLE ORDERS
    // =========================================================

    private static void loadSampleOrders() {

        if (orders.size() > 0) {
            return;
        }


        // -----------------------------------------------------
        // ORDER 1
        // -----------------------------------------------------

        Order order1 = new Order(
                "FD101",
                "Rahul",
                "9876543210",
                "Burger King",
                "Whopper",
                2,
                220,
                50,
                "Koramangala",
                5,
                "UPI"
        );


        // -----------------------------------------------------
        // ORDER 2
        // -----------------------------------------------------

        Order order2 = new Order(
                "FD102",
                "Ananya",
                "9876501234",
                "Dominos",
                "Farmhouse Pizza",
                1,
                499,
                70,
                "Indiranagar",
                7,
                "Card"
        );


        // -----------------------------------------------------
        // ORDER 3
        // -----------------------------------------------------

        Order order3 = new Order(
                "FD103",
                "Arjun",
                "9988776655",
                "KFC",
                "Chicken Bucket",
                1,
                399,
                60,
                "Whitefield",
                6,
                "Cash"
        );


        // -----------------------------------------------------
        // ORDER 4
        // -----------------------------------------------------

        Order order4 = new Order(
                "FD104",
                "Vikram",
                "9876123456",
                "McDonalds",
                "McChicken Meal",
                2,
                249,
                50,
                "HSR Layout",
                4,
                "UPI"
        );


        // -----------------------------------------------------
        // ORDER 5
        // -----------------------------------------------------

        Order order5 = new Order(
                "FD105",
                "Sneha",
                "9876987654",
                "Truffles",
                "Chicken Steak",
                1,
                450,
                60,
                "BTM Layout",
                5,
                "Card"
        );


        // -----------------------------------------------------
        // ORDER 6
        // -----------------------------------------------------

        Order order6 = new Order(
                "FD106",
                "Aditya",
                "9988771122",
                "Subway",
                "Chicken Teriyaki Sub",
                2,
                299,
                50,
                "Bellandur",
                8,
                "UPI"
        );


        // =====================================================
        // LINKED LIST
        // =====================================================

        orders.insert(order1);
        orders.insert(order2);
        orders.insert(order3);
        orders.insert(order4);
        orders.insert(order5);
        orders.insert(order6);


        // =====================================================
        // QUEUE
        // =====================================================

        pendingOrders.enqueue(order1);
        pendingOrders.enqueue(order2);
        pendingOrders.enqueue(order3);
        pendingOrders.enqueue(order4);
        pendingOrders.enqueue(order5);
        pendingOrders.enqueue(order6);


        // =====================================================
        // BINARY SEARCH TREE
        // =====================================================

        orderTree.insert(order1);
        orderTree.insert(order2);
        orderTree.insert(order3);
        orderTree.insert(order4);
        orderTree.insert(order5);
        orderTree.insert(order6);
    }


    // =========================================================
    // INITIALIZE DELIVERY NETWORK GRAPH
    // =========================================================

    private static void initializeGraph() {

        /*
         * =====================================================
         * FOODFLOW BANGALORE DELIVERY NETWORK
         * =====================================================
         *
         * Vertices:
         * Restaurants + Delivery Areas
         *
         * Edges:
         * Restaurant <-> Delivery Area
         *
         * The graph represents which restaurants
         * serve which delivery areas.
         *
         * Bangalore itself is NOT a vertex.
         *
         * This avoids the incorrect representation:
         *
         * Whitefield -> Bangalore
         *
         * Instead:
         *
         * Whitefield -> Restaurants serving Whitefield
         *
         * =====================================================
         */


        // =====================================================
        // DOMINOS
        // =====================================================

        deliveryGraph.addEdge(
                "Dominos",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Dominos",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "Dominos",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "Dominos",
                "BTM Layout"
        );

        deliveryGraph.addEdge(
                "Dominos",
                "Jayanagar"
        );


        // =====================================================
        // KFC
        // =====================================================

        deliveryGraph.addEdge(
                "KFC",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "KFC",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "KFC",
                "Marathahalli"
        );

        deliveryGraph.addEdge(
                "KFC",
                "Bellandur"
        );

        deliveryGraph.addEdge(
                "KFC",
                "Koramangala"
        );


        // =====================================================
        // BURGER KING
        // =====================================================

        deliveryGraph.addEdge(
                "Burger King",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Burger King",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "Burger King",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "Burger King",
                "BTM Layout"
        );


        // =====================================================
        // MCDONALDS
        // =====================================================

        deliveryGraph.addEdge(
                "McDonalds",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "McDonalds",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "McDonalds",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "McDonalds",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "McDonalds",
                "Bellandur"
        );


        // =====================================================
        // PIZZA HUT
        // =====================================================

        deliveryGraph.addEdge(
                "Pizza Hut",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "Pizza Hut",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "Pizza Hut",
                "Marathahalli"
        );

        deliveryGraph.addEdge(
                "Pizza Hut",
                "Jayanagar"
        );

        deliveryGraph.addEdge(
                "Pizza Hut",
                "BTM Layout"
        );


        // =====================================================
        // SUBWAY
        // =====================================================

        deliveryGraph.addEdge(
                "Subway",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Subway",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "Subway",
                "BTM Layout"
        );

        deliveryGraph.addEdge(
                "Subway",
                "Bellandur"
        );

        deliveryGraph.addEdge(
                "Subway",
                "Indiranagar"
        );


        // =====================================================
        // TRUFFLES
        // =====================================================

        deliveryGraph.addEdge(
                "Truffles",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Truffles",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "Truffles",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "Truffles",
                "BTM Layout"
        );

        deliveryGraph.addEdge(
                "Truffles",
                "Jayanagar"
        );


        // =====================================================
        // MEGHANA FOODS
        // =====================================================

        deliveryGraph.addEdge(
                "Meghana Foods",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Meghana Foods",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "Meghana Foods",
                "Marathahalli"
        );

        deliveryGraph.addEdge(
                "Meghana Foods",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "Meghana Foods",
                "Bellandur"
        );


        // =====================================================
        // EMPIRE RESTAURANT
        // =====================================================

        deliveryGraph.addEdge(
                "Empire Restaurant",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "Empire Restaurant",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Empire Restaurant",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "Empire Restaurant",
                "BTM Layout"
        );

        deliveryGraph.addEdge(
                "Empire Restaurant",
                "Jayanagar"
        );


        // =====================================================
        // CALIFORNIA BURRITO
        // =====================================================

        deliveryGraph.addEdge(
                "California Burrito",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "California Burrito",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "California Burrito",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "California Burrito",
                "Bellandur"
        );

        deliveryGraph.addEdge(
                "California Burrito",
                "Marathahalli"
        );


        // =====================================================
        // MTR
        // =====================================================

        deliveryGraph.addEdge(
                "MTR",
                "Jayanagar"
        );

        deliveryGraph.addEdge(
                "MTR",
                "BTM Layout"
        );

        deliveryGraph.addEdge(
                "MTR",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "MTR",
                "Indiranagar"
        );


        // =====================================================
        // CORNER HOUSE
        // =====================================================

        deliveryGraph.addEdge(
                "Corner House",
                "Koramangala"
        );

        deliveryGraph.addEdge(
                "Corner House",
                "Indiranagar"
        );

        deliveryGraph.addEdge(
                "Corner House",
                "Whitefield"
        );

        deliveryGraph.addEdge(
                "Corner House",
                "HSR Layout"
        );

        deliveryGraph.addEdge(
                "Corner House",
                "Jayanagar"
        );
    }


    // =========================================================
    // RESTAURANT VALIDATION
    // =========================================================

    private static boolean isValidRestaurant(
            String restaurant) {

        if (restaurant == null) {
            return false;
        }

        String name =
                restaurant.trim();

        return name.equalsIgnoreCase("Dominos")
                || name.equalsIgnoreCase("KFC")
                || name.equalsIgnoreCase("Burger King")
                || name.equalsIgnoreCase("McDonalds")
                || name.equalsIgnoreCase("Pizza Hut")
                || name.equalsIgnoreCase("Subway")
                || name.equalsIgnoreCase("Truffles")
                || name.equalsIgnoreCase("Meghana Foods")
                || name.equalsIgnoreCase("Empire Restaurant")
                || name.equalsIgnoreCase("California Burrito")
                || name.equalsIgnoreCase("MTR")
                || name.equalsIgnoreCase("Corner House");
    }


    // =========================================================
    // DELIVERY AREA VALIDATION
    // =========================================================

    private static boolean isValidDeliveryArea(
            String address) {

        if (address == null) {
            return false;
        }

        String area =
                address.trim();

        return area.equalsIgnoreCase("Koramangala")
                || area.equalsIgnoreCase("Indiranagar")
                || area.equalsIgnoreCase("Whitefield")
                || area.equalsIgnoreCase("HSR Layout")
                || area.equalsIgnoreCase("BTM Layout")
                || area.equalsIgnoreCase("Jayanagar")
                || area.equalsIgnoreCase("Marathahalli")
                || area.equalsIgnoreCase("Bellandur");
    }


    // =========================================================
    // CHECK RESTAURANT -> AREA CONNECTION
    // =========================================================

    private static boolean restaurantServesArea(
            String restaurant,
            String area) {

        if (!isValidRestaurant(restaurant)
                || !isValidDeliveryArea(area)) {

            return false;
        }

        Map<String, ArrayList<String>> graph =
                deliveryGraph.getAdjacencyList();

        ArrayList<String> areas =
                graph.get(restaurant);

        if (areas == null) {
            return false;
        }

        for (String connectedArea : areas) {

            if (connectedArea.equalsIgnoreCase(area)) {
                return true;
            }
        }

        return false;
    }


    // =========================================================
    // ADD ORDER
    // =========================================================

    private static synchronized void saveRescueDeal(Map<String, String> data, HttpExchange exchange) throws IOException {
        try {
            String id = data.get("dealId");
            RescueDeal deal;
            synchronized (rescueDeals) {
                deal = id == null || id.isEmpty() ? new RescueDeal() : rescueDeals.get(id);
                if (deal == null) { sendJson(exchange, "{\"success\":false,\"message\":\"Deal not found.\"}"); return; }
                String shop = data.get("shop"), item = data.get("item"), deadline = data.get("deadline");
                double original = Double.parseDouble(data.get("originalPrice"));
                double discounted = Double.parseDouble(data.get("discountedPrice"));
                int quantity = Integer.parseInt(data.get("quantity"));
                if (!getCustomerMenu().containsKey(shop) || !getCustomerMenu().get(shop).containsKey(item)) throw new IllegalArgumentException("Choose a listed shop and food item.");
                if (original <= 0 || discounted <= 0 || discounted >= original) throw new IllegalArgumentException("Discounted price must be lower than a positive original price.");
                if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
                LocalDateTime parsed = LocalDateTime.parse(deadline);
                if (!parsed.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Pickup deadline must be in the future.");
                deal.id = id == null || id.isEmpty() ? "RD" + nextRescueDealId++ : id;
                deal.shop = shop; deal.item = item; deal.originalPrice = original; deal.discountedPrice = discounted;
                deal.quantity = quantity; deal.deadline = parsed.toString(); deal.active = true;
                rescueDeals.put(deal.id, deal);
            }
            sendJson(exchange, "{\"success\":true,\"dealId\":\"" + deal.id + "\"}");
        } catch (Exception e) { sendJson(exchange, "{\"success\":false,\"message\":\"" + escape(e.getMessage() == null ? "Invalid deal details." : e.getMessage()) + "\"}"); }
    }

    private static String rescueDealsJson() {
        synchronized (rescueDeals) {
            StringBuilder out = new StringBuilder("{\"success\":true,\"deals\":["); boolean first = true;
            for (RescueDeal d : rescueDeals.values()) {
                if (!first) out.append(','); first = false;
                out.append("{\"id\":\"").append(escape(d.id)).append("\",\"shop\":\"").append(escape(d.shop))
                    .append("\",\"item\":\"").append(escape(d.item)).append("\",\"originalPrice\":").append(d.originalPrice)
                    .append(",\"discountedPrice\":").append(d.discountedPrice).append(",\"quantity\":").append(d.quantity)
                    .append(",\"deadline\":\"").append(escape(d.deadline)).append("\",\"active\":").append(d.active).append('}');
            }
            return out.append("]}").toString();
        }
    }

    private static synchronized void placeRescueOrder(Map<String, String> data, HttpExchange exchange) throws IOException {
        synchronized (rescueDeals) {
            try {
                RescueDeal d = rescueDeals.get(data.get("dealId"));
                int quantity = Integer.parseInt(data.get("quantity"));
                if (d == null || !d.active) throw new IllegalArgumentException("This rescue deal is unavailable.");
                if (LocalDateTime.now().isAfter(LocalDateTime.parse(d.deadline))) throw new IllegalArgumentException("The pickup deadline has passed.");
                if (quantity < 1 || quantity > d.quantity) throw new IllegalArgumentException("Requested quantity exceeds the available deal quantity.");
                String customer = data.get("customerName"), phone = data.get("phone");
                if (customer == null || customer.trim().isEmpty() || phone == null || phone.trim().isEmpty()) throw new IllegalArgumentException("Name and phone are required.");
                String id; do { id = "RF" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT); } while (orderTree.search(id) != null);
                Order order = new Order(id, customer.trim(), phone.trim(), d.shop, d.item + " (Food Rescue)", quantity, d.discountedPrice, 0, "Pickup by " + d.deadline, 0, data.getOrDefault("paymentMethod", "UPI"));
                order.markRescueOrder((d.originalPrice - d.discountedPrice) * quantity);
                orders.insert(order); pendingOrders.enqueue(order); orderTree.insert(order);
                d.quantity -= quantity;
                sendJson(exchange, "{\"success\":true,\"orderId\":\"" + id + "\",\"total\":" + order.getTotalAmount() + "}");
            } catch (Exception e) { sendJson(exchange, "{\"success\":false,\"message\":\"" + escape(e.getMessage() == null ? "Unable to place rescue order." : e.getMessage()) + "\"}"); }
        }
    }

    private static String rescueImpactJson() {
        int meals = 0; double savings = 0;
        for (Order o : orders.traverse()) if (o.isRescueOrder()) { meals += o.getQuantity(); savings += o.getCustomerSavings(); }
        return "{\"mealsRescued\":" + meals + ",\"customerSavings\":" + savings + "}";
    }

    // Temporary customer-facing menu. Existing sample order prices are reused
    // where available; remaining shops have clearly temporary sample entries.
    private static Map<String, Map<String, Double>> getCustomerMenu() {
        Map<String, Map<String, Double>> menu = new LinkedHashMap<>();
        addMenuItem(menu, "Burger King", "Whopper", 220);
        addMenuItem(menu, "Dominos", "Farmhouse Pizza", 499);
        addMenuItem(menu, "KFC", "Chicken Bucket", 399);
        addMenuItem(menu, "McDonalds", "McChicken Meal", 249);
        addMenuItem(menu, "Truffles", "Chicken Steak", 450);
        addMenuItem(menu, "Subway", "Chicken Teriyaki Sub", 299);
        addMenuItem(menu, "Pizza Hut", "Margherita Pizza (Sample)", 279);
        addMenuItem(menu, "Meghana Foods", "Chicken Biryani (Sample)", 320);
        addMenuItem(menu, "Empire Restaurant", "Chicken Kebab (Sample)", 260);
        addMenuItem(menu, "California Burrito", "Paneer Burrito (Sample)", 240);
        addMenuItem(menu, "MTR", "Masala Dosa (Sample)", 160);
        addMenuItem(menu, "Corner House", "Chocolate Sundae (Sample)", 180);
        return menu;
    }

    private static void addMenuItem(Map<String, Map<String, Double>> menu,
                                    String restaurant, String item, double price) {
        Map<String, Double> items = new LinkedHashMap<>();
        items.put(item, price);
        menu.put(restaurant, items);
    }

    private static String getCustomerMenuJson() {
        StringBuilder json = new StringBuilder("{\"success\":true,\"shops\":[");
        boolean firstShop = true;
        for (Map.Entry<String, Map<String, Double>> shop : getCustomerMenu().entrySet()) {
            if (!firstShop) json.append(',');
            firstShop = false;
            json.append("{\"name\":\"").append(escape(shop.getKey())).append("\",\"items\":[");
            boolean firstItem = true;
            for (Map.Entry<String, Double> item : shop.getValue().entrySet()) {
                if (!firstItem) json.append(',');
                firstItem = false;
                json.append("{\"name\":\"").append(escape(item.getKey()))
                        .append("\",\"price\":").append(item.getValue()).append('}');
            }
            json.append("]}");
        }
        return json.append("],\"areas\":[\"Koramangala\",\"Indiranagar\",\"Whitefield\",\"HSR Layout\",\"BTM Layout\",\"Jayanagar\",\"Marathahalli\",\"Bellandur\"]}").toString();
    }

    private static void addCustomerOrder(Map<String, String> data, HttpExchange exchange)
            throws IOException {
        try {
            String restaurant = data.get("restaurant");
            String foodItem = data.get("foodItem");
            Map<String, Double> items = getCustomerMenu().get(restaurant);
            if (items == null || !items.containsKey(foodItem)) {
                sendJson(exchange, "{\"success\":false,\"message\":\"Select an available shop and menu item.\"}");
                return;
            }
            int quantity = Integer.parseInt(data.get("quantity"));
            if (quantity < 1 || quantity > 99) {
                sendJson(exchange, "{\"success\":false,\"message\":\"Quantity must be between 1 and 99.\"}");
                return;
            }
            String orderId;
            do {
                orderId = "CF" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
            } while (orderTree.search(orderId) != null);

            Map<String, String> orderData = new HashMap<>(data);
            orderData.put("orderId", orderId);
            orderData.put("foodPrice", String.valueOf(items.get(foodItem)));
            orderData.put("distance", "5");
            orderData.put("baseCharge", "30");
            orderData.put("paymentMethod", data.getOrDefault("paymentMethod", "UPI"));
            if (orderData.get("customerName") == null || orderData.get("customerName").trim().isEmpty()
                    || orderData.get("phone") == null || orderData.get("phone").trim().isEmpty()) {
                sendJson(exchange, "{\"success\":false,\"message\":\"Name and phone are required.\"}");
                return;
            }
            addOrder(orderData, exchange);
        } catch (NumberFormatException ex) {
            sendJson(exchange, "{\"success\":false,\"message\":\"Enter a valid quantity.\"}");
        }
    }

    private static void addOrder(
            Map<String, String> data,
            HttpExchange exchange)
            throws IOException {

        try {

            String orderId =
                    data.get("orderId");

            String customerName =
                    data.get("customerName");

            String phone =
                    data.get("phone");

            String restaurant =
                    data.get("restaurant");

            String foodItem =
                    data.get("foodItem");


            int quantity =
                    Integer.parseInt(
                            data.get("quantity")
                    );


            double foodPrice =
                    Double.parseDouble(
                            data.get("foodPrice")
                    );


            double distance =
                    Double.parseDouble(
                            data.get("distance")
                    );


            double baseCharge =
                    Double.parseDouble(
                            data.get("baseCharge")
                    );


            String address =
                    data.get("address");


            String paymentMethod =
                    data.get("paymentMethod");


            // =================================================
            // VALIDATION
            // =================================================

            if (orderId == null ||
                    orderId.trim().isEmpty()) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"Order ID is required.\"}"
                );

                return;
            }


            // Check duplicate using BST
            if (orderTree.search(orderId) != null) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"Order ID already exists.\"}"
                );

                return;
            }


            // Validate restaurant
            if (!isValidRestaurant(restaurant)) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"Invalid restaurant.\"}"
                );

                return;
            }


            // Validate delivery area
            if (!isValidDeliveryArea(address)) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"Invalid delivery area. Available areas: Koramangala, Indiranagar, Whitefield, HSR Layout, BTM Layout, Jayanagar, Marathahalli, Bellandur.\"}"
                );

                return;
            }


            // Check restaurant service area
            if (!restaurantServesArea(
                    restaurant,
                    address)) {

                sendJson(
                        exchange,
                        "{\"success\":false,\"message\":\"This restaurant does not currently deliver to the selected area.\"}"
                );

                return;
            }


            // =================================================
            // STACK-BASED EXPRESSION
            // =================================================

            String expression =
                    baseCharge
                            + " + ("
                            + distance
                            + " * 10)";


            double deliveryCharge =
                    evaluator.evaluate(
                            expression
                    );


            // =================================================
            // CREATE ORDER
            // =================================================

            Order order = new Order(
                    orderId,
                    customerName,
                    phone,
                    restaurant,
                    foodItem,
                    quantity,
                    foodPrice,
                    deliveryCharge,
                    address,
                    distance,
                    paymentMethod
            );


            // =================================================
            // PHASE 1
            // =================================================

            // Linked List
            orders.insert(order);

            // Queue
            pendingOrders.enqueue(order);


            // =================================================
            // PHASE 2 - BST
            // =================================================

            orderTree.insert(order);


            // =================================================
            // PHASE 2 - GRAPH
            // =================================================

            /*
             * Connect restaurant and delivery area.
             *
             * The connection already exists for supported
             * restaurant-area combinations, but addEdge()
             * safely prevents the system from using a missing
             * graph vertex.
             */

            deliveryGraph.addEdge(
                    restaurant,
                    address
            );


            // =================================================
            // RESPONSE
            // =================================================

            String json =
                    "{"
                            + "\"success\":true,"
                            + "\"message\":\"Order added successfully.\","
                            + "\"orderId\":\""
                            + escape(orderId)
                            + "\","
                            + "\"restaurant\":\""
                            + escape(restaurant)
                            + "\","
                            + "\"deliveryArea\":\""
                            + escape(address)
                            + "\","
                            + "\"deliveryCharge\":"
                            + deliveryCharge
                            + ","
                            + "\"total\":"
                            + order.getTotalAmount()
                            + "}";


            sendJson(
                    exchange,
                    json
            );


        } catch (Exception e) {

            sendJson(
                    exchange,
                    "{"
                            + "\"success\":false,"
                            + "\"message\":\""
                            + escape(
                            e.getMessage()
                    )
                            + "\""
                            + "}"
            );
        }
    }


    // =========================================================
    // DELETE / CANCEL ORDER
    // =========================================================

    private static void deleteOrder(
            String orderId,
            HttpExchange exchange)
            throws IOException {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            sendJson(
                    exchange,
                    "{\"success\":false,\"message\":\"Order ID is required.\"}"
            );

            return;
        }


        // Find active order
        Order order =
                orders.search(orderId);


        if (order == null) {

            sendJson(
                    exchange,
                    "{\"success\":false,\"message\":\"Order not found.\"}"
            );

            return;
        }


        // Remove from Linked List
        Order deletedOrder =
                orders.delete(orderId);


        if (deletedOrder == null) {

            sendJson(
                    exchange,
                    "{\"success\":false,\"message\":\"Unable to cancel order.\"}"
            );

            return;
        }


        // Change status
        deletedOrder.setStatus(
                "Cancelled"
        );


        // Add to Stack
        cancelledOrders.push(
                deletedOrder
        );


        // Remove directly from Binary Search Tree
        boolean removedFromTree =
                orderTree.delete(
                        orderId
                );


        sendJson(
                exchange,
                "{"
                        + "\"success\":true,"
                        + "\"message\":\"Order cancelled successfully.\","
                        + "\"orderId\":\""
                        + escape(
                        deletedOrder.getOrderId()
                )
                        + "\","
                        + "\"removedFromBST\":"
                        + removedFromTree
                        + "}"
        );
    }


    // =========================================================
    // PROCESS NEXT ORDER
    // =========================================================

    private static void processNextOrder(
            HttpExchange exchange)
            throws IOException {

        Order processedOrder = null;


        while (!pendingOrders.isEmpty()) {

            Order nextOrder =
                    pendingOrders.dequeue();


            /*
             * Cancelled orders can still exist in the queue
             * because Queue follows FIFO and does not support
             * arbitrary deletion.
             *
             * Therefore, skip anything that is not Pending.
             */

            if (nextOrder.getStatus()
                    .equals("Pending")) {

                nextOrder.setStatus(
                        "Processing"
                );

                processedOrder =
                        nextOrder;

                break;
            }
        }


        if (processedOrder == null) {

            sendJson(
                    exchange,
                    "{"
                            + "\"success\":false,"
                            + "\"message\":\"No pending orders available.\""
                            + "}"
            );

            return;
        }


        sendJson(
                exchange,
                "{"
                        + "\"success\":true,"
                        + "\"message\":\"Order is now being processed.\","
                        + "\"orderId\":\""
                        + escape(
                        processedOrder.getOrderId()
                )
                        + "\""
                        + "}"
        );
    }


    // =========================================================
    // RETRIEVE CANCELLED ORDER
    // =========================================================

    private static void retrieveCancelledOrder(
            HttpExchange exchange)
            throws IOException {

        /*
         * Stack follows LIFO.
         *
         * Therefore the most recently cancelled
         * order will be retrieved first.
         */

        Order retrievedOrder =
                cancelledOrders.pop();


        if (retrievedOrder == null) {

            sendJson(
                    exchange,
                    "{\"success\":false,\"message\":\"No cancelled orders available.\"}"
            );

            return;
        }


        // Change status back to Pending
        retrievedOrder.setStatus(
                "Pending"
        );


        // Add back to Linked List
        orders.insert(
                retrievedOrder
        );


        // Add back to Queue
        pendingOrders.enqueue(
                retrievedOrder
        );


        // Add back to BST
        orderTree.insert(
                retrievedOrder
        );


        // Graph connection already exists
        // because restaurant and delivery area
        // are part of the valid delivery network.

        sendJson(
                exchange,
                "{"
                        + "\"success\":true,"
                        + "\"message\":\"Cancelled order retrieved successfully.\","
                        + "\"orderId\":\""
                        + escape(
                        retrievedOrder.getOrderId()
                )
                        + "\","
                        + "\"addedToBST\":true"
                        + "}"
        );
    }


    // =========================================================
    // EXPRESSION EVALUATOR
    // =========================================================

    private static void evaluateExpression(
            String expression,
            HttpExchange exchange)
            throws IOException {

        try {

            double result =
                    evaluator.evaluate(
                            expression
                    );


            sendJson(
                    exchange,
                    "{"
                            + "\"success\":true,"
                            + "\"result\":"
                            + result
                            + "}"
            );


        } catch (Exception e) {

            sendJson(
                    exchange,
                    "{"
                            + "\"success\":false,"
                            + "\"message\":\""
                            + escape(
                            e.getMessage()
                    )
                            + "\""
                            + "}"
            );
        }
    }


    // =========================================================
    // BST SEARCH
    // =========================================================

    private static void searchTree(
            String orderId,
            HttpExchange exchange)
            throws IOException {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            sendJson(
                    exchange,
                    "{\"success\":false,\"message\":\"Order ID is required.\"}"
            );

            return;
        }


        // REAL BST SEARCH
        Order found =
                orderTree.search(
                        orderId
                );


        if (found == null) {

            sendJson(
                    exchange,
                    "{"
                            + "\"success\":false,"
                            + "\"message\":\"Order not found in Binary Search Tree.\""
                            + "}"
            );

            return;
        }


        sendJson(
                exchange,
                "{"
                        + "\"success\":true,"
                        + "\"message\":\"Order found using Binary Search Tree.\","
                        + "\"order\":"
                        + orderToJson(found)
                        + "}"
        );
    }


    // =========================================================
    // TREE TRAVERSAL JSON
    // =========================================================

    private static String getTreeTraversalJson(
            String traversalName,
            Order[] traversalOrders) {

        StringBuilder json =
                new StringBuilder();


        json.append("{");

        json.append("\"success\":true,");

        json.append("\"traversal\":\"")
                .append(
                        escape(traversalName)
                )
                .append("\",");

        json.append("\"count\":")
                .append(
                        traversalOrders.length
                )
                .append(",");

        json.append("\"orders\":[");


        for (int i = 0;
             i < traversalOrders.length;
             i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(
                    orderToJson(
                            traversalOrders[i]
                    )
            );
        }


        json.append("]");

        json.append("}");


        return json.toString();
    }


    // =========================================================
    // GRAPH BFS
    // =========================================================

    private static void getBfsTraversal(
            String start,
            HttpExchange exchange)
            throws IOException {

        String[] traversal =
                deliveryGraph.bfs(
                        start
                );


        if (traversal.length == 0) {

            sendJson(
                    exchange,
                    "{"
                            + "\"success\":false,"
                            + "\"message\":\"Start vertex not found in graph.\""
                            + "}"
            );

            return;
        }


        StringBuilder json =
                new StringBuilder();


        json.append("{");

        json.append("\"success\":true,");

        json.append("\"algorithm\":\"BFS\",");

        json.append("\"start\":\"")
                .append(
                        escape(start)
                )
                .append("\",");

        json.append("\"traversal\":[");


        for (int i = 0;
             i < traversal.length;
             i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append("\"")
                    .append(
                            escape(
                                    traversal[i]
                            )
                    )
                    .append("\"");
        }


        json.append("]");

        json.append("}");


        sendJson(
                exchange,
                json.toString()
        );
    }


    // =========================================================
    // GRAPH JSON
    // =========================================================

    private static String getGraphJson() {

        Map<String, ArrayList<String>> graph =
                deliveryGraph.getAdjacencyList();


        StringBuilder json =
                new StringBuilder();


        json.append("{");

        json.append("\"success\":true,");

        json.append("\"representation\":\"Adjacency List\",");

        json.append("\"vertices\":[");


        int vertexIndex = 0;


        for (String vertex :
                graph.keySet()) {

            if (vertexIndex > 0) {
                json.append(",");
            }

            json.append("\"")
                    .append(
                            escape(vertex)
                    )
                    .append("\"");

            vertexIndex++;
        }


        json.append("],");


        json.append("\"adjacencyList\":{");


        int mapIndex = 0;


        for (Map.Entry<String, ArrayList<String>> entry :
                graph.entrySet()) {

            if (mapIndex > 0) {
                json.append(",");
            }

            json.append("\"")
                    .append(
                            escape(
                                    entry.getKey()
                            )
                    )
                    .append("\":[");


            ArrayList<String> neighbors =
                    entry.getValue();


            for (int i = 0;
                 i < neighbors.size();
                 i++) {

                if (i > 0) {
                    json.append(",");
                }

                json.append("\"")
                        .append(
                                escape(
                                        neighbors.get(i)
                                )
                        )
                        .append("\"");
            }


            json.append("]");

            mapIndex++;
        }


        json.append("}");

        json.append("}");


        return json.toString();
    }


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    private static String getOrdersJson() {

        Order[] orderArray =
                orders.traverse();


        StringBuilder json =
                new StringBuilder("[");


        for (int i = 0;
             i < orderArray.length;
             i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(
                    orderToJson(
                            orderArray[i]
                    )
            );
        }


        json.append("]");


        return json.toString();
    }


    // =========================================================
    // GET CANCELLED ORDERS
    // =========================================================

    private static String getCancelledOrdersJson() {

        Order[] cancelled =
                cancelledOrders.toArray();


        StringBuilder json =
                new StringBuilder("[");


        for (int i = 0;
             i < cancelled.length;
             i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(
                    orderToJson(
                            cancelled[i]
                    )
            );
        }


        json.append("]");


        return json.toString();
    }


    // =========================================================
    // GET STATISTICS
    // =========================================================

    private static String getStatsJson() {

        int totalOrders =
                orders.size();

        int pending = 0;

        int processing = 0;

        int cancelled =
                cancelledOrders.size();


        Order[] allOrders =
                orders.traverse();


        for (Order order :
                allOrders) {

            if (order.getStatus()
                    .equals("Pending")) {

                pending++;

            } else if (
                    order.getStatus()
                            .equals("Processing")
            ) {

                processing++;
            }
        }


        return "{"
                + "\"totalOrders\":"
                + totalOrders
                + ","
                + "\"pendingOrders\":"
                + pending
                + ","
                + "\"processingOrders\":"
                + processing
                + ","
                + "\"cancelledOrders\":"
                + cancelled
                + "}";
    }


    // =========================================================
    // ORDER TO JSON
    // =========================================================

    private static String orderToJson(
            Order order) {

        return "{"

                + "\"orderId\":\""
                + escape(
                order.getOrderId()
        )
                + "\","

                + "\"customerName\":\""
                + escape(
                order.getCustomerName()
        )
                + "\","

                + "\"phone\":\""
                + escape(
                order.getPhone()
        )
                + "\","

                + "\"restaurant\":\""
                + escape(
                order.getRestaurant()
        )
                + "\","

                + "\"foodItem\":\""
                + escape(
                order.getFoodItem()
        )
                + "\","

                + "\"quantity\":"
                + order.getQuantity()
                + ","

                + "\"foodPrice\":"
                + order.getFoodPrice()
                + ","

                + "\"deliveryCharge\":"
                + order.getDeliveryCharge()
                + ","

                + "\"address\":\""
                + escape(
                order.getAddress()
        )
                + "\","

                + "\"distance\":"
                + order.getDistance()
                + ","

                + "\"paymentMethod\":\""
                + escape(
                order.getPaymentMethod()
        )
                + "\","

                + "\"status\":\""
                + escape(
                order.getStatus()
        )
                + "\","

                + "\"total\":"
                + order.getTotalAmount()

                + ",\"rescueOrder\":" + order.isRescueOrder()
                + ",\"customerSavings\":" + order.getCustomerSavings()

                + "}";
    }


    // =========================================================
    // STATIC FILE SERVER
    // =========================================================

    private static void serveStaticFile(
            HttpExchange exchange,
            String path)
            throws IOException {

        if (path.contains("..")) {

            sendText(
                    exchange,
                    "Invalid path.",
                    400,
                    "text/plain"
            );

            return;
        }


        if (path.equals("/")) {

            path =
                    "/index.html";
        }


        Path filePath =
                Paths.get(
                        "web",
                        path.substring(1)
                );


        if (!Files.exists(filePath)
                || Files.isDirectory(filePath)) {

            sendText(
                    exchange,
                    "404 - Page Not Found",
                    404,
                    "text/plain"
            );

            return;
        }


        byte[] content =
                Files.readAllBytes(
                        filePath
                );


        String contentType =
                getContentType(
                        filePath.toString()
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType
                );


        exchange.sendResponseHeaders(
                200,
                content.length
        );


        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(content);
        }
    }


    // =========================================================
    // CONTENT TYPE
    // =========================================================

    private static String getContentType(
            String fileName) {

        if (fileName.endsWith(".html")) {

            return "text/html; charset=UTF-8";
        }


        if (fileName.endsWith(".css")) {

            return "text/css; charset=UTF-8";
        }


        if (fileName.endsWith(".js")) {

            return "application/javascript; charset=UTF-8";
        }


        if (fileName.endsWith(".png")) {

            return "image/png";
        }


        if (fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")) {

            return "image/jpeg";
        }


        if (fileName.endsWith(".svg")) {

            return "image/svg+xml";
        }


        return "application/octet-stream";
    }


    // =========================================================
    // READ REQUEST BODY
    // =========================================================

    private static String readBody(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();


        return new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }


    // =========================================================
    // PARSE FORM DATA
    // =========================================================

    private static Map<String, String> parseFormData(
            String body) {

        Map<String, String> data =
                new HashMap<>();


        if (body == null
                || body.isEmpty()) {

            return data;
        }


        String[] pairs =
                body.split("&");


        for (String pair :
                pairs) {

            String[] parts =
                    pair.split(
                            "=",
                            2
                    );


            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8
                        );


                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );


                data.put(
                        key,
                        value
                );
            }
        }


        return data;
    }


    // =========================================================
    // PARSE URL QUERY STRING
    // =========================================================

    private static Map<String, String> parseQueryString(
            String query) {

        Map<String, String> data =
                new HashMap<>();


        if (query == null
                || query.isEmpty()) {

            return data;
        }


        String[] pairs =
                query.split("&");


        for (String pair :
                pairs) {

            String[] parts =
                    pair.split(
                            "=",
                            2
                    );


            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8
                        );


                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );


                data.put(
                        key,
                        value
                );
            }
        }


        return data;
    }


    // =========================================================
    // SEND JSON
    // =========================================================

    private static void sendJson(
            HttpExchange exchange,
            String json)
            throws IOException {

        sendText(
                exchange,
                json,
                200,
                "application/json; charset=UTF-8"
        );
    }


    // =========================================================
    // SEND TEXT
    // =========================================================

    private static void sendText(
            HttpExchange exchange,
            String text,
            int status,
            String contentType)
            throws IOException {

        byte[] response =
                text.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType
                );


        exchange.sendResponseHeaders(
                status,
                response.length
        );


        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(response);
        }
    }


    // =========================================================
    // ESCAPE JSON STRING
    // =========================================================

    private static String escape(
            String text) {

        if (text == null) {
            return "";
        }


        return text
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );
    }
    
    // =========================================================
    // PHASE 3 - SEARCH
    // =========================================================

    private static void handleSearch(
            HttpExchange exchange,
            String type) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"POST required\"}");
            return;
        }

        Map<String, String> data =
                parseFormData(readBody(exchange));

        String orderId = data.get("orderId");

        if (orderId == null || orderId.trim().isEmpty()) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"Enter an Order ID.\"}");
            return;
        }

        Order[] allOrders = orders.traverse();

        SearchSort.SearchResult result;

        if (type.equals("linear")) {
            result = SearchSort.linearSearch(allOrders, orderId);
        } else {
            Order[] sortedOrders =
                    SearchSort.mergeSortByOrderId(allOrders);

            result = SearchSort.binarySearch(sortedOrders, orderId);
        }

        if (!result.isFound()) {
            sendJson(exchange,
                    "{\"success\":false,"
                    + "\"message\":\"Order not found.\","
                    + "\"comparisons\":" + result.getComparisons() + "}");
            return;
        }

        sendJson(exchange,
                "{\"success\":true,"
                + "\"order\":" + orderToJson(result.getOrder()) + ","
                + "\"comparisons\":" + result.getComparisons() + "}");
    }


    // =========================================================
    // PHASE 3 - SORT
    // =========================================================

    private static void handleSort(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"GET required\"}");
            return;
        }

        String query = exchange.getRequestURI().getRawQuery();

        Map<String, String> params = parseQueryString(query);

        String sortBy = params.get("by");

        Order[] allOrders = orders.traverse();
        Order[] sortedOrders;

        if ("total".equalsIgnoreCase(sortBy)) {
            sortedOrders = SearchSort.mergeSortByTotal(allOrders);
        } else {
            sortedOrders = SearchSort.mergeSortByOrderId(allOrders);
        }

        StringBuilder json = new StringBuilder();

        json.append("{\"success\":true,\"sortBy\":\"")
                .append("total".equalsIgnoreCase(sortBy)
                        ? "total" : "orderId")
                .append("\",\"algorithm\":\"Merge Sort\",\"orders\":[");

        for (int i = 0; i < sortedOrders.length; i++) {
            if (i > 0) {
                json.append(",");
            }

            json.append(orderToJson(sortedOrders[i]));
        }

        json.append("]}");

        sendJson(exchange, json.toString());
    }
private static void handleSearchComparison(HttpExchange exchange)
        throws IOException {

    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendJson(exchange,
                "{\"success\":false,\"message\":\"POST required\"}");
        return;
    }

    Map<String, String> formData =
            parseFormData(readBody(exchange));

    String orderId = formData.get("orderId");

    if (orderId == null || orderId.trim().isEmpty()) {
        sendJson(exchange,
                "{\"success\":false,\"message\":\"Enter an Order ID\"}");
        return;
    }

    Order[] allOrders = Main.orders.traverse();

    SearchSort.SearchResult linear =
            SearchSort.linearSearch(allOrders, orderId.trim());

    Order[] sortedOrders =
            SearchSort.mergeSortByOrderId(allOrders);

    SearchSort.SearchResult binary =
            SearchSort.binarySearch(sortedOrders, orderId.trim());

    String json = "{"
            + "\"success\":true,"
            + "\"orderId\":\"" + orderId.trim().replace("\\", "\\\\")
                    .replace("\"", "\\\"") + "\","
            + "\"totalOrders\":" + allOrders.length + ","
            + "\"linearComparisons\":" + linear.getComparisons() + ","
            + "\"binaryComparisons\":" + binary.getComparisons() + ","
            + "\"linearFound\":" + linear.isFound() + ","
            + "\"binaryFound\":" + binary.isFound()
            + "}";

    sendJson(exchange, json);
}

    // =========================================================
    // PHASE 4 - GREEDY ALGORITHM
    // Select the nearest candidate by distance
    // =========================================================
    private static void handleGreedyDelivery(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"GET required\"}");
            return;
        }

        Map<String, String> params =
                parseQueryString(exchange.getRequestURI().getRawQuery());

        String optionsText = params.get("options");
        if (optionsText == null || optionsText.trim().isEmpty()) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"Provide options as Name:distance,Name:distance\"}");
            return;
        }

        try {
            String[] entries = optionsText.split(",");
            List<GreedyAlgorithm.DeliveryOption> options = new ArrayList<>();

            for (String entry : entries) {
                String[] parts = entry.trim().split(":", 2);
                if (parts.length != 2 || parts[0].trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Each option must use Name:distance format.");
                }

                double distance = Double.parseDouble(parts[1].trim());
                options.add(new GreedyAlgorithm.DeliveryOption(
                        parts[0].trim(), distance));
            }

            GreedyAlgorithm.DeliveryOption best =
                    GreedyAlgorithm.selectNearest(options);

            String json = "{"
                    + "\"success\":true,"
                    + "\"algorithm\":\"Greedy - Nearest Delivery Option\","
                    + "\"strategy\":\"Choose the available option with the smallest distance\","
                    + "\"candidateCount\":" + options.size() + ","
                    + "\"selected\":{\"name\":\"" + escape(best.getName()) + "\","
                    + "\"distance\":" + best.getDistance() + "},"
                    + "\"candidates\":[";

            for (int i = 0; i < options.size(); i++) {
                if (i > 0) json += ",";
                GreedyAlgorithm.DeliveryOption option = options.get(i);
                json += "{\"name\":\"" + escape(option.getName()) + "\","
                        + "\"distance\":" + option.getDistance() + "}";
            }

            json += "]}";
            sendJson(exchange, json);

        } catch (IllegalArgumentException e) {
            sendJson(exchange, "{\"success\":false,\"message\":\""
                    + escape(e.getMessage()) + "\"}");
        }
    }



    // =========================================================
    // PHASE 4 - DYNAMIC PROGRAMMING: 0/1 KNAPSACK
    // Query format: capacity=5&items=OrderA:2:100,OrderB:3:150
    // =========================================================
    private static void handleDynamicProgramming(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"GET required\"}");
            return;
        }

        Map<String, String> params =
                parseQueryString(exchange.getRequestURI().getRawQuery());

        try {
            String capacityText = params.get("capacity");
            String itemsText = params.get("items");

            if (capacityText == null || capacityText.trim().isEmpty()) {
                throw new IllegalArgumentException("Capacity is required.");
            }
            if (itemsText == null || itemsText.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Provide items as Name:weight:value,Name:weight:value.");
            }

            int capacity = Integer.parseInt(capacityText.trim());
            String[] entries = itemsText.split(",");
            List<DynamicProgramming.Item> items = new ArrayList<>();

            for (String entry : entries) {
                String[] parts = entry.trim().split(":", 3);
                if (parts.length != 3 || parts[0].trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Each item must use Name:weight:value format.");
                }

                int weight = Integer.parseInt(parts[1].trim());
                int value = Integer.parseInt(parts[2].trim());
                items.add(new DynamicProgramming.Item(
                        parts[0].trim(), weight, value));
            }

            DynamicProgramming.Result result =
                    DynamicProgramming.solveKnapsack(items, capacity);

            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,")
                    .append("\"algorithm\":\"Dynamic Programming - 0/1 Knapsack\",")
                    .append("\"capacity\":").append(capacity).append(",")
                    .append("\"totalWeight\":").append(result.getTotalWeight()).append(",")
                    .append("\"totalValue\":").append(result.getTotalValue()).append(",")
                    .append("\"selectedItems\":[");

            List<DynamicProgramming.Item> selected = result.getSelectedItems();
            for (int i = 0; i < selected.size(); i++) {
                if (i > 0) json.append(",");
                DynamicProgramming.Item item = selected.get(i);
                json.append("{\"name\":\"").append(escape(item.getName())).append("\",")
                        .append("\"weight\":").append(item.getWeight()).append(",")
                        .append("\"value\":").append(item.getValue()).append("}");
            }

            json.append("]}");
            sendJson(exchange, json.toString());

        } catch (IllegalArgumentException e) {
            sendJson(exchange, "{\"success\":false,\"message\":\""
                    + escape(e.getMessage()) + "\"}");
        }
    }


    // =========================================================
    // PHASE 4 - BACKTRACKING: DELIVERY SLOT ASSIGNMENT
    // Query format: orders=OrderA:Slot1|Slot2;OrderB:Slot2|Slot3
    // Each delivery slot can be assigned to only one order.
    // =========================================================
    private static void handleBacktracking(HttpExchange exchange)
            throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange,
                    "{\"success\":false,\"message\":\"GET required\"}");
            return;
        }

        Map<String, String> params =
                parseQueryString(exchange.getRequestURI().getRawQuery());
        String ordersText = params.get("orders");

        try {
            if (ordersText == null || ordersText.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Provide orders as OrderName:SlotA|SlotB;OrderName2:SlotB|SlotC.");
            }

            List<Backtracking.OrderSlotRequest> requests = new ArrayList<>();
            Set<String> uniqueNames = new HashSet<>();
            for (String entry : ordersText.split(";")) {
                String[] parts = entry.split(":", 2);
                if (parts.length != 2 || parts[0].trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Each order must use OrderName:SlotA|SlotB format.");
                }
                String name = parts[0].trim();
                if (!uniqueNames.add(name.toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("Order names must be unique: " + name);
                }

                List<String> slots = new ArrayList<>();
                Set<String> seenSlots = new HashSet<>();
                for (String slotText : parts[1].split("\\|")) {
                    String slot = slotText.trim();
                    if (slot.isEmpty()) continue;
                    if (seenSlots.add(slot.toLowerCase(Locale.ROOT))) slots.add(slot);
                }
                if (slots.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Every order needs at least one available delivery slot.");
                }
                requests.add(new Backtracking.OrderSlotRequest(name, slots));
            }

            List<Backtracking.Assignment> assignments =
                    Backtracking.findAssignment(requests);
            if (assignments == null) {
                sendJson(exchange,
                        "{\"success\":true,\"possible\":false,"
                        + "\"algorithm\":\"Backtracking - Delivery Slot Assignment\","
                        + "\"message\":\"No conflict-free assignment exists for these choices. Try adding or changing available slots.\"}");
                return;
            }

            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"possible\":true,")
                    .append("\"algorithm\":\"Backtracking - Delivery Slot Assignment\",")
                    .append("\"assignedCount\":").append(assignments.size()).append(",")
                    .append("\"assignments\":[");
            for (int i = 0; i < assignments.size(); i++) {
                if (i > 0) json.append(",");
                Backtracking.Assignment a = assignments.get(i);
                json.append("{\"order\":\"").append(escape(a.getOrderName()))
                        .append("\",\"slot\":\"").append(escape(a.getSlot()))
                        .append("\"}");
            }
            json.append("]}");
            sendJson(exchange, json.toString());

        } catch (IllegalArgumentException e) {
            sendJson(exchange, "{\"success\":false,\"message\":\""
                    + escape(e.getMessage()) + "\"}");
        }
    }


}
