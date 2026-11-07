# 🍔 FoodFlow – Food Delivery Management System

**FoodFlow** is a Java-based Food Delivery Management System developed to demonstrate the practical implementation of Data Structures and Algorithms (DSA) in a real-world food ordering and order management scenario.

The project combines a Java backend with an HTML, CSS, and JavaScript frontend to provide an interactive interface for managing food orders, processing requests, searching records, and demonstrating different algorithmic techniques.

## ✨ Features

### 📦 Order Management
- Add new food delivery orders.
- View and manage existing orders.
- Delete orders using their Order ID.
- Search for specific orders.
- View order details, including customer information, restaurant, food item, quantity, and delivery charges.
- Calculate the total order amount.

### 🔄 Order Processing
- Process pending orders using a Queue.
- Manage cancelled orders using a Stack.
- Maintain order records using a Linked List.
- Display order information through a dedicated interface.

### 🔍 Searching and Sorting
- Linear Search to locate orders.
- Binary Search to find orders by Order ID.
- Merge Sort to sort orders by Order ID.
- Merge Sort to sort orders by total amount.
- Display comparison counts for search operations.

### 🌳 Data Structures and Algorithms

FoodFlow demonstrates several important concepts:

| Concept | Implementation |
|---|---|
| Linked List | Stores and manages order records |
| Queue | Processes pending orders using FIFO |
| Stack | Manages cancelled orders using LIFO |
| Binary Search Tree | Organizes orders using Order IDs |
| Graph | Represents connections and performs BFS traversal |
| Greedy Algorithm | Selects the nearest option from supplied distances |
| Dynamic Programming | Solves the 0/1 Knapsack problem |
| Backtracking | Assigns unique delivery slots |
| Expression Evaluation | Evaluates mathematical expressions using stacks |
| Searching and Sorting | Implements search and merge sort algorithms |

## 🛠️ Technologies Used

**Backend**
- Java
- Java HTTP Server (`HttpServer`)
- Object-Oriented Programming

**Frontend**
- HTML5
- CSS3
- JavaScript

**Development Tools**
- Visual Studio Code
- JDK 21
- Git and GitHub

## 📁 Project Structure

```text
FoodFlow/
│
├── src/
│   ├── Main.java
│   ├── Order.java
│   ├── Node.java
│   ├── LinkedList.java
│   ├── Queue.java
│   ├── Stack.java
│   ├── BinaryTree.java
│   ├── TreeNode.java
│   ├── Graph.java
│   ├── SearchSort.java
│   ├── GreedyAlgorithm.java
│   ├── DynamicProgramming.java
│   ├── Backtracking.java
│   └── ExpressionEvaluator.java
│
├── web/
│   ├── index.html
│   ├── login.html
│   ├── orders.html
│   ├── orders_add.html
│   ├── orders_delete.html
│   ├── process_orders.html
│   ├── cancelled_orders.html
│   ├── traverse.html
│   ├── search_sort.html
│   ├── algorithms.html
│   ├── dsa.html
│   └── css/
│       └── style.css
│
└── README.md
```

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- Java Development Kit (JDK 21 or compatible)
- Visual Studio Code (recommended)
- Git

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/7vyshnav07-bot/Food-Delivery-Management-System.git
```

**2. Navigate to the project directory**

```bash
cd Food-Delivery-Management-System
```

**3. Open the project in Visual Studio Code**

```bash
code .
```

**4. Run the application**

Open `src/Main.java` in Visual Studio Code and run the `Main` class.

The Java HTTP server starts and serves the frontend pages from the `web` directory. Open the application in your browser using the local address and port configured in `Main.java`.

## 🧠 Data Storage

FoodFlow currently uses in-memory data structures to store and manage order information.

- Linked List maintains the order records.
- Queue manages pending orders.
- Stack maintains cancelled orders.
- Binary Search Tree supports order insertion, searching, deletion, and traversal.

**Note:** The current implementation does not use an external database. In-memory records are not permanently retained after the application is restarted.

## 🎯 Project Objectives

- Apply theoretical Data Structures and Algorithms concepts to a practical application.
- Understand the working of linear and non-linear data structures.
- Implement searching, sorting, graph traversal, and optimization techniques.
- Develop a functional interface for food delivery order management.
- Demonstrate the relationship between algorithmic efficiency and real-world applications.

## 🔮 Future Enhancements

- Integrate a database such as MySQL for permanent data storage.
- Implement customer and administrator authentication.
- Add restaurant and menu management.
- Introduce real-time order tracking.
- Integrate online payment functionality.
- Develop delivery partner management.
- Improve order processing with real-world delivery route information.

## 🎓 Academic Project

FoodFlow was developed as an academic project to demonstrate the implementation and application of Data Structures and Algorithms using Java.

The project brings together multiple DSA concepts in a single food delivery management application, providing practical experience in Java programming, backend development, frontend integration, and algorithm design.

## 👨‍💻 Author

**Developed as part of an MCA academic project.**

GitHub: [@7vyshnav07-bot](https://github.com/7vyshnav07-bot)

---

⭐ If you find this project useful, consider giving the repository a star!
