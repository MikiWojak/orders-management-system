package gui.order;

import data.GlobalData;
import layout.SpringUtilities;
import model.Item;
import model.OrderItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class OrderItemRow extends JPanel {
    private final String DELETE_ORDER_ITEM_CONTROL = "deleteOrderItem";

    private final OrderItemsFormPanel orderItemsFormPanel;
    private final GlobalData globalData;

    JComboBox<Item> itemComboBox;
    JSpinner countSpinner;
    JSpinner discountSpinner;
    JFormattedTextField netSumTextField;
    JFormattedTextField grossSumTextField;
    JButton deleteButton;

    public OrderItemRow(
            OrderItemsFormPanel orderItemsFormPanel,
            GlobalData globalData
    ) {
        this.orderItemsFormPanel = orderItemsFormPanel;
        this.globalData = globalData;

        prepareGUI();
        countNetSumAndGrossSum();
    }

    private void prepareGUI() {
        setLayout(new SpringLayout());

        JLabel itemLabel = new JLabel("Item", JLabel.CENTER);
        itemComboBox = new JComboBox<Item>();
        itemComboBox.addItemListener(new ItemOptionsActionListener());
        itemLabel.setLabelFor(itemComboBox);

        reloadItemsOptions();

        JLabel countLabel = new JLabel("Count", JLabel.CENTER);
        countSpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, null, 1)
        );
        countLabel.setLabelFor(countSpinner);
        countSpinner.addChangeListener(new SumChangeListener());

        JLabel discountLabel = new JLabel(
                "<html>Discount in %<br/>(optional)</html>",
                JLabel.CENTER
        );
        discountSpinner = new JSpinner(
                new SpinnerNumberModel(0, 0, 100, 1)
        );
        discountLabel.setLabelFor(discountSpinner);
        discountSpinner.addChangeListener(new SumChangeListener());

        JLabel netSumLabel = new JLabel("Net Sum", JLabel.CENTER);
        netSumTextField = new JFormattedTextField(new BigDecimal("0.00"));
        netSumTextField.setEditable(false);
        netSumLabel.setLabelFor(netSumTextField);

        DefaultFormatter netSumFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        netSumFormatter.setValueClass(netSumTextField.getValue().getClass());
        DefaultFormatterFactory netSumFormatterFactory = new DefaultFormatterFactory(
                netSumFormatter,
                netSumFormatter,
                netSumFormatter);
        netSumTextField.setFormatterFactory(netSumFormatterFactory);

        JLabel grossSumLabel = new JLabel("Gross Sum", JLabel.CENTER);
        grossSumTextField = new JFormattedTextField(new BigDecimal("0.00"));
        grossSumTextField.setEditable(false);
        grossSumLabel.setLabelFor(grossSumTextField);

        DefaultFormatter grossSumFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        netSumFormatter.setValueClass(grossSumTextField.getValue().getClass());
        DefaultFormatterFactory grossSumFormatterFactory = new DefaultFormatterFactory(
                grossSumFormatter,
                grossSumFormatter,
                grossSumFormatter);
        grossSumTextField.setFormatterFactory(grossSumFormatterFactory);

        JLabel actionsLabel = new JLabel("", JLabel.CENTER);
        deleteButton = new JButton("Delete");
        deleteButton.setActionCommand(DELETE_ORDER_ITEM_CONTROL);
        deleteButton.addActionListener(new ButtonClickListener());
        actionsLabel.setLabelFor(deleteButton);

        add(itemLabel);
        add(countLabel);
        add(discountLabel);
        add(netSumLabel);
        add(grossSumLabel);
        add(actionsLabel);

        add(itemComboBox);
        add(countSpinner);
        add(discountSpinner);
        add(netSumTextField);
        add(grossSumTextField);
        add(deleteButton);

        SpringUtilities.makeCompactGrid(
                this,
                2, 6,
                0, 0,
                6, 6
        );
    }

    private void reloadItemsOptions() {
        List<Item> itemOptions = globalData.getItems();

        itemComboBox.setModel(
                new DefaultComboBoxModel<Item>(
                        itemOptions.toArray(new Item[0])
                )
        );
    }

    private void countNetSumAndGrossSum() {
        Item item = (Item) itemComboBox.getSelectedItem();

        if(item == null) {
            return;
        }

        BigDecimal unitNetPrice = item.unitNetPrice();
        BigDecimal unitGrossPrice = item.unitGrossPrice();

        int count = (int) countSpinner.getValue();
        int discount = (int) discountSpinner.getValue();

        BigDecimal discountPercentage = BigDecimal.valueOf(discount);
        discountPercentage = discountPercentage.setScale(2, RoundingMode.FLOOR);
        discountPercentage = discountPercentage.divide(new BigDecimal("100.00"), RoundingMode.FLOOR);

        BigDecimal netSumBeforeDiscount = unitNetPrice.multiply(BigDecimal.valueOf(count));
        BigDecimal netSumToSubtract = netSumBeforeDiscount.multiply(discountPercentage);
        BigDecimal netSum = netSumBeforeDiscount.subtract(netSumToSubtract);
        netSum = netSum.setScale(2, RoundingMode.CEILING);

        BigDecimal grossSumBeforeDiscount = unitGrossPrice.multiply(BigDecimal.valueOf(count));
        BigDecimal grossSumToSubtract = grossSumBeforeDiscount.multiply(discountPercentage);
        BigDecimal grossSum = grossSumBeforeDiscount.subtract(grossSumToSubtract);
        grossSum = grossSum.setScale(2, RoundingMode.CEILING);

        netSumTextField.setValue(netSum);
        grossSumTextField.setValue(grossSum);

        orderItemsFormPanel.countAndSetTotalPrice();
    }

    public OrderItem getOrderItem() {
        Item item = (Item) itemComboBox.getSelectedItem();
        int count = (int) countSpinner.getValue();
        int discount = (int) discountSpinner.getValue();
        BigDecimal netSum = (BigDecimal) netSumTextField.getValue();
        BigDecimal grossSum = (BigDecimal) grossSumTextField.getValue();

        return new OrderItem(
                item,
                count,
                discount,
                netSum,
                grossSum
        );
    }

    public void runShowOrderMode(OrderItem orderItem) {
        itemComboBox.setSelectedItem(orderItem.item());
        countSpinner.setValue(orderItem.count());
        discountSpinner.setValue(orderItem.discount());
        netSumTextField.setValue(orderItem.netSum());
        grossSumTextField.setValue(orderItem.grossSum());
    }

    public void setFieldsAndButtonsActive(boolean mode) {
        itemComboBox.setEnabled(mode);
        countSpinner.setEnabled(mode);
        discountSpinner.setEnabled(mode);

        deleteButton.setEnabled(mode);
    }

    public BigDecimal getNetSum() {
        return (BigDecimal) netSumTextField.getValue();
    }

    public BigDecimal getGrossSum() {
        return (BigDecimal) grossSumTextField.getValue();
    }

    public boolean isOrderItemValid() {
        Item item = (Item) itemComboBox.getSelectedItem();

        return item != null;
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals(DELETE_ORDER_ITEM_CONTROL)) {
                orderItemsFormPanel.deleteOrderItemRow(OrderItemRow.this);

                orderItemsFormPanel.countAndSetTotalPrice();
            }
        }
    }

    private class ItemOptionsActionListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            countNetSumAndGrossSum();
        }
    }

    private class SumChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            countNetSumAndGrossSum();
        }
    }
}
