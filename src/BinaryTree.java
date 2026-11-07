import java.util.ArrayList;

public class BinaryTree {

    private TreeNode root;

    public BinaryTree() {
        root = null;
    }

    // =========================================================
    // INSERT
    // =========================================================

    public void insert(Order order) {

        if (order == null) {
            return;
        }

        TreeNode newNode =
                new TreeNode(order);

        if (root == null) {
            root = newNode;
            return;
        }

        TreeNode current = root;

        while (true) {

            int comparison =
                    order.getOrderId()
                            .compareToIgnoreCase(
                                    current.data.getOrderId()
                            );

            if (comparison < 0) {

                if (current.left == null) {

                    current.left = newNode;
                    return;
                }

                current = current.left;

            } else if (comparison > 0) {

                if (current.right == null) {

                    current.right = newNode;
                    return;
                }

                current = current.right;

            } else {

                // Duplicate Order ID
                return;
            }
        }
    }


    // =========================================================
    // SEARCH
    // =========================================================

    public Order search(String orderId) {

        if (orderId == null) {
            return null;
        }

        TreeNode current = root;

        while (current != null) {

            int comparison =
                    orderId.compareToIgnoreCase(
                            current.data.getOrderId()
                    );

            if (comparison == 0) {

                return current.data;
            }

            if (comparison < 0) {

                current = current.left;

            } else {

                current = current.right;
            }
        }

        return null;
    }


    // =========================================================
    // DELETE
    // =========================================================

    public boolean delete(String orderId) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            return false;
        }

        if (search(orderId) == null) {

            return false;
        }

        root = deleteNode(
                root,
                orderId
        );

        return true;
    }


    private TreeNode deleteNode(
            TreeNode node,
            String orderId) {

        if (node == null) {
            return null;
        }


        int comparison =
                orderId.compareToIgnoreCase(
                        node.data.getOrderId()
                );


        // Search left subtree
        if (comparison < 0) {

            node.left =
                    deleteNode(
                            node.left,
                            orderId
                    );

            return node;
        }


        // Search right subtree
        if (comparison > 0) {

            node.right =
                    deleteNode(
                            node.right,
                            orderId
                    );

            return node;
        }


        // =====================================================
        // NODE FOUND
        // =====================================================

        // Case 1: No children
        if (node.left == null &&
                node.right == null) {

            return null;
        }


        // Case 2: Only right child
        if (node.left == null) {

            return node.right;
        }


        // Case 3: Only left child
        if (node.right == null) {

            return node.left;
        }


        // Case 4: Two children
        //
        // Find the smallest node in the
        // right subtree.
        TreeNode successor =
                findMinimum(
                        node.right
                );


        node.data =
                successor.data;


        node.right =
                deleteNode(
                        node.right,
                        successor.data.getOrderId()
                );


        return node;
    }


    // =========================================================
    // FIND MINIMUM
    // =========================================================

    private TreeNode findMinimum(
            TreeNode node) {

        TreeNode current = node;

        while (current.left != null) {

            current = current.left;
        }

        return current;
    }


    // =========================================================
    // INORDER
    //
    // Left → Root → Right
    //
    // Produces Order IDs in sorted order.
    // =========================================================

    public Order[] inorder() {

        ArrayList<Order> result =
                new ArrayList<>();

        inorderTraversal(
                root,
                result
        );

        return result.toArray(
                new Order[0]
        );
    }


    private void inorderTraversal(
            TreeNode node,
            ArrayList<Order> result) {

        if (node == null) {
            return;
        }

        inorderTraversal(
                node.left,
                result
        );

        result.add(
                node.data
        );

        inorderTraversal(
                node.right,
                result
        );
    }


    // =========================================================
    // PREORDER
    //
    // Root → Left → Right
    // =========================================================

    public Order[] preorder() {

        ArrayList<Order> result =
                new ArrayList<>();

        preorderTraversal(
                root,
                result
        );

        return result.toArray(
                new Order[0]
        );
    }


    private void preorderTraversal(
            TreeNode node,
            ArrayList<Order> result) {

        if (node == null) {
            return;
        }

        result.add(
                node.data
        );

        preorderTraversal(
                node.left,
                result
        );

        preorderTraversal(
                node.right,
                result
        );
    }


    // =========================================================
    // POSTORDER
    //
    // Left → Right → Root
    // =========================================================

    public Order[] postorder() {

        ArrayList<Order> result =
                new ArrayList<>();

        postorderTraversal(
                root,
                result
        );

        return result.toArray(
                new Order[0]
        );
    }


    private void postorderTraversal(
            TreeNode node,
            ArrayList<Order> result) {

        if (node == null) {
            return;
        }

        postorderTraversal(
                node.left,
                result
        );

        postorderTraversal(
                node.right,
                result
        );

        result.add(
                node.data
        );
    }


    // =========================================================
    // GET ROOT
    // =========================================================

    public Order getRoot() {

        if (root == null) {
            return null;
        }

        return root.data;
    }


    // =========================================================
    // CHECK EMPTY
    // =========================================================

    public boolean isEmpty() {

        return root == null;
    }


    // =========================================================
    // CLEAR
    // =========================================================

    public void clear() {

        root = null;
    }
}