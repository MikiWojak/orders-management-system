package gui.item;

import data.GlobalData;
import gui.MainFrame;
import layout.SpringUtilities;
import model.Item;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

public class ItemsTablePanel extends JPanel {
    private final String ADD_CONTROL = "add";
    private final String SHOW_CONTROL = "show";
    private final String DELETE_CONTROL = "delete";
    private final String FILTER_NAME = "filterName";
    private final String SHOW_ALL = "showAll";


    private final MainFrame mainFrame;
    private final GlobalData globalData;

    private List<Item> items;

    String[] columnNames = {
            "Name",
            "SKU",
            "Unit Net Price",
            "Unit Gross Price",
            "Dimensions",
            "Weight"
    };

    JTextField filterNameInput;
    private DefaultTableModel itemsDefaultTableModel;
    private JTable itemsTable;

    public ItemsTablePanel(MainFrame mainFrame, GlobalData globalData) {
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

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new SpringLayout());

        filterNameInput = new JTextField();

        JButton filterNameButton = new JButton("Filter by Name");
        filterNameButton.setActionCommand(FILTER_NAME);
        filterNameButton.addActionListener(new FilterListener());

        JLabel headerLabel = new JLabel(
                "Items",
                JLabel.CENTER);

        labelPanel.add(headerLabel);

        filterPanel.add(filterNameInput);
        filterPanel.add(filterNameButton);

        SpringUtilities.makeGrid(
                filterPanel,
                1, 2,
                6, 6,
                6, 6
        );

        JPanel showAllPanel = new JPanel();
        showAllPanel.setLayout(new FlowLayout());

        JButton showAllButton = new JButton("Show All");
        showAllButton.setActionCommand(SHOW_ALL);
        showAllButton.addActionListener(new FilterListener());

        showAllPanel.add(showAllButton);

        headerPanel.add(labelPanel, BorderLayout.PAGE_START);
        headerPanel.add(filterPanel, BorderLayout.CENTER);
        headerPanel.add(showAllPanel, BorderLayout.PAGE_END);

        itemsDefaultTableModel = new DefaultTableModel(columnNames, 0);
        itemsTable = new JTable(itemsDefaultTableModel) {
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            };
        };
        JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);

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
        add(itemsTableScrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private void populateItemsToTable() {
        itemsDefaultTableModel.setRowCount(0);

        for (Item item : items) {
            Object[] rowData = new Object[6];

            rowData[0] = item.name();
            rowData[1] = item.sku();
            rowData[2] = item.unitNetPrice();
            rowData[3] = item.unitGrossPrice();
            rowData[4] = item.dimensions();
            rowData[5] = item.weight();

            itemsDefaultTableModel.addRow(rowData);
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

    private void openAddSingleItem() {
        mainFrame.openAddSingleItemPanel();
    }

    private void openShowSingleItem() {
        int index = itemsTable.getSelectedRow();

        if (index <= -1) {
            showWarningMessageDialog("Select Item.");

            return;
        }

        mainFrame.openShowSingleItemPanel(items.get(index));
    }

    private void removeItem() {
        int index = itemsTable.getSelectedRow();

        if (index <= -1) {
            showWarningMessageDialog("Select Item.");

            return;
        }

        UUID id = items.get(index).id();

        if(globalData.checkIfItemIsUsed(id)) {
            showWarningMessageDialog(
                    "Selected Item is being used.\n" +
                    "You cannot delete it."
            );

            return;
        }

        Object[] optionNames = {"Yes", "No"};

        int selectedOption = JOptionPane.showOptionDialog(
                null,
                "Do you really want to delete selected Item?",
                "Question",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionNames,
                optionNames[1]);

        if(selectedOption != 0) {
            return;
        }

        globalData.deleteItem(id);

        reloadPanel();
    }

    public void reloadPanel() {
        clearFilters();

        getAndShowAllItems();
    }

    private void getAndShowAllItems() {
        items = globalData.getItems();

        populateItemsToTable();
    }

    private void getAndShowItemsFilterName(String name) {
        items = globalData.getItemsFilterName(name);

        populateItemsToTable();
    }

    private void clearFilters() {
        filterNameInput.setText("");
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case ADD_CONTROL:
                    openAddSingleItem();

                    break;
                case SHOW_CONTROL:
                    openShowSingleItem();

                    break;
                case DELETE_CONTROL:
                    removeItem();

                    break;
            }
        }
    }

    private class FilterListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case FILTER_NAME:
                    String name = filterNameInput.getText();

                    if(name.isEmpty()) {
                        getAndShowAllItems();

                        return;
                    }

                    getAndShowItemsFilterName(name);

                    break;
                case SHOW_ALL:
                    clearFilters();
                    getAndShowAllItems();

                    break;
            }
        }
    }
}
