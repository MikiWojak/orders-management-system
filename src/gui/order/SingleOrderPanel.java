package gui.order;

import data.GlobalData;
import gui.MainFrame;
import layout.SpringUtilities;
import model.Address;
import model.Client;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SingleOrderPanel extends JPanel {
    private final String SUBMIT_CONTROL = "submit";
    private final String BACK_CONTROL = "back";

    private final MainFrame mainFrame;
    private final GlobalData globalData;
    private final List<Address> addressList;

    private JLabel headerLabel;
    private JSpinner orderedAtDatePicker;
    private OrderItemsFormPanel orderItemsFormPanel;
    JFormattedTextField netTotalField;
    JFormattedTextField grossTotalField;
    private JComboBox<Client> clientComboBox;
    private JComboBox<Address> addressPresetComboBox;
    private JButton submitButton;

    public SingleOrderPanel(MainFrame mainFrame, GlobalData globalData) {
        this.mainFrame = mainFrame;
        this.globalData = globalData;

        addressList = new ArrayList<>();

        prepareGUI();
        runAddOrderMode();
    }

    private void prepareGUI() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());

        headerLabel = new JLabel("", JLabel.CENTER);

        headerPanel.add(headerLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new SpringLayout());

        JLabel orderedAtLabel = new JLabel("Ordered At");
        orderedAtDatePicker = new JSpinner();
        orderedAtDatePicker.setModel(
                new SpinnerDateModel(
                        new Date(),
                        null,
                        null,
                        java.util.Calendar.HOUR_OF_DAY)
        );
        orderedAtLabel.setLabelFor(orderedAtDatePicker);

        orderItemsFormPanel = new OrderItemsFormPanel(this, globalData);

        JPanel totalPricePanel = new JPanel();
        totalPricePanel.setLayout(new SpringLayout());

        JLabel netTotalLabel = new JLabel("Net Total", JLabel.TRAILING);
        netTotalField = new JFormattedTextField(new BigDecimal("0.00"));
        netTotalField.setEditable(false);
        netTotalLabel.setLabelFor(netTotalField);

        DefaultFormatter netTotalFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        netTotalFormatter.setValueClass(netTotalField.getValue().getClass());
        DefaultFormatterFactory netTotalFormatterFactory = new DefaultFormatterFactory(
                netTotalFormatter,
                netTotalFormatter,
                netTotalFormatter);
        netTotalField.setFormatterFactory(netTotalFormatterFactory);

        JLabel grossTotalLabel = new JLabel("Gross Total", JLabel.TRAILING);
        grossTotalField = new JFormattedTextField(new BigDecimal("0.00"));
        grossTotalField.setEditable(false);
        grossTotalLabel.setLabelFor(grossTotalField);

        DefaultFormatter grossTotalFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        grossTotalFormatter.setValueClass(grossTotalField.getValue().getClass());
        DefaultFormatterFactory grossTotalFormatterFactory = new DefaultFormatterFactory(
                grossTotalFormatter,
                grossTotalFormatter,
                grossTotalFormatter);
        grossTotalField.setFormatterFactory(grossTotalFormatterFactory);

        totalPricePanel.add(netTotalLabel);
        totalPricePanel.add(netTotalField);
        totalPricePanel.add(grossTotalLabel);
        totalPricePanel.add(grossTotalField);

        SpringUtilities.makeGrid(
                totalPricePanel,
                2, 2,
                0, 0,
                6, 6
        );

        JLabel clientLabel = new JLabel("Client");
        clientLabel.setLabelFor(clientComboBox);

        clientComboBox = new JComboBox<Client>();
        reloadClientsOptions();
        clientComboBox.addItemListener(new ClientOptionsActionListener());

        JLabel addressPresetLabel = new JLabel("Address (1. Address, 2. Delivery Address if valid)");
        addressPresetComboBox = new JComboBox<Address>();
        addressPresetLabel.setLabelFor(addressPresetComboBox);
        reloadAddressOptions();

        formPanel.add(orderedAtLabel);
        formPanel.add(orderedAtDatePicker);
        formPanel.add(orderItemsFormPanel);
        formPanel.add(totalPricePanel);
        formPanel.add(clientLabel);
        formPanel.add(clientComboBox);
        formPanel.add(addressPresetLabel);
        formPanel.add(addressPresetComboBox);

        SpringUtilities.makeCompactGrid(
                formPanel,
                8, 1,
                6, 6,
                6, 6
        );

        JScrollPane formScrollPane = new JScrollPane(
                formPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        submitButton = new JButton("Submit");
        submitButton.setActionCommand(SUBMIT_CONTROL);
        JButton backButton = new JButton("Back");
        backButton.setActionCommand(BACK_CONTROL);

        submitButton.addActionListener(new ButtonClickListener());
        backButton.addActionListener(new ButtonClickListener());

        controlPanel.add(submitButton);
        controlPanel.add(backButton);

        add(headerPanel, BorderLayout.PAGE_START);
        add(formScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private void showValidationMessageDialog(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void submitForm() {
        try {
            Date orderedAt = (Date) orderedAtDatePicker.getValue();
            List<OrderItem> orderItems = orderItemsFormPanel.getOrderItems();
            BigDecimal netTotal = orderItemsFormPanel.getNetTotal();
            BigDecimal grossTotal = orderItemsFormPanel.getGrossTotal();
            Client client = (Client) clientComboBox.getSelectedItem();
            Address deliveryAddress = (Address) addressPresetComboBox.getSelectedItem();

            if(!orderItemsFormPanel.areOrderItemsValid()) {
                showValidationMessageDialog(
                        "List of Items should not be empty.\n" +
                        "All rows should have selected Item."
                );

                return;
            }

            if(
                    client == null ||
                    deliveryAddress == null
            ) {
                showValidationMessageDialog("Fill all required fields.");

                return;
            }

            Order order = new Order(
                    orderedAt,
                    orderItems,
                    netTotal,
                    grossTotal,
                    client,
                    deliveryAddress
            );

            globalData.addOrder(order);

            JOptionPane.showMessageDialog(
                    null,
                    "Order has been added",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );

            mainFrame.openOrdersTablePanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error while adding the Order",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        orderedAtDatePicker.setValue(new Date());

        orderItemsFormPanel.clearForm();

        netTotalField.setValue(new BigDecimal("0.00"));
        grossTotalField.setValue(new BigDecimal("0.00"));

        reloadClientsOptions();
        reloadAddressOptions();
    }

    private void fillForm(Order order) {
        orderedAtDatePicker.setValue(order.orderedAt());

        orderItemsFormPanel.runShowOrderMode(order.orderItems());

        netTotalField.setValue(order.netTotal());
        grossTotalField.setValue(order.grossTotal());

        clientComboBox.setSelectedItem(order.client());
        addressPresetComboBox.setSelectedItem(order.deliveryAddress());
    }

    private void setFieldsEditable(boolean mode) {
        orderedAtDatePicker.setEnabled(mode);

        orderItemsFormPanel.setFieldsEditable(mode);

        clientComboBox.setEnabled(mode);
        addressPresetComboBox.setEnabled(mode);
    }

    private void setButtonsEnabled(boolean mode) {
        submitButton.setEnabled(mode);
    }

    private void reloadAddressOptions() {
        addressList.clear();

        Client selectedClient = (Client) clientComboBox.getSelectedItem();

        if(selectedClient != null) {
            addressList.add(selectedClient.address());

            if(selectedClient.deliveryAddress().isValid()) {
                addressList.add(selectedClient.deliveryAddress());
            }
        }

        addressPresetComboBox.setModel(
                new DefaultComboBoxModel<Address>(
                        addressList.toArray(new Address[0])
                )
        );
    }

    private void reloadClientsOptions() {
        List<Client> clientOptions = globalData.getClients();

        clientComboBox.setModel(
                new DefaultComboBoxModel<Client>(
                        clientOptions.toArray(new Client[0])
                )
        );
    }

    public void runAddOrderMode() {
        headerLabel.setText("Add Order");

        clearForm();
        setFieldsEditable(true);
        setButtonsEnabled(true);
    }

    public void runShowOrderMode(Order order) {
        headerLabel.setText("Order Details");

        fillForm(order);
        setFieldsEditable(false);
        setButtonsEnabled(false);
    }

    public void getTotalPrice() {
        BigDecimal netTotal = orderItemsFormPanel.getNetTotal();
        BigDecimal grossTotal = orderItemsFormPanel.getGrossTotal();

        netTotalField.setValue(netTotal);
        grossTotalField.setValue(grossTotal);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case SUBMIT_CONTROL:
                    submitForm();

                    break;
                case BACK_CONTROL:
                    mainFrame.openOrdersTablePanel();

                    break;
            }
        }
    }

    private class ClientOptionsActionListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            reloadAddressOptions();
        }
    }
}
