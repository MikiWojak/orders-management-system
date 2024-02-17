package gui.order;

import data.GlobalData;
import layout.SpringUtilities;
import model.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsFormPanel extends JPanel {
    private final String ADD_ORDER_ITEM_CONTROL = "addOrderItem";

    private final SingleOrderPanel singleOrderPanel;
    private final GlobalData globalData;

    private List<OrderItemRow> orderItemRowList;

    JPanel orderItemsPanel;
    JButton addButton;

    public OrderItemsFormPanel(SingleOrderPanel singleOrderPanel, GlobalData globalData) {
        this.singleOrderPanel = singleOrderPanel;
        this.globalData = globalData;

        prepareGUI();
    }

    private void prepareGUI() {
        orderItemRowList = new ArrayList<>();

        setLayout(new BorderLayout());

        JLabel formLabel = new JLabel("List of Items", JLabel.CENTER);
        formLabel.setFont(new Font("Arial", Font.BOLD, 12));

        orderItemsPanel = new JPanel();
        orderItemsPanel.setLayout(new SpringLayout());

        reloadOrderItemsPanel();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        addButton = new JButton("Add Item");
        addButton.setActionCommand(ADD_ORDER_ITEM_CONTROL);
        addButton.addActionListener(new ButtonClickListener());

        controlPanel.add(addButton);

        add(formLabel, BorderLayout.PAGE_START);
        add(orderItemsPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private void addOrderItem() {
        orderItemRowList.add(
                new OrderItemRow(this, globalData)
        );

        reloadOrderItemsPanel();
    }

    public void clearForm() {
        orderItemRowList.clear();

        reloadOrderItemsPanel();
    }

    public void runShowOrderMode(List<OrderItem> orderItems) {
        orderItemRowList.clear();

        for(OrderItem orderItem : orderItems) {
            OrderItemRow orderItemRow = new OrderItemRow(this, globalData);
            orderItemRow.runShowOrderMode(orderItem);

            orderItemRowList.add(orderItemRow);
        }

        reloadOrderItemsPanel();
    }

    private void reloadOrderItemsPanel() {
        orderItemsPanel.removeAll();

        for(OrderItemRow row : orderItemRowList) {
            orderItemsPanel.add(row);
        }

        int rowsCount = orderItemRowList.size();

        SpringUtilities.makeCompactGrid(
                orderItemsPanel,
                rowsCount, 1,
                0, 0,
                6, 6
        );

        orderItemsPanel.repaint();
        orderItemsPanel.revalidate();
    }

    public void deleteOrderItemRow(OrderItemRow orderItemRow) {
        orderItemRowList.remove(orderItemRow);

        reloadOrderItemsPanel();
    }

    public List<OrderItem> getOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemRow row : orderItemRowList) {
            orderItems.add(row.getOrderItem());
        }

        return orderItems;
    }

    public void setFieldsEditable(boolean mode) {
        for(OrderItemRow row : orderItemRowList) {
            row.setFieldsAndButtonsActive(mode);
        }

        addButton.setEnabled(mode);
    }

    public BigDecimal getNetTotal() {
        BigDecimal netTotal = new BigDecimal("0.00");

        for(OrderItemRow orderItemRow : orderItemRowList) {
            netTotal = netTotal.add(orderItemRow.getNetSum());
        }

        return netTotal;
    }

    public BigDecimal getGrossTotal() {
        BigDecimal grossTotal = new BigDecimal("0.00");

        for(OrderItemRow orderItemRow : orderItemRowList) {
            grossTotal = grossTotal.add(orderItemRow.getGrossSum());
        }

        return grossTotal;
    }

    public void countAndSetTotalPrice() {
        singleOrderPanel.getTotalPrice();
    }

    public boolean areOrderItemsValid() {
        if(orderItemRowList.isEmpty()) {
            return false;
        }

        for(OrderItemRow orderItemRow : orderItemRowList) {
            if(!orderItemRow.isOrderItemValid()) {
                return false;
            }
        }

        return true;
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals(ADD_ORDER_ITEM_CONTROL)) {
                addOrderItem();

                countAndSetTotalPrice();
            }
        }
    }
}
