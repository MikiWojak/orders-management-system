package gui.common;

import layout.SpringUtilities;
import model.Address;

import javax.swing.*;
import java.awt.*;

public class AddressFormPanel extends JPanel {
    private JTextField streetNameTextField;
    private JTextField houseNumberTextField;
    private JTextField apartmentNumberTextField;
    private JTextField cityTextField;
    private JTextField zipCodeTextField;
    private JTextField districtTextField;
    private JTextField countryTextField;

    public AddressFormPanel() {
        prepareGUI(
                "Address",
                "Street",
                "House Number",
                "Apartment Number (optional)",
                "City",
                "Zip Code",
                "District",
                "Country"
        );
    }

    public AddressFormPanel(
            String formLabelText,
            String streetNameLabelText,
            String houseNumberLabelText,
            String apartmentNumberLabelText,
            String cityLabelText,
            String zipCodeLabelText,
            String districtLabelText,
            String countryLabelText
    ) {
        prepareGUI(
                formLabelText,
                streetNameLabelText,
                houseNumberLabelText,
                apartmentNumberLabelText,
                cityLabelText,
                zipCodeLabelText,
                districtLabelText,
                countryLabelText
        );
    }

    private void prepareGUI(
            String formLabelText,
            String streetNameLabelText,
            String houseNumberLabelText,
            String apartmentNumberLabelText,
            String cityLabelText,
            String zipCodeLabelText,
            String districtLabelText,
            String countryLabelText
    ) {
        setLayout(new SpringLayout());

        JLabel formLabel = new JLabel(formLabelText, JLabel.CENTER);
        formLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel streetNameLabel = new JLabel(streetNameLabelText);
        streetNameTextField = new JTextField();
        streetNameLabel.setLabelFor(streetNameTextField);

        JLabel houseNumberLabel = new JLabel(houseNumberLabelText);
        houseNumberTextField = new JTextField();
        houseNumberLabel.setLabelFor(houseNumberTextField);

        JLabel apartmentNumberLabel = new JLabel(apartmentNumberLabelText);
        apartmentNumberTextField = new JTextField();
        apartmentNumberLabel.setLabelFor(apartmentNumberTextField);

        JLabel cityLabel = new JLabel(cityLabelText);
        cityTextField = new JTextField();
        cityLabel.setLabelFor(cityTextField);

        JLabel zipCodeLabel = new JLabel(zipCodeLabelText);
        zipCodeTextField = new JTextField();
        zipCodeLabel.setLabelFor(zipCodeTextField);

        JLabel districtLabel = new JLabel(districtLabelText);
        districtTextField = new JTextField();
        districtLabel.setLabelFor(districtTextField);

        JLabel countryLabel = new JLabel(countryLabelText);
        countryTextField = new JTextField();
        countryLabel.setLabelFor(countryTextField);

        add(formLabel);
        add(streetNameLabel);
        add(streetNameTextField);
        add(houseNumberLabel);
        add(houseNumberTextField);
        add(apartmentNumberLabel);
        add(apartmentNumberTextField);
        add(cityLabel);
        add(cityTextField);
        add(zipCodeLabel);
        add(zipCodeTextField);
        add(districtLabel);
        add(districtTextField);
        add(countryLabel);
        add(countryTextField);

        SpringUtilities.makeCompactGrid(
                this,
                15, 1,
                0, 0,
                6, 6
        );
    }

    public void clearForm() {
        streetNameTextField.setText("");
        houseNumberTextField.setText("");
        apartmentNumberTextField.setText("");
        cityTextField.setText("");
        zipCodeTextField.setText("");
        districtTextField.setText("");
        countryTextField.setText("");
    }

    public void fillForm(Address address) {
        streetNameTextField.setText(address.street());
        houseNumberTextField.setText(address.houseNumber());
        apartmentNumberTextField.setText(address.apartmentNumber());
        cityTextField.setText(address.city());
        zipCodeTextField.setText(address.zipCode());
        districtTextField.setText(address.district());
        countryTextField.setText(address.country());
    }

    public void setFieldsEditable(boolean mode) {
        streetNameTextField.setEditable(mode);
        houseNumberTextField.setEditable(mode);
        apartmentNumberTextField.setEditable(mode);
        cityTextField.setEditable(mode);
        zipCodeTextField.setEditable(mode);
        districtTextField.setEditable(mode);
        countryTextField.setEditable(mode);
    }

    public Address getAddress() {
        return new Address(
                streetNameTextField.getText(),
                houseNumberTextField.getText(),
                apartmentNumberTextField.getText(),
                cityTextField.getText(),
                zipCodeTextField.getText(),
                districtTextField.getText(),
                countryTextField.getText()
        );
    }
}
