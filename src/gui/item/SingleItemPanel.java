package gui.item;

import data.GlobalData;
import gui.MainFrame;
import layout.SpringUtilities;
import model.Item;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SingleItemPanel extends JPanel {
    private final String SUBMIT_CONTROL = "submit";
    private final String BACK_CONTROL = "back";

    private final MainFrame mainFrame;
    private final GlobalData globalData;

    private JLabel headerLabel;
    private JTextField nameTextField;
    private JTextArea descriptionTextArea;
    private JTextField skuTextField;
    private JFormattedTextField unitNetPriceTextField;
    private JFormattedTextField unitGrossPriceTextField;
    private JTextField dimensionsTextField;
    private JTextField weightTextField;
    private JButton submitButton;

    public SingleItemPanel(MainFrame mainFrame, GlobalData globalData) {
        this.mainFrame = mainFrame;
        this.globalData = globalData;

        prepareGUI();
        runAddItemMode();
    }

    private void prepareGUI() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());

        headerLabel = new JLabel("", JLabel.CENTER);

        headerPanel.add(headerLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new SpringLayout());

        JLabel nameLabel = new JLabel("Name");
        nameTextField = new JTextField();
        nameLabel.setLabelFor(nameTextField);

        JLabel descriptionLabel = new JLabel("Description (optional)");
        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionLabel.setLabelFor(descriptionTextArea);

        JLabel skuLabel = new JLabel("SKU");
        skuTextField = new JTextField();
        skuLabel.setLabelFor(skuTextField);

        JLabel unitNetPriceLabel = new JLabel("Unit Net Price");
        unitNetPriceTextField = new JFormattedTextField(new BigDecimal("0.00"));
        unitNetPriceLabel.setLabelFor(unitNetPriceTextField);

        DefaultFormatter unitNetPriceFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        unitNetPriceFormatter.setValueClass(unitNetPriceTextField.getValue().getClass());
        DefaultFormatterFactory unitNetPriceFormatterFactory = new DefaultFormatterFactory(
                unitNetPriceFormatter,
                unitNetPriceFormatter,
                unitNetPriceFormatter);
        unitNetPriceTextField.setFormatterFactory(unitNetPriceFormatterFactory);

        JLabel unitGrossPriceLabel = new JLabel("Unit Gross Price");
        unitGrossPriceTextField = new JFormattedTextField(new BigDecimal("0.00"));
        unitGrossPriceLabel.setLabelFor(unitGrossPriceTextField);

        DefaultFormatter unitGrossPriceFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        unitGrossPriceFormatter.setValueClass(unitGrossPriceTextField.getValue().getClass());
        DefaultFormatterFactory unitGrossPriceFormatterFactory = new DefaultFormatterFactory(
                unitGrossPriceFormatter,
                unitGrossPriceFormatter,
                unitGrossPriceFormatter);
        unitGrossPriceTextField.setFormatterFactory(unitGrossPriceFormatterFactory);

        JLabel dimensionsLabel = new JLabel("Dimensions (optional)");
        dimensionsTextField = new JTextField();
        dimensionsLabel.setLabelFor(dimensionsTextField);

        JLabel weightLabel = new JLabel("Weight (optional)");
        weightTextField = new JTextField();
        weightLabel.setLabelFor(weightTextField);

        formPanel.add(nameLabel);
        formPanel.add(nameTextField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionTextArea);
        formPanel.add(skuLabel);
        formPanel.add(skuTextField);
        formPanel.add(unitNetPriceLabel);
        formPanel.add(unitNetPriceTextField);
        formPanel.add(unitGrossPriceLabel);
        formPanel.add(unitGrossPriceTextField);
        formPanel.add(dimensionsLabel);
        formPanel.add(dimensionsTextField);
        formPanel.add(weightLabel);
        formPanel.add(weightTextField);

        SpringUtilities.makeCompactGrid(
                formPanel,
                14, 1,
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
            String name = nameTextField.getText();
            String description = descriptionTextArea.getText();
            String sku = skuTextField.getText();
            BigDecimal unitNetPrice = (BigDecimal) unitNetPriceTextField.getValue();
            BigDecimal unitGrossPrice = (BigDecimal) unitGrossPriceTextField.getValue();
            String dimensions = dimensionsTextField.getText();
            String weight = weightTextField.getText();

            unitNetPrice = unitNetPrice.setScale(2, RoundingMode.CEILING);
            unitGrossPrice = unitGrossPrice.setScale(2, RoundingMode.CEILING);

            if(
                    name.isEmpty() ||
                    sku.isEmpty()
            ) {
                showValidationMessageDialog("Fill all required fields.");

                return;
            }

            if(!globalData.checkIfItemSkuUnique(sku)) {
                showValidationMessageDialog("SKU must be unique.");

                return;
            }

            if(
                    unitNetPrice.equals(new BigDecimal("0.00")) ||
                    unitGrossPrice.equals(new BigDecimal("0.00"))
            ) {
                showValidationMessageDialog("Net Price and Gross Price cannot be equal to 0.");

                return;
            }

            Item newItem = new Item(
                    name,
                    description,
                    sku,
                    unitNetPrice,
                    unitGrossPrice,
                    dimensions,
                    weight
            );

            globalData.addItem(newItem);

            JOptionPane.showMessageDialog(
                    null,
                    "Item has been added",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );

            mainFrame.openItemsTablePanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error while adding the Item",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        nameTextField.setText("");
        descriptionTextArea.setText("");
        skuTextField.setText("");
        unitNetPriceTextField.setValue(new BigDecimal("0.00"));
        unitGrossPriceTextField.setValue(new BigDecimal("0.00"));
        dimensionsTextField.setText("");
        weightTextField.setText("");
    }

    private void fillForm(Item item) {
        nameTextField.setText(item.name());
        descriptionTextArea.setText(item.description());
        skuTextField.setText(item.sku());
        unitNetPriceTextField.setValue(item.unitNetPrice());
        unitGrossPriceTextField.setValue(item.unitGrossPrice());
        dimensionsTextField.setText(item.dimensions());
        weightTextField.setText(item.weight());
    }

    private void setFieldsEditable(boolean mode) {
        nameTextField.setEditable(mode);
        descriptionTextArea.setEditable(mode);
        skuTextField.setEditable(mode);
        unitNetPriceTextField.setEditable(mode);
        unitGrossPriceTextField.setEditable(mode);
        dimensionsTextField.setEditable(mode);
        weightTextField.setEditable(mode);
    }

    private void setButtonsEnabled(boolean mode) {
        submitButton.setEnabled(mode);
    }

    public void runAddItemMode() {
        headerLabel.setText("Add Item");

        clearForm();
        setFieldsEditable(true);
        setButtonsEnabled(true);
    }

    public void runShowItemMode(Item item) {
        headerLabel.setText("Item Details");

        fillForm(item);
        setFieldsEditable(false);
        setButtonsEnabled(false);
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
                    mainFrame.openItemsTablePanel();

                    break;
            }
        }
    }
}
