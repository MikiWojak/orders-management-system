package gui;

import data.GlobalData;
import gui.client.ClientsTablePanel;
import gui.client.SingleClientPanel;
import gui.item.ItemsTablePanel;
import gui.item.SingleItemPanel;
import gui.order.OrdersTablePanel;
import gui.order.SingleOrderPanel;
import model.Client;
import model.Item;
import model.Order;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final String CLIENTS_TABLE_PANEL = "clientsTablePanel";
    private final String SINGLE_CLIENT_PANEL = "singleClientPanel";
    private final String ITEMS_TABLE_PANEL = "itemsTablePanel";
    private final String SINGLE_ITEM_PANEL = "singleItemPanel";
    private final String ORDERS_TABLE_PANEL = "ordersTablePanel";
    private final String SINGLE_ORDER_PANEL = "singleOrderPanel";

    private final GlobalData globalData;

    private JPanel containerPanel;
    private CardLayout containerCardLayout;
    private ClientsTablePanel clientsTablePanel;
    private SingleClientPanel singleClientPanel;
    private ItemsTablePanel itemsTablePanel;
    private SingleItemPanel singleItemPanel;
    private OrdersTablePanel ordersTablePanel;
    private SingleOrderPanel singleOrderPanel;

    public MainFrame(){
        globalData = new GlobalData();

        this.prepareGUI();
        this.showFrame();
    }
    private void prepareGUI(){
        setTitle("Orders Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        setLayout(new GridLayout(1, 1));

        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");

        JMenuItem clientsMenuItem = new JMenuItem("Clients");
        clientsMenuItem.setActionCommand(CLIENTS_TABLE_PANEL);
        JMenuItem itemsMenuItem = new JMenuItem("Items");
        itemsMenuItem.setActionCommand(ITEMS_TABLE_PANEL);
        JMenuItem ordersMenuItem = new JMenuItem("Orders");
        ordersMenuItem.setActionCommand(ORDERS_TABLE_PANEL);

        containerCardLayout = new CardLayout(0, 0);
        containerPanel = new JPanel();
        containerPanel.setLayout(containerCardLayout);

        clientsTablePanel = new ClientsTablePanel(this, globalData);
        singleClientPanel = new SingleClientPanel(this, globalData);
        itemsTablePanel = new ItemsTablePanel(this, globalData);
        singleItemPanel = new SingleItemPanel(this, globalData);
        ordersTablePanel = new OrdersTablePanel(this, globalData);
        singleOrderPanel = new SingleOrderPanel(this, globalData);

        clientsMenuItem.addActionListener(new MenuClickListener());
        itemsMenuItem.addActionListener(new MenuClickListener());
        ordersMenuItem.addActionListener(new MenuClickListener());

        viewMenu.add(clientsMenuItem);
        viewMenu.add(itemsMenuItem);
        viewMenu.add(ordersMenuItem);
        menuBar.add(viewMenu);

        containerPanel.add(clientsTablePanel, CLIENTS_TABLE_PANEL);
        containerPanel.add(singleClientPanel, SINGLE_CLIENT_PANEL);
        containerPanel.add(itemsTablePanel, ITEMS_TABLE_PANEL);
        containerPanel.add(singleItemPanel, SINGLE_ITEM_PANEL);
        containerPanel.add(ordersTablePanel, ORDERS_TABLE_PANEL);
        containerPanel.add(singleOrderPanel, SINGLE_ORDER_PANEL);

        setJMenuBar(menuBar);
        add(containerPanel);
    }

    private void showFrame(){
        setVisible(true);
    }

    public void openClientsTablePanel() {
        clientsTablePanel.reloadPanel();
        containerCardLayout.show(containerPanel, CLIENTS_TABLE_PANEL);
    }

    public void openShowSingleClientPanel(Client client) {
        singleClientPanel.runShowClientMode(client);
        containerCardLayout.show(containerPanel, SINGLE_CLIENT_PANEL);
    }

    public void openAddSingleClientPanel() {
        singleClientPanel.runAddClientMode();
        containerCardLayout.show(containerPanel, SINGLE_CLIENT_PANEL);
    }

    public void openItemsTablePanel() {
        itemsTablePanel.reloadPanel();
        containerCardLayout.show(containerPanel, ITEMS_TABLE_PANEL);
    }

    public void openShowSingleItemPanel(Item item) {
        singleItemPanel.runShowItemMode(item);
        containerCardLayout.show(containerPanel, SINGLE_ITEM_PANEL);
    }

    public void openAddSingleItemPanel() {
        singleItemPanel.runAddItemMode();
        containerCardLayout.show(containerPanel, SINGLE_ITEM_PANEL);
    }

    public void openOrdersTablePanel() {
        ordersTablePanel.reloadPanel();
        containerCardLayout.show(containerPanel, ORDERS_TABLE_PANEL);
    }

    public void openShowSingleOrderPanel(Order order) {
        singleOrderPanel.runShowOrderMode(order);
        containerCardLayout.show(containerPanel, SINGLE_ORDER_PANEL);
    }

    public void openAddSingleOrderPanel() {
        singleOrderPanel.runAddOrderMode();
        containerCardLayout.show(containerPanel, SINGLE_ORDER_PANEL);
    }

    private class MenuClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case CLIENTS_TABLE_PANEL:
                    openClientsTablePanel();

                    break;
                case ITEMS_TABLE_PANEL:
                    openItemsTablePanel();

                    break;
                case ORDERS_TABLE_PANEL:
                    openOrdersTablePanel();

                    break;
            }
        }
    }
}
