package gui.client;

import data.GlobalData;
import gui.MainFrame;
import gui.common.AddressFormPanel;
import layout.SpringUtilities;
import model.Address;
import model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SingleClientPanel extends JPanel {
    private final String SUBMIT_CONTROL = "submit";
    private final String BACK_CONTROL = "back";

    private final MainFrame mainFrame;
    private final GlobalData globalData;

    private JLabel headerLabel;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField companyNameTextField;
    private JTextField tinTextField;
    private AddressFormPanel addressFormPanel;
    private AddressFormPanel deliveryAddressFormPanel;
    private JButton submitButton;

    public SingleClientPanel(MainFrame mainFrame, GlobalData globalData) {
        this.mainFrame = mainFrame;
        this.globalData = globalData;

        prepareGUI();
        runAddClientMode();
    }

    private void prepareGUI() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());

        headerLabel = new JLabel("", JLabel.CENTER);

        headerPanel.add(headerLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new SpringLayout());

        JPanel basicDataPanel = new JPanel();
        basicDataPanel.setLayout(new SpringLayout());

        JLabel firstNameLabel = new JLabel("First Name");
        firstNameTextField = new JTextField();
        firstNameLabel.setLabelFor(firstNameTextField);

        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameTextField = new JTextField();
        lastNameLabel.setLabelFor(lastNameTextField);

        JLabel companyNameLabel = new JLabel("Company Name (optional)");
        companyNameTextField = new JTextField();
        companyNameLabel.setLabelFor(companyNameTextField);

        JLabel tinLabel = new JLabel("TIN (optional)");
        tinTextField = new JTextField();
        tinLabel.setLabelFor(tinTextField);

        basicDataPanel.add(firstNameLabel);
        basicDataPanel.add(firstNameTextField);
        basicDataPanel.add(lastNameLabel);
        basicDataPanel.add(lastNameTextField);
        basicDataPanel.add(companyNameLabel);
        basicDataPanel.add(companyNameTextField);
        basicDataPanel.add(tinLabel);
        basicDataPanel.add(tinTextField);

        SpringUtilities.makeCompactGrid(
                basicDataPanel,
                8, 1,
                0, 0,
                6, 6
        );

        addressFormPanel = new AddressFormPanel();

        deliveryAddressFormPanel = new AddressFormPanel(
                "Delivery Address (optional)",
                "Street (optional)",
                "House Number (optional)",
                "Apartment Number (optional)",
                "City (optional)",
                "Zip Code (optional)",
                "District (optional)",
                "Country (optional)"
        );

        formPanel.add(basicDataPanel);
        formPanel.add(addressFormPanel);
        formPanel.add(deliveryAddressFormPanel);

        SpringUtilities.makeCompactGrid(
                formPanel,
                3, 1,
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
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String companyName = companyNameTextField.getText();
            String tin = tinTextField.getText();

            Address address = addressFormPanel.getAddress();
            Address deliveryAddress = deliveryAddressFormPanel.getAddress();

            if(
                    firstName.isEmpty() ||
                    lastName.isEmpty() ||
                    !address.isValid()
            ) {
                showValidationMessageDialog("Fill all required fields.");

                return;
            }

            Client newClient = new Client(
                    firstName,
                    lastName,
                    companyName,
                    tin,
                    address,
                    deliveryAddress
            );

            globalData.addClient(newClient);

            JOptionPane.showMessageDialog(
                    null,
                    "Client has been added",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE
            );

            mainFrame.openClientsTablePanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error while adding the Client",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        companyNameTextField.setText("");
        tinTextField.setText("");

        addressFormPanel.clearForm();
        deliveryAddressFormPanel.clearForm();
    }

    private void fillForm(Client client) {
        firstNameTextField.setText(client.firstName());
        lastNameTextField.setText(client.lastName());
        companyNameTextField.setText(client.companyName());
        tinTextField.setText(client.tin());

        addressFormPanel.fillForm(client.address());
        deliveryAddressFormPanel.fillForm(client.deliveryAddress());
    }

    private void setFieldsEditable(boolean mode) {
        firstNameTextField.setEditable(mode);
        lastNameTextField.setEditable(mode);
        companyNameTextField.setEditable(mode);
        tinTextField.setEditable(mode);

        addressFormPanel.setFieldsEditable(mode);
        deliveryAddressFormPanel.setFieldsEditable(mode);
    }

    private void setButtonsEnabled(boolean mode) {
        submitButton.setEnabled(mode);
    }

    public void runAddClientMode() {
        headerLabel.setText("Add Client");

        clearForm();
        setFieldsEditable(true);
        setButtonsEnabled(true);
    }

    public void runShowClientMode(Client client) {
        headerLabel.setText("Client Details");

        fillForm(client);
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
                    mainFrame.openClientsTablePanel();

                    break;
            }
        }
    }
}
