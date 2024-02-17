package gui.order;

import data.GlobalData;
import gui.MainFrame;
import layout.SpringUtilities;
import model.Client;
import model.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrdersTablePanel extends JPanel {
    private final String ADD_CONTROL = "add";
    private final String SHOW_CONTROL = "show";
    private final String DELETE_CONTROL = "delete";
    private final String FILTER_ORDERED_AT = "filterOrderedAt";
    private final String FILTER_GROSS_TOTAL = "filterGrossTotal";
    private final String FILTER_CLIENT = "filterClient";
    private final String SHOW_ALL = "showAll";

    private final MainFrame mainFrame;
    private final GlobalData globalData;

    private List<Order> orders;
    JSpinner filterDateFromInput;
    JSpinner filterDateToInput;
    JFormattedTextField grossTotalFromInput;
    JFormattedTextField grossTotalToInput;
    private JComboBox<Client> clientComboBox;

    String[] columnNames = {
            "Ordered At",
            "Client's First Name",
            "Client's Last Name",
            "Client's Company Name",
            "Client's TIN",
            "Net Total",
            "Gross Total"
    };

    private DefaultTableModel ordersDefaultTableModel;
    private JTable ordersTable;

    public OrdersTablePanel(MainFrame mainFrame, GlobalData globalData) {
        this.mainFrame = mainFrame;
        this.globalData = globalData;

        prepareGUI();
        reloadPanel();
    }

    private void prepareGUI(){
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout());

        JLabel headerLabel = new JLabel(
                "Orders",
                JLabel.CENTER);

        labelPanel.add(headerLabel);

        JPanel generalFiltersPanel = new JPanel();
        generalFiltersPanel.setLayout(new SpringLayout());

        filterDateFromInput = new JSpinner();
        filterDateFromInput.setModel(
                new SpinnerDateModel(
                        new Date(),
                        null,
                        null,
                        java.util.Calendar.HOUR_OF_DAY)
        );
        filterDateToInput = new JSpinner();
        filterDateToInput.setModel(
                new SpinnerDateModel(
                        new Date(),
                        null,
                        null,
                        java.util.Calendar.HOUR_OF_DAY)
        );

        JButton filterDateButton = new JButton("Filter by Ordered At Range");
        filterDateButton.setActionCommand(FILTER_ORDERED_AT);
        filterDateButton.addActionListener(new FilterListener());

        grossTotalFromInput = new JFormattedTextField(new BigDecimal("0.00"));

        DefaultFormatter grossTotalFromFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        grossTotalFromFormatter.setValueClass(grossTotalFromInput.getValue().getClass());
        DefaultFormatterFactory grossTotalFromFormatterFactory = new DefaultFormatterFactory(
                grossTotalFromFormatter,
                grossTotalFromFormatter,
                grossTotalFromFormatter);
        grossTotalFromInput.setFormatterFactory(grossTotalFromFormatterFactory);

        grossTotalToInput = new JFormattedTextField(new BigDecimal("0.00"));

        DefaultFormatter grossTotalToFormatter = new NumberFormatter(new DecimalFormat("####0.00"));
        grossTotalToFormatter.setValueClass(grossTotalToInput.getValue().getClass());
        DefaultFormatterFactory grossTotalToFormatterFactory = new DefaultFormatterFactory(
                grossTotalToFormatter,
                grossTotalToFormatter,
                grossTotalToFormatter);
        grossTotalToInput.setFormatterFactory(grossTotalToFormatterFactory);

        JButton filterGrossTotalButton = new JButton("Filter by GrossTotal Range");
        filterGrossTotalButton.setActionCommand(FILTER_GROSS_TOTAL);
        filterGrossTotalButton.addActionListener(new FilterListener());

        generalFiltersPanel.add(filterDateFromInput);
        generalFiltersPanel.add(filterDateToInput);
        generalFiltersPanel.add(filterDateButton);
        generalFiltersPanel.add(grossTotalFromInput);
        generalFiltersPanel.add(grossTotalToInput);
        generalFiltersPanel.add(filterGrossTotalButton);

        SpringUtilities.makeGrid(
                generalFiltersPanel,
                2, 3,
                0, 0,
                6, 6
        );

        JPanel clientFilterPanel = new JPanel();
        clientFilterPanel.setLayout(new SpringLayout());

        clientComboBox = new JComboBox<Client>();

        JButton filterClientButton = new JButton("Filter by selected Client");
        filterClientButton.setActionCommand(FILTER_CLIENT);
        filterClientButton.addActionListener(new FilterListener());

        clientFilterPanel.add(clientComboBox);
        clientFilterPanel.add(filterClientButton);

        SpringUtilities.makeGrid(
                clientFilterPanel,
                1, 2,
                0, 0,
                6, 6
        );

        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new SpringLayout());

        filtersPanel.add(generalFiltersPanel);
        filtersPanel.add(clientFilterPanel);

        SpringUtilities.makeCompactGrid(
                filtersPanel,
                2, 1,
                6, 0,
                6, 6
        );

        JPanel showAllPanel = new JPanel();
        showAllPanel.setLayout(new FlowLayout());

        JButton showAllButton = new JButton("Show All");
        showAllButton.setActionCommand(SHOW_ALL);
        showAllButton.addActionListener(new FilterListener());

        showAllPanel.add(showAllButton);

        headerPanel.add(headerLabel, BorderLayout.PAGE_START);
        headerPanel.add(filtersPanel, BorderLayout.CENTER);
        headerPanel.add(showAllPanel, BorderLayout.PAGE_END);

        ordersDefaultTableModel = new DefaultTableModel(columnNames, 0);
        ordersTable = new JTable(ordersDefaultTableModel) {
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            };
        };
        JScrollPane ordersTableScrollPane = new JScrollPane(ordersTable);

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
        add(ordersTableScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private void populateOrdersToTable() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

        ordersDefaultTableModel.setRowCount(0);

        for (Order order : orders) {
            Object[] rowData = new Object[7];

            rowData[0] = dateFormat.format((order.orderedAt()));
            rowData[1] = order.client().firstName();
            rowData[2] = order.client().lastName();
            rowData[3] = order.client().companyName();
            rowData[4] = order.client().tin();
            rowData[5] = order.netTotal();
            rowData[6] = order.grossTotal();

            ordersDefaultTableModel.addRow(rowData);
        }
    }

    private void showSelectOrderMessageDialog() {
        JOptionPane.showMessageDialog(
                null,
                "Select Order.",
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void openAddSingleOrder() {
        mainFrame.openAddSingleOrderPanel();
    }

    private void openShowSingleOrder() {
        int index = ordersTable.getSelectedRow();

        if (index <= -1) {
            showSelectOrderMessageDialog();

            return;
        }

        mainFrame.openShowSingleOrderPanel(orders.get(index));
    }

    private void removeOrder() {
        int index = ordersTable.getSelectedRow();

        if (index <= -1) {
            showSelectOrderMessageDialog();

            return;
        }

        Object[] optionNames = {"Yes", "No"};

        int selectedOption = JOptionPane.showOptionDialog(
                null,
                "Do you really want to delete selected Order?",
                "Question",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionNames,
                optionNames[1]);

        if(selectedOption != 0) {
            return;
        }

        globalData.deleteOrder(orders.get(index).id());

        reloadPanel();
    }

    public void reloadPanel() {
        getAndShowAllOrders();
        resetFilters();
    }

    private void reloadClientsOptions() {
        List<Client> clientOptions = globalData.getClients();

        clientComboBox.setModel(
                new DefaultComboBoxModel<Client>(
                        clientOptions.toArray(new Client[0])
                )
        );
    }

    private void getAndShowAllOrders() {
        orders = globalData.getOrders();

        populateOrdersToTable();
    }

    private void getAndShowOrdersFilterOrderedAtRange(Date from, Date to) {
        orders = globalData.getOrdersFilterOrderedAtRange(from, to);

        populateOrdersToTable();
    }

    private void getAndShowOrdersFilterGrossTotalRange(BigDecimal from, BigDecimal to) {
        orders = globalData.getOrdersFilterGrossTotalRang(from, to);

        populateOrdersToTable();
    }

    private void getAndShowOrdersFilterClient(Client client) {
        orders = globalData.getOrdersFilterClient(client);

        populateOrdersToTable();
    }

    private void resetFilters() {
        filterDateFromInput.setValue(new Date());
        filterDateToInput.setValue(new Date());

        grossTotalFromInput.setValue(new BigDecimal("0.00"));
        grossTotalToInput.setValue(new BigDecimal("0.00"));

        reloadClientsOptions();
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case ADD_CONTROL:
                    openAddSingleOrder();

                    break;
                case SHOW_CONTROL:
                    openShowSingleOrder();

                    break;
                case DELETE_CONTROL:
                    removeOrder();

                    break;
            }
        }
    }

    private class FilterListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case FILTER_ORDERED_AT:
                    Date dateFrom = (Date) filterDateFromInput.getValue();
                    Date dateTo = (Date) filterDateToInput.getValue();

                    getAndShowOrdersFilterOrderedAtRange(dateFrom, dateTo);

                    break;
                case FILTER_GROSS_TOTAL:
                    BigDecimal totalFrom = (BigDecimal) grossTotalFromInput.getValue();
                    BigDecimal totalTo = (BigDecimal) grossTotalToInput.getValue();

                    getAndShowOrdersFilterGrossTotalRange(totalFrom, totalTo);

                    break;
                case FILTER_CLIENT:
                    Client selectedClient = (Client) clientComboBox.getSelectedItem();

                    if(selectedClient == null) {
                        getAndShowAllOrders();

                        return;
                    }

                    getAndShowOrdersFilterClient(selectedClient);

                    break;
                case SHOW_ALL:
                    getAndShowAllOrders();

                    break;
            }
        }
    }
}
