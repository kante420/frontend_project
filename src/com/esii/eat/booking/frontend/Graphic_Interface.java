package com.esii.eat.booking.frontend;

import com.esii.eat.booking.backend.Chain;
import com.esii.eat.booking.backend.Restaurant;
import javax.swing.*;

/**
 * Represents the main GUI for managing restaurant reservations within a chain.
 * This class provides a user interface for selecting restaurants, checking table availability,
 * making reservations, and searching for alternative restaurants if the preferred one is full.
 * <p>
 * The GUI is built using Swing components and interacts with the {@link Chain} and {@link Restaurant}
 * classes to manage reservations and display relevant information. All user-facing strings are loaded
 * from a {@code messages.properties} file for localization support.
 * </p>
 */
public class Graphic_Interface extends JFrame {

    /** Combo box for selecting a restaurant from the chain. */
    private JComboBox<String> restaurantComboBox;

    /** Text field for entering the number of diners. */
    private JTextField dinersCountTextField;

    /** Button to check table availability for the selected restaurant. */
    private JButton checkButton;

    /** Button to make a reservation at the selected restaurant. */
    private JButton reserveButton;

    /** Button to search for alternative restaurants if the selected one is full. */
    private JButton searchAlternativeButton;

    /** Text area to display the status of tables in the selected restaurant. */
    private JTextArea restaurantTableTextArea;

    /** Text area to display suggested tables for the reservation. */
    private JTextArea suggestedTablesTextArea;

    /** Main panel containing all GUI components. */
    private JPanel mainPanel;

    /** Text field for entering the name of the person making the reservation. */
    private JTextField nameTextField;

    /** Label to display a message when no tables are available. */
    private JLabel noTablesAvailableLabel;
    private JLabel stablishment_select_label;
    private JLabel number_dinners_label;
    private JLabel suggested_table_label;
    private JLabel client_name_label;
    private JLabel tables_restaurant_label;

    /** The restaurant chain managing all restaurants. */
    private Chain gourmetChain;

    /** An instance of the Italian Bistro restaurant. */
    private Restaurant italianBistro;

    /** An instance of the Sushi Palace restaurant. */
    private Restaurant sushiPalace;

    /** An instance of the Steak House restaurant. */
    private Restaurant steakHouse;

    /**
     * Constructs the Booking GUI and initializes the restaurant chain with predefined restaurants.
     * The GUI allows users to select a restaurant, check table availability, and make reservations.
     */
    public Graphic_Interface() {
        // Initialize the main window components
        setContentPane(mainPanel);
        setTitle("Restaurant reservation system");

        // Set the size of the window to fit all components
        setSize(850, 450);

        // Center the window on the screen
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        // Initialize the restaurant chain and add restaurants
        initializeRestaurants();

        // Populate the combo box with restaurant names
        populateRestaurantComboBox();

        // Display the initial restaurant table status
        restaurantTableTextArea.setText(italianBistro.toString());

        // Add action listeners to handle user interactions
        addActionListeners();
    }

    /**
     * Initializes the restaurant chain and adds predefined restaurants to it.
     */
    private void initializeRestaurants() {
        gourmetChain = new Chain("Gourmet Dining", 3);
        italianBistro = new Restaurant("Italian Bistro", 5);
        sushiPalace = new Restaurant("Sushi Palace", 3);
        steakHouse = new Restaurant("Steak House", 4);

        gourmetChain.addRestaurant(italianBistro);
        gourmetChain.addRestaurant(sushiPalace);
        gourmetChain.addRestaurant(steakHouse);
    }

    /**
     * Populates the restaurant combo box with the names of the available restaurants.
     */
    private void populateRestaurantComboBox() {
        // Clear existing items in the combo box
        restaurantComboBox.removeAllItems();

        // Add actual restaurant names
        restaurantComboBox.addItem(italianBistro.getName());
        restaurantComboBox.addItem(sushiPalace.getName());
        restaurantComboBox.addItem(steakHouse.getName());
    }

    /**
     * Adds action listeners to the combo box, check button, reserve button, and search alternative button
     * to handle user interactions.
     */
    private void addActionListeners() {
        // Add action listener to the combo box to update the table status display
        restaurantComboBox.addActionListener(_ -> updateRestaurantTableDisplay());

        // Add action listener to the check button to verify table availability
        checkButton.addActionListener(_ -> checkTableAvailability());

        // Add action listener to the reserve button to make a reservation
        reserveButton.addActionListener(_ -> makeReservation());

        // Add action listener to the search alternative button to find another restaurant
        searchAlternativeButton.addActionListener(_ -> searchAlternativeRestaurant());
    }

    /**
     * Updates the table status display based on the selected restaurant in the combo box.
     */
    private void updateRestaurantTableDisplay() {
        String selected_restaurant = (String) restaurantComboBox.getSelectedItem();
        if(selected_restaurant != null) {
            Restaurant restaurant = gourmetChain.getRestaurant(selected_restaurant);
            if(restaurant != null) {
                restaurantTableTextArea.setText(restaurant.toString());
            }
        }

        // TODO: Implement table display update
    }

    /**
     * Checks the availability of tables for the specified number of diners at the selected restaurant.
     * If tables are available, it enables the reserve button and displays the suggested tables.
     * If no tables are available, it shows a message and enables the search alternative button.
     */
    private void checkTableAvailability() {
        try {
            int numberOfPeople = Integer.parseInt(dinersCountTextField.getText());
            String selectedRestaurant = (String) restaurantComboBox.getSelectedItem();
            Restaurant restaurant = gourmetChain.getRestaurant(selectedRestaurant);

            if (restaurant != null && restaurant.hasAvailableTables(numberOfPeople)) {
                suggestedTablesTextArea.setText(restaurant.availableTablesInfo(numberOfPeople));
                reserveButton.setEnabled(true);
                nameTextField.setEnabled(true);
                noTablesAvailableLabel.setVisible(false);
            } else {
                noTablesAvailableLabel.setVisible(true);
                searchAlternativeButton.setEnabled(true);
            }
        } catch (NumberFormatException ex) { //In case user hasn´t write numbers, the exceptions jumps
            JOptionPane.showMessageDialog(null, "Please enter a valid number of diners.");
        }
    }

    /**
     * Attempts to make a reservation at the selected restaurant for the specified number of diners.
     * If the reservation is successful, it displays a confirmation message and resets the form.
     */
    private void makeReservation() {
        int numberOfPeople = Integer.parseInt(dinersCountTextField.getText());
        String selectedRestaurant = (String) restaurantComboBox.getSelectedItem();
        String reservationName = nameTextField.getText();

        boolean reservationSuccessful = gourmetChain.reserveRestaurant(numberOfPeople, selectedRestaurant, reservationName);
        if (reservationSuccessful) {
            JOptionPane.showMessageDialog(null, "Reservation successful!");
            resetForm();
        } else {
            JOptionPane.showMessageDialog(null, "Reservation failed. Please try again.");
        }
    }

    /**
     * Searches for an alternative restaurant with available tables if the selected restaurant is full.
     * If an alternative is found, it prompts the user to confirm the reservation.
     */
    private void searchAlternativeRestaurant() {
        String restaurantName = (String) restaurantComboBox.getSelectedItem();
        String reservationName = (String) nameTextField.getText();
        int numberOfPeople = Integer.parseInt(dinersCountTextField.getText());

        //Search an alternative restaurant
        Restaurant alternative = gourmetChain.searchRestaurant(numberOfPeople, restaurantName);

        if (alternative != null) {
            //Ask if the user wants to reserve in the alternative restaurant
            int response = JOptionPane.showConfirmDialog(null, "The selected restaurant is full\n" + "¿Do you want to reserve in the alternative one \"" + alternative.getName() + "\"?",
                    "Alternative restaurant found", JOptionPane.YES_NO_OPTION);

            String new_name = JOptionPane.showInputDialog(null, "Please enter the name for the reservation: ", "Reservation", JOptionPane.PLAIN_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {

                boolean reserved = gourmetChain.reserveRestaurant(numberOfPeople, alternative.getName(), new_name);
                if (reserved) {
                    JOptionPane.showMessageDialog(null, "Confirmed resevation in: \"" + alternative.getName() + "\"", "Confirmed reservation", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "The reservation cannot be done.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else {
            //If there isn´t any available restaurant
            JOptionPane.showMessageDialog(null, "We haven´t found any restaurant for " + numberOfPeople + " of people.", "No restaurant found", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Resets the form to its initial state, clearing input fields and disabling buttons.
     */
    private void resetForm() {
        restaurantComboBox.setSelectedIndex(0);
        nameTextField.setText("");

        dinersCountTextField.setText("");
        restaurantTableTextArea.setText("");
        suggestedTablesTextArea.setText("");
        reserveButton.setEnabled(false);
        searchAlternativeButton.setEnabled(false);
        nameTextField.setEnabled(false);
        noTablesAvailableLabel.setVisible(false);


        String selected_restaurant = (String) restaurantComboBox.getSelectedItem();
        Restaurant restaurant = gourmetChain.getRestaurant(selected_restaurant);
        restaurantTableTextArea.setText(restaurant.toString());

        // TODO: Implement form reset for the remaining and necessary components
    }

    /**
     * Main method to launch the Booking GUI.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Graphic_Interface();
    }
}