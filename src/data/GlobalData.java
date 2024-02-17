package data;

import model.*;

import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GlobalData {
    private final String clientsFilePath;
    private final String itemsFilePath;
    private final String ordersFilePath;
    private final String orderItemsFilePath;

    private File clientsFile;
    private File itemsFile;
    private File ordersFile;
    private File orderItemsFile;

    private final List<Client> clients;
    private final List<Item> items;
    private final List<Order> orders;

    public GlobalData() {
        this.clientsFilePath = "clients.txt";
        this.itemsFilePath = "items.txt";
        this.ordersFilePath = "orders.txt";
        this.orderItemsFilePath = "orderItems.txt";

        this.prepareFiles();

        this.clients = new ArrayList<>();
        this.items = new ArrayList<>();
        this.orders = new ArrayList<>();

        this.fetchAllClientsFromFile();
        this.fetchAllItemsFromFile();
        this.fetchAllOrdersFromFile();
    }

    private void prepareFiles() {
        this.clientsFile = new File(this.clientsFilePath);

        if(!this.clientsFile.exists()) {
            createFile(this.clientsFile);
        }

        this.itemsFile = new File(this.itemsFilePath);

        if(!this.itemsFile.exists()) {
            createFile(this.itemsFile);
        }

        this.ordersFile = new File(this.ordersFilePath);

        if(!this.ordersFile.exists()) {
            createFile(this.ordersFile);
        }

        this.orderItemsFile = new File(this.orderItemsFilePath);

        if(!this.orderItemsFile.exists()) {
            createFile(this.orderItemsFile);
        }
    }

    private void createFile(File file) {
        try {
            boolean status = file.createNewFile();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private void saveAllClientsToFile() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(clientsFile, false));

            for (Client client : this.clients) {
                printWriter.println(client.getFileRecord());
            }

            printWriter.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void fetchAllClientsFromFile() {
        Scanner scanner;

        try {
            scanner = new Scanner(clientsFile);
            clients.clear();

            while(scanner.hasNext()) {
                String[] sections = scanner.nextLine().split(";", -1);

                clients.add(new Client(
                        UUID.fromString(sections[0]),
                        sections[1],
                        sections[2],
                        sections[3],
                        sections[4],
                        new Address(
                                sections[5],
                                sections[6],
                                sections[7],
                                sections[8],
                                sections[9],
                                sections[10],
                                sections[11]
                        ),
                        new Address(
                                sections[12],
                                sections[13],
                                sections[14],
                                sections[15],
                                sections[16],
                                sections[17],
                                sections[18]
                        )
                ));
            }
        }
        catch (FileNotFoundException e) {
            System.err.println(e.toString());
        }
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients);
    }

    public Client getClientById(UUID id) {
        for(Client client : clients) {
            if(client.id().equals(id)) {
                return client;
            }
        }

        return null;
    }

    public void addClient(Client client) {
        try {
            clients.add(client);

            saveAllClientsToFile();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void deleteClient(UUID id) {
        try {
            clients.removeIf(client -> client.id().equals(id));

            saveAllClientsToFile();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public boolean checkIfClientIsUsed(UUID id) {
        for(Order order : orders) {
            if(order.client().id().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private void saveAllItemsToFile() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(itemsFile, false));

            for (Item item : this.items) {
                printWriter.println(item.getFileRecord());
            }

            printWriter.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void fetchAllItemsFromFile() {
        Scanner scanner;

        try {
            scanner = new Scanner(itemsFile);
            items.clear();

            while(scanner.hasNext()) {
                String[] sections = scanner.nextLine().split(";", -1);

                items.add(new Item(
                        UUID.fromString(sections[0]),
                        sections[1],
                        sections[2],
                        sections[3],
                        new BigDecimal(sections[4]),
                        new BigDecimal(sections[5]),
                        sections[6],
                        sections[7]
                ));
            }
        }
        catch (FileNotFoundException e) {
            System.err.println(e.toString());
        }
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public List<Item> getItemsFilterName(String name) {
        List<Item> filteredItems = new ArrayList<>();

        for (Item item : items) {
            if(item.name().contains(name)) {
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }

    public Item getItemById(UUID id) {
        for(Item item : items) {
            if(item.id().equals(id)) {
                return item;
            }
        }

        return null;
    }

    public void addItem(Item item) {
        try {
            items.add(item);

            saveAllItemsToFile();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }

    public void deleteItem(UUID id) {
        try {
            items.removeIf(item -> item.id().equals(id));

            saveAllItemsToFile();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public boolean checkIfItemIsUsed(UUID id) {
        for(Order order : orders) {
            for (OrderItem orderItem : order.orderItems()) {
                if (orderItem.item().id().equals(id)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkIfItemSkuUnique(String sku) {
        for(Item item : items) {
            if (item.sku().equals(sku)) {
                return false;
            }
        }

        return true;
    }

    private void saveAllOrdersToFiles() {
        try {
            PrintWriter ordersWriter = new PrintWriter(
                    new FileWriter(ordersFile, false)
            );
            PrintWriter orderItemsWriter = new PrintWriter(
                    new FileWriter(orderItemsFile, false)
            );

            for (Order order : orders) {
                ordersWriter.println(order.getFileRecord());

                for (OrderItem orderItem : order.orderItems()) {
                    orderItemsWriter.println(
                            orderItem.getFileRecord(order.id())
                    );
                }
            }

            ordersWriter.close();
            orderItemsWriter.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void fetchAllOrdersFromFile() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        try {
            Scanner orderScanner = new Scanner(ordersFile);
            orders.clear();

            while(orderScanner.hasNext()) {
                String[] orderSections = orderScanner.nextLine().split(";", -1);

                List<OrderItem> orderItems = new ArrayList<>();
                Client client = getClientById(UUID.fromString(orderSections[4]));

                orders.add(new Order(
                        UUID.fromString(orderSections[0]),
                        dateFormat.parse(orderSections[1]),
                        orderItems,
                        new BigDecimal(orderSections[2]),
                        new BigDecimal(orderSections[3]),
                        client,
                        new Address(
                                orderSections[5],
                                orderSections[6],
                                orderSections[7],
                                orderSections[8],
                                orderSections[9],
                                orderSections[10],
                                orderSections[11]
                        )
                ));
            }

            Scanner orderItemScanner = new Scanner(orderItemsFile);

            while(orderItemScanner.hasNext()) {
                String[] orderItemSections = orderItemScanner.nextLine().split(";", -1);

                Order order = getOrderById(UUID.fromString(orderItemSections[1]));

                if(order == null) {
                    continue;
                }

                Item item = getItemById(UUID.fromString(orderItemSections[2]));

                order.addOrderItem(new OrderItem(
                        UUID.fromString(orderItemSections[0]),
                        item,
                        Integer.parseInt(orderItemSections[3]),
                        Integer.parseInt(orderItemSections[4]),
                        new BigDecimal(orderItemSections[5]),
                        new BigDecimal(orderItemSections[6])
                ));
            }
        }
        catch (FileNotFoundException | ParseException e) {
            System.err.println(e.toString());
        }
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public List<Order> getOrdersFilterOrderedAtRange(Date from, Date to) {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            Date orderedAt = order.orderedAt();

            if(
                    (orderedAt.after(from) || orderedAt.equals(from)) &&
                    (orderedAt.before(to) || orderedAt.equals(to))
            ) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    public List<Order> getOrdersFilterGrossTotalRang(BigDecimal from, BigDecimal to) {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            if(
                    order.grossTotal().compareTo(from) >= 0 &&
                    order.grossTotal().compareTo(to) <= 0
            ) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    public List<Order> getOrdersFilterClient(Client client) {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            if(order.client().equals(client)) {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }

    public Order getOrderById(UUID id) {
        for(Order order : orders) {
            if(order.id().equals(id)) {
                return order;
            }
        }

        return null;
    }

    public void addOrder(Order order) {
        try {
            orders.add(order);

            saveAllOrdersToFiles();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void deleteOrder(UUID id) {
        try {
            orders.removeIf(order -> order.id().equals(id));

            saveAllOrdersToFiles();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
