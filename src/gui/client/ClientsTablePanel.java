package gui.client;

import data.GlobalData;
import gui.MainFrame;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

public class ClientsTablePanel extends JPanel {
    private final String ADD_CONTROL = "add";
    private final String SHOW_CONTROL = "show";
    private final String DELETE_CONTROL = "delete";

    private final MainFrame mainFrame;
    private final GlobalData globalData;

    private List<Client> clients;

    String[] columnNames = {
            "First Name",
            "Last Name",
            "Company Name",
            "TIN"
    };

    private DefaultTableModel clientsDefaultTableModel;
    private JTable clientsTable;

    public ClientsTablePanel(MainFrame mainFrame, GlobalData globalData) {
        this.mainFrame = mainFrame;
        this.globalData = globalData;

        prepareGUI();
        reloadPanel();
    }

    private void prepareGUI(){
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());

        JLabel headerLabel = new JLabel(
                "Clients",
                JLabel.CENTER);

        headerPanel.add(headerLabel);

        clientsDefaultTableModel = new DefaultTableModel(columnNames, 0);
        clientsTable = new JTable(clientsDefaultTableModel) {
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            };
        };
        JScrollPane clientsTableScrollPane = new JScrollPane(clientsTable);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton addButton = new JButton("Add");
        addButton.setActionCommand(ADD_CONTROL);
        addButton.addActionListener(new ButtonClickListener());

        JButton showButton = new JButton("Show");
        showButton.setActionCommand(SHOW_CONTROL);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setActionCommand(DELETE_CONTROL);

        showButton.addActionListener(new ButtonClickListener());
        deleteButton.addActionListener(new ButtonClickListener());

        controlPanel.add(addButton);
        controlPanel.add(showButton);
        controlPanel.add(deleteButton);

        add(headerPanel, BorderLayout.PAGE_START);
        add(clientsTableScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private void populateClientsToTable() {
        clientsDefaultTableModel.setRowCount(0);

        clients = globalData.getClients();

        for (Client client : clients) {
            Object[] rowData = new Object[4];

            rowData[0] = client.firstName();
            rowData[1] = client.lastName();
            rowData[2] = client.companyName();
            rowData[3] = client.tin();

            clientsDefaultTableModel.addRow(rowData);
        }
    }

    private void showWarningMessageDialog(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void openAddSingleClient() {
        mainFrame.openAddSingleClientPanel();
    }

    private void openShowSingleClient() {
        int index = clientsTable.getSelectedRow();

        if (index <= -1) {
            showWarningMessageDialog("Select Client.");

            return;
        }

        mainFrame.openShowSingleClientPanel(clients.get(index));
    }

    private void removeClient() {
        int index = clientsTable.getSelectedRow();

        if (index <= -1) {
            showWarningMessageDialog("Select Client.");

            return;
        }

        UUID id = clients.get(index).id();

        if(globalData.checkIfClientIsUsed(id)) {
            showWarningMessageDialog(
                    "Selected Client is being used.\n" +
                    "You cannot delete him/her."
            );

            return;
        }

        Object[] optionNames = {"Yes", "No"};

        int selectedOption = JOptionPane.showOptionDialog(
                null,
                "Do you really want to delete selected Client?",
                "Question",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionNames,
                optionNames[1]);

        if(selectedOption != 0) {
            return;
        }

        globalData.deleteClient(id);

        reloadPanel();
    }

    public void reloadPanel() {
        populateClientsToTable();
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case ADD_CONTROL:
                    openAddSingleClient();

                    break;
                case SHOW_CONTROL:
                    openShowSingleClient();

                    break;
                case DELETE_CONTROL:
                    removeClient();

                    break;
            }
        }
    }
}
