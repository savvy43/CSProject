package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import service.BookingService;
import service.DatabaseService;
import model.*;
import payment.*;

import java.util.*;

public class MainController {

    @FXML private Label hotelInfoLabel;
    @FXML private Label cashBalanceLabel;
    @FXML private StackPane contentArea;

    private BookingService service;
    private DatabaseService dbService;
    private Map<Room, String> guestNames = new HashMap<>();
    private Map<Room, Invoice> roomInvoices = new HashMap<>();
    private List<HouseKeeping> houseKeepingList = new ArrayList<>();
    private List<Invoice> completedInvoices = new ArrayList<>();

    public MainController() {
        Hotel hotel = new Hotel("Grand Hotel", new HotelLocation("Tashkent", "Amir Temur Street 15"));

        // Add rooms with different styles
        hotel.addRoom(new Room(101, "Single", Arrays.asList("WiFi", "TV")));
        hotel.addRoom(new Room(102, "Single", Arrays.asList("WiFi", "TV", "Mini Bar")));
        hotel.addRoom(new Room(201, "Double", Arrays.asList("WiFi", "TV", "Mini Bar", "Balcony")));
        hotel.addRoom(new Room(202, "Double", Arrays.asList("WiFi", "TV", "Mini Bar")));
        hotel.addRoom(new Room(301, "Suite", Arrays.asList("WiFi", "TV", "Mini Bar", "Balcony", "Jacuzzi")));
        hotel.addRoom(new Room(302, "Suite", Arrays.asList("WiFi", "TV", "Mini Bar", "Balcony", "Kitchen")));

        service = new BookingService(hotel);
        dbService = new DatabaseService();

        // Initialize housekeeping for all rooms
        for (Room room : hotel.getAllRooms()) {
            houseKeepingList.add(new HouseKeeping(room, "Clean"));
        }

        // Load room statuses from database
        loadRoomStatusesFromDB();
    }

    @FXML
    public void initialize() {
        try {
            if (hotelInfoLabel != null) {
                hotelInfoLabel.setText(service.getHotel().getName() + " - " + 
                                       service.getHotel().getLocation().toString());
            }
            updateCashBalanceDisplay();
            showDashboard(); // Start with dashboard instead of room management
        } catch (Exception ex) {
            showAlert("Initialization Error", "Failed to initialize application: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadRoomStatusesFromDB() {
        try {
            Map<Integer, DatabaseService.RoomData> roomDataMap = dbService.loadAllRoomStatuses();
            
            for (Room room : service.getHotel().getAllRooms()) {
                DatabaseService.RoomData data = roomDataMap.get(room.getRoomNumber());
                if (data != null) {
                    room.setAvailability(data.isAvailable);
                    if (data.guestName != null && !data.guestName.isEmpty()) {
                        guestNames.put(room, data.guestName);
                    }
                    
                    // Update housekeeping status
                    for (HouseKeeping hk : houseKeepingList) {
                        if (hk.getRoom().getRoomNumber() == room.getRoomNumber()) {
                            if ("Clean".equals(data.cleaningStatus)) {
                                hk.markClean();
                            } else {
                                hk.markDirty();
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading room statuses: " + ex.getMessage());
        }
    }

    private void updateCashBalanceDisplay() {
        try {
            double balance = dbService.getCashBalance();
            if (cashBalanceLabel != null) {
                cashBalanceLabel.setText("Cash Balance: $" + String.format("%.2f", balance));
            }
        } catch (Exception ex) {
            System.err.println("Error updating cash balance display: " + ex.getMessage());
        }
    }

    private void saveRoomToDB(Room room) {
        try {
            String guestName = guestNames.getOrDefault(room, null);
            String cleaningStatus = "Clean";
            
            for (HouseKeeping hk : houseKeepingList) {
                if (hk.getRoom().getRoomNumber() == room.getRoomNumber()) {
                    cleaningStatus = hk.getStatus();
                    break;
                }
            }
            
            dbService.saveRoomStatus(room, room.isAvailable(), guestName, cleaningStatus);
        } catch (Exception ex) {
            System.err.println("Error saving room to DB: " + ex.getMessage());
        }
    }

    @FXML
    public void showDashboard() {
        try {
            VBox panel = new VBox(20);
            panel.getStyleClass().add("panel");

            Label title = new Label("📊 Dashboard Overview");
            title.getStyleClass().add("panel-title");

            // Statistics Cards
            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(20);
            statsGrid.setVgap(20);

            // Total Rooms
            VBox totalRoomsCard = createStatCard("🏨", 
                String.valueOf(service.getHotel().getAllRooms().size()), 
                "Total Rooms");
            
            // Available Rooms
            long availableCount = service.getHotel().getAllRooms().stream()
                .filter(Room::isAvailable).count();
            VBox availableCard = createStatCard("✅", 
                String.valueOf(availableCount), 
                "Available Rooms");
            
            // Occupied Rooms
            long occupiedCount = service.getHotel().getAllRooms().stream()
                .filter(r -> !r.isAvailable()).count();
            VBox occupiedCard = createStatCard("🛏️", 
                String.valueOf(occupiedCount), 
                "Occupied Rooms");
            
            // Total Revenue
            VBox revenueCard = createStatCard("💰", 
                "$" + String.format("%.2f", getTotalRevenue()), 
                "Total Revenue");

            statsGrid.add(totalRoomsCard, 0, 0);
            statsGrid.add(availableCard, 1, 0);
            statsGrid.add(occupiedCard, 0, 1);
            statsGrid.add(revenueCard, 1, 1);

            // Occupancy Rate Chart
            Label occupancyTitle = new Label("📈 Occupancy Rate");
            occupancyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            double occupancyRate = (occupiedCount * 100.0) / service.getHotel().getAllRooms().size();
            ProgressBar occupancyBar = new ProgressBar(occupancyRate / 100.0);
            occupancyBar.setPrefWidth(400);
            occupancyBar.setPrefHeight(30);
            occupancyBar.setStyle("-fx-accent: linear-gradient(90deg, #667eea 0%, #764ba2 100%);");
            
            Label occupancyLabel = new Label(String.format("%.1f%% Occupied", occupancyRate));
            occupancyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Cleaning Status Overview
            Label cleaningTitle = new Label("🧹 Cleaning Status");
            cleaningTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            long cleanCount = houseKeepingList.stream()
                .filter(hk -> "Clean".equals(hk.getStatus())).count();
            long dirtyCount = houseKeepingList.stream()
                .filter(hk -> "Dirty".equals(hk.getStatus())).count();
            
            HBox cleaningStats = new HBox(30);
            Label cleanLabel = new Label("Clean: " + cleanCount);
            cleanLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #11998e;");
            Label dirtyLabel = new Label("Dirty: " + dirtyCount);
            dirtyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff6b6b;");
            cleaningStats.getChildren().addAll(cleanLabel, dirtyLabel);

            // Quick Actions
            Label actionsTitle = new Label("⚡ Quick Actions");
            actionsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            HBox quickActions = new HBox(15);
            Button bookBtn = new Button("➕ New Booking");
            bookBtn.getStyleClass().add("btn-success");
            bookBtn.setOnAction(e -> showBooking());
            
            Button checkoutBtn = new Button("💰 Checkout");
            checkoutBtn.getStyleClass().add("btn-primary");
            checkoutBtn.setOnAction(e -> showCheckout());
            
            Button cleanBtn = new Button("🧹 Housekeeping");
            cleanBtn.getStyleClass().add("btn-warning");
            cleanBtn.setOnAction(e -> showHousekeeping());
            
            quickActions.getChildren().addAll(bookBtn, checkoutBtn, cleanBtn);

            panel.getChildren().addAll(
                title, 
                statsGrid, 
                new Separator(),
                occupancyTitle, 
                occupancyBar, 
                occupancyLabel,
                new Separator(),
                cleaningTitle,
                cleaningStats,
                new Separator(),
                actionsTitle,
                quickActions
            );
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(panel);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load dashboard: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createStatCard(String icon, String value, String label) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setPrefHeight(120);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        
        Label descLabel = new Label(label);
        descLabel.getStyleClass().add("stat-label");

        card.getChildren().addAll(iconLabel, valueLabel, descLabel);
        return card;
    }

    @FXML
    public void showSearch() {
        try {
            VBox panel = new VBox(20);
            panel.getStyleClass().add("panel");
            panel.setMaxWidth(700);

            Label title = new Label("🔍 Search & Filter Rooms");
            title.getStyleClass().add("panel-title");

            // Search by room number
            Label searchLabel = new Label("Search by Room Number:");
            TextField searchField = new TextField();
            searchField.setPromptText("Enter room number (e.g., 101)");

            // Filter by room style
            Label styleLabel = new Label("Filter by Room Style:");
            ComboBox<String> styleCombo = new ComboBox<>();
            styleCombo.getItems().addAll("All", "Single", "Double", "Suite");
            styleCombo.setValue("All");
            styleCombo.setPrefWidth(200);

            // Filter by availability
            Label availLabel = new Label("Filter by Availability:");
            ComboBox<String> availCombo = new ComboBox<>();
            availCombo.getItems().addAll("All", "Available", "Occupied");
            availCombo.setValue("All");
            availCombo.setPrefWidth(200);

            // Results table
            TableView<Room> resultsTable = new TableView<>();
            resultsTable.setPrefHeight(300);

            TableColumn<Room, Integer> numCol = new TableColumn<>("Room #");
            numCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRoomNumber()));
            numCol.setPrefWidth(100);

            TableColumn<Room, String> styleCol = new TableColumn<>("Style");
            styleCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomStyle()));
            styleCol.setPrefWidth(120);

            TableColumn<Room, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().isAvailable() ? "Available" : "Occupied"));
            statusCol.setPrefWidth(120);

            TableColumn<Room, String> amenitiesCol = new TableColumn<>("Amenities");
            amenitiesCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(
                    String.join(", ", data.getValue().getAmenities())));
            amenitiesCol.setPrefWidth(250);

            resultsTable.getColumns().addAll(numCol, styleCol, statusCol, amenitiesCol);

            // Search button
            Button searchBtn = new Button("🔍 Search");
            searchBtn.getStyleClass().add("btn-primary");
            searchBtn.setOnAction(e -> {
                try {
                    List<Room> filtered = service.getHotel().getAllRooms().stream()
                        .filter(room -> {
                            // Filter by room number
                            String searchText = searchField.getText().trim();
                            if (!searchText.isEmpty()) {
                                try {
                                    int searchNum = Integer.parseInt(searchText);
                                    if (room.getRoomNumber() != searchNum) return false;
                                } catch (NumberFormatException ex) {
                                    return false;
                                }
                            }
                            
                            // Filter by style
                            String style = styleCombo.getValue();
                            if (!"All".equals(style) && !room.getRoomStyle().equals(style)) {
                                return false;
                            }
                            
                            // Filter by availability
                            String avail = availCombo.getValue();
                            if ("Available".equals(avail) && !room.isAvailable()) return false;
                            if ("Occupied".equals(avail) && room.isAvailable()) return false;
                            
                            return true;
                        })
                        .collect(java.util.stream.Collectors.toList());
                    
                    resultsTable.setItems(FXCollections.observableArrayList(filtered));
                    
                    if (filtered.isEmpty()) {
                        showAlert("No Results", "No rooms match your search criteria", Alert.AlertType.INFORMATION);
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Search failed: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            Button clearBtn = new Button("🔄 Clear Filters");
            clearBtn.getStyleClass().add("btn-warning");
            clearBtn.setOnAction(e -> {
                searchField.clear();
                styleCombo.setValue("All");
                availCombo.setValue("All");
                resultsTable.setItems(FXCollections.observableArrayList(service.getHotel().getAllRooms()));
            });

            HBox buttonBox = new HBox(15, searchBtn, clearBtn);

            GridPane filterGrid = new GridPane();
            filterGrid.setHgap(15);
            filterGrid.setVgap(15);
            filterGrid.add(searchLabel, 0, 0);
            filterGrid.add(searchField, 1, 0);
            filterGrid.add(styleLabel, 0, 1);
            filterGrid.add(styleCombo, 1, 1);
            filterGrid.add(availLabel, 0, 2);
            filterGrid.add(availCombo, 1, 2);

            // Initial load - show all rooms
            resultsTable.setItems(FXCollections.observableArrayList(service.getHotel().getAllRooms()));

            panel.getChildren().addAll(title, filterGrid, buttonBox, resultsTable);
            panel.setAlignment(Pos.TOP_CENTER);
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(panel);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load search: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showReports() {
        try {
            VBox panel = new VBox(20);
            panel.getStyleClass().add("panel");

            Label title = new Label("📈 Reports & Analytics");
            title.getStyleClass().add("panel-title");

            // Revenue Report
            Label revenueTitle = new Label("💰 Revenue Report");
            revenueTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            GridPane revenueGrid = new GridPane();
            revenueGrid.setHgap(20);
            revenueGrid.setVgap(10);
            revenueGrid.setStyle("-fx-padding: 15; -fx-background-color: rgba(102,126,234,0.1); -fx-background-radius: 10;");

            Label totalInvoicesLabel = new Label("Total Invoices:");
            totalInvoicesLabel.setStyle("-fx-font-size: 16px;");
            Label totalInvoicesValue = new Label(String.valueOf(completedInvoices.size()));
            totalInvoicesValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label totalRevenueLabel = new Label("Total Revenue:");
            totalRevenueLabel.setStyle("-fx-font-size: 16px;");
            Label totalRevenueValue = new Label("$" + String.format("%.2f", getTotalRevenue()));
            totalRevenueValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #11998e;");

            Label avgInvoiceLabel = new Label("Average Invoice:");
            avgInvoiceLabel.setStyle("-fx-font-size: 16px;");
            double avgInvoice = completedInvoices.isEmpty() ? 0 : getTotalRevenue() / completedInvoices.size();
            Label avgInvoiceValue = new Label("$" + String.format("%.2f", avgInvoice));
            avgInvoiceValue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            revenueGrid.add(totalInvoicesLabel, 0, 0);
            revenueGrid.add(totalInvoicesValue, 1, 0);
            revenueGrid.add(totalRevenueLabel, 0, 1);
            revenueGrid.add(totalRevenueValue, 1, 1);
            revenueGrid.add(avgInvoiceLabel, 0, 2);
            revenueGrid.add(avgInvoiceValue, 1, 2);

            // Room Statistics
            Label roomStatsTitle = new Label("🏨 Room Statistics");
            roomStatsTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            GridPane roomStatsGrid = new GridPane();
            roomStatsGrid.setHgap(20);
            roomStatsGrid.setVgap(10);
            roomStatsGrid.setStyle("-fx-padding: 15; -fx-background-color: rgba(102,126,234,0.1); -fx-background-radius: 10;");

            long singleCount = service.getHotel().getAllRooms().stream()
                .filter(r -> "Single".equals(r.getRoomStyle())).count();
            long doubleCount = service.getHotel().getAllRooms().stream()
                .filter(r -> "Double".equals(r.getRoomStyle())).count();
            long suiteCount = service.getHotel().getAllRooms().stream()
                .filter(r -> "Suite".equals(r.getRoomStyle())).count();

            roomStatsGrid.add(new Label("Single Rooms:"), 0, 0);
            roomStatsGrid.add(new Label(String.valueOf(singleCount)), 1, 0);
            roomStatsGrid.add(new Label("Double Rooms:"), 0, 1);
            roomStatsGrid.add(new Label(String.valueOf(doubleCount)), 1, 1);
            roomStatsGrid.add(new Label("Suite Rooms:"), 0, 2);
            roomStatsGrid.add(new Label(String.valueOf(suiteCount)), 1, 2);

            // Occupancy Report
            Label occupancyTitle = new Label("📊 Occupancy Report");
            occupancyTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            long occupied = service.getHotel().getAllRooms().stream()
                .filter(r -> !r.isAvailable()).count();
            long available = service.getHotel().getAllRooms().stream()
                .filter(Room::isAvailable).count();
            double occupancyRate = (occupied * 100.0) / service.getHotel().getAllRooms().size();

            GridPane occupancyGrid = new GridPane();
            occupancyGrid.setHgap(20);
            occupancyGrid.setVgap(10);
            occupancyGrid.setStyle("-fx-padding: 15; -fx-background-color: rgba(102,126,234,0.1); -fx-background-radius: 10;");

            occupancyGrid.add(new Label("Occupied Rooms:"), 0, 0);
            occupancyGrid.add(new Label(String.valueOf(occupied)), 1, 0);
            occupancyGrid.add(new Label("Available Rooms:"), 0, 1);
            occupancyGrid.add(new Label(String.valueOf(available)), 1, 1);
            occupancyGrid.add(new Label("Occupancy Rate:"), 0, 2);
            Label occupancyRateLabel = new Label(String.format("%.1f%%", occupancyRate));
            occupancyRateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #667eea;");
            occupancyGrid.add(occupancyRateLabel, 1, 2);

            // Export button
            Button exportBtn = new Button("📄 Export Report");
            exportBtn.getStyleClass().add("btn-primary");
            exportBtn.setOnAction(e -> {
                showAlert("Export", "Report export feature - Coming Soon!\nWould save to PDF or Excel in production.", Alert.AlertType.INFORMATION);
            });

            panel.getChildren().addAll(
                title,
                revenueTitle, revenueGrid,
                new Separator(),
                roomStatsTitle, roomStatsGrid,
                new Separator(),
                occupancyTitle, occupancyGrid,
                new Separator(),
                exportBtn
            );
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(panel);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load reports: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showRoomManagement() {
        try {
            VBox panel = new VBox(15);
            panel.getStyleClass().add("panel");

            Label title = new Label("Room Management");
            title.getStyleClass().add("panel-title");

            // Create table
            TableView<Room> table = new TableView<>();
            table.setPrefHeight(400);

            TableColumn<Room, Integer> numCol = new TableColumn<>("Room #");
            numCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRoomNumber()));
            numCol.setPrefWidth(100);

            TableColumn<Room, String> styleCol = new TableColumn<>("Style");
            styleCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomStyle()));
            styleCol.setPrefWidth(120);

            TableColumn<Room, String> amenitiesCol = new TableColumn<>("Amenities");
            amenitiesCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(
                    String.join(", ", data.getValue().getAmenities())));
            amenitiesCol.setPrefWidth(300);

            TableColumn<Room, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().isAvailable() ? "Available" : "Occupied"));
            statusCol.setPrefWidth(120);

            TableColumn<Room, String> guestCol = new TableColumn<>("Guest");
            guestCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(
                    guestNames.getOrDefault(data.getValue(), "-")));
            guestCol.setPrefWidth(150);

            table.getColumns().addAll(numCol, styleCol, amenitiesCol, statusCol, guestCol);

            ObservableList<Room> rooms = FXCollections.observableArrayList(service.getHotel().getAllRooms());
            table.setItems(rooms);

            Button refreshBtn = new Button("🔄 Refresh");
            refreshBtn.getStyleClass().add("btn-primary");
            refreshBtn.setOnAction(e -> {
                try {
                    table.setItems(FXCollections.observableArrayList(service.getHotel().getAllRooms()));
                } catch (Exception ex) {
                    showAlert("Error", "Failed to refresh room list: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            HBox btnBox = new HBox(10, refreshBtn);
            btnBox.setAlignment(Pos.CENTER_LEFT);

            panel.getChildren().addAll(title, table, btnBox);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(panel);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load room management: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showBooking() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel");
        panel.setMaxWidth(600);

        Label title = new Label("Book a Room");
        title.getStyleClass().add("panel-title");

        // Guest name
        Label nameLabel = new Label("Guest Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter guest name");

        // Room selection
        Label roomLabel = new Label("Select Room:");
        ComboBox<Room> roomCombo = new ComboBox<>();
        try {
            for (Room room : service.getHotel().getAllRooms()) {
                if (room.isAvailable()) {
                    roomCombo.getItems().add(room);
                }
            }
        } catch (Exception ex) {
            showAlert("Error", "Failed to load rooms: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        roomCombo.setPromptText("Choose available room");
        roomCombo.setPrefWidth(300);

        // Room price based on style
        Label priceLabel = new Label("Price per night: $0");
        roomCombo.setOnAction(e -> {
            try {
                Room selected = roomCombo.getValue();
                if (selected != null) {
                    double price = getRoomPrice(selected);
                    priceLabel.setText("Price per night: $" + price);
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to calculate price: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Number of nights
        Label nightsLabel = new Label("Number of Nights:");
        Spinner<Integer> nightsSpinner = new Spinner<>(1, 30, 1);
        nightsSpinner.setPrefWidth(150);
        nightsSpinner.setEditable(true);

        Button bookBtn = new Button("✅ Confirm Booking");
        bookBtn.getStyleClass().add("btn-success");
        bookBtn.setOnAction(e -> {
            try {
                String guestName = nameField.getText().trim();
                Room selectedRoom = roomCombo.getValue();
                
                // Validate guest name
                if (guestName.isEmpty()) {
                    showAlert("Validation Error", "Please enter guest name", Alert.AlertType.ERROR);
                    return;
                }
                
                if (guestName.length() < 2) {
                    showAlert("Validation Error", "Guest name must be at least 2 characters", Alert.AlertType.ERROR);
                    return;
                }
                
                if (!guestName.matches("[a-zA-Z\\s]+")) {
                    showAlert("Validation Error", "Guest name should only contain letters", Alert.AlertType.ERROR);
                    return;
                }

                if (selectedRoom == null) {
                    showAlert("Validation Error", "Please select a room", Alert.AlertType.ERROR);
                    return;
                }

                // Validate nights
                int nights;
                try {
                    nights = nightsSpinner.getValue();
                    if (nights < 1 || nights > 30) {
                        showAlert("Validation Error", "Number of nights must be between 1 and 30", Alert.AlertType.ERROR);
                        return;
                    }
                } catch (Exception ex) {
                    showAlert("Validation Error", "Invalid number of nights", Alert.AlertType.ERROR);
                    return;
                }

                if (service.book(selectedRoom)) {
                    guestNames.put(selectedRoom, guestName);
                    
                    // Create invoice
                    Invoice invoice = new Invoice();
                    double roomPrice = getRoomPrice(selectedRoom);
                    invoice.addItem(new InvoiceItem(roomPrice * nights));
                    roomInvoices.put(selectedRoom, invoice);

                    // Save to database
                    saveRoomToDB(selectedRoom);

                    showAlert("Success", 
                        "Room " + selectedRoom.getRoomNumber() + " booked for " + guestName + 
                        "\nTotal: $" + (roomPrice * nights) + " (" + nights + " nights)", 
                        Alert.AlertType.INFORMATION);
                    
                    nameField.clear();
                    roomCombo.getItems().clear();
                    for (Room room : service.getHotel().getAllRooms()) {
                        if (room.isAvailable()) {
                            roomCombo.getItems().add(room);
                        }
                    }
                } else {
                    showAlert("Booking Error", "Failed to book room. Room may no longer be available.", Alert.AlertType.ERROR);
                }
            } catch (NullPointerException ex) {
                showAlert("Error", "Missing required information. Please fill all fields.", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Error", "Booking failed: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(roomLabel, 0, 1);
        form.add(roomCombo, 1, 1);
        form.add(priceLabel, 1, 2);
        form.add(nightsLabel, 0, 3);
        form.add(nightsSpinner, 1, 3);

        panel.getChildren().addAll(title, form, bookBtn);
        panel.setAlignment(Pos.TOP_CENTER);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    @FXML
    public void showLeaveHotel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel");
        panel.setMaxWidth(600);

        Label title = new Label("Guest Leaving Hotel");
        title.getStyleClass().add("panel-title");

        Label infoLabel = new Label("Select a room to process guest departure (no payment)");
        infoLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        // Select occupied room
        Label roomLabel = new Label("Select Room:");
        ComboBox<Room> roomCombo = new ComboBox<>();
        try {
            for (Room room : service.getHotel().getAllRooms()) {
                if (!room.isAvailable()) {
                    roomCombo.getItems().add(room);
                }
            }
            if (roomCombo.getItems().isEmpty()) {
                showAlert("Info", "No occupied rooms", Alert.AlertType.INFORMATION);
            }
        } catch (Exception ex) {
            showAlert("Error", "Failed to load rooms: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        roomCombo.setPromptText("Choose occupied room");
        roomCombo.setPrefWidth(300);

        Label guestLabel = new Label("Guest: -");
        guestLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        roomCombo.setOnAction(e -> {
            try {
                Room selected = roomCombo.getValue();
                if (selected != null) {
                    String guest = guestNames.getOrDefault(selected, "Unknown");
                    guestLabel.setText("Guest: " + guest);
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to load room details: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button leaveBtn = new Button("🚪 Process Departure");
        leaveBtn.getStyleClass().add("btn-warning");
        leaveBtn.setOnAction(e -> {
            try {
                Room selected = roomCombo.getValue();
                if (selected == null) {
                    showAlert("Validation Error", "Please select a room", Alert.AlertType.ERROR);
                    return;
                }

                String guestName = guestNames.getOrDefault(selected, "Unknown");
                
                // Confirm departure
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Departure");
                confirmAlert.setHeaderText("Process guest departure?");
                confirmAlert.setContentText("Guest: " + guestName + "\nRoom: " + selected.getRoomNumber() + 
                                           "\n\nNote: No payment will be collected.");
                
                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    // Free the room
                    selected.setAvailability(true);
                    guestNames.remove(selected);
                    roomInvoices.remove(selected);
                    
                    // Mark room as dirty for housekeeping
                    for (HouseKeeping hk : houseKeepingList) {
                        if (hk.getRoom().equals(selected)) {
                            hk.markDirty();
                            break;
                        }
                    }

                    // Save to database
                    saveRoomToDB(selected);

                    showAlert("Success", 
                        "Guest " + guestName + " has left.\nRoom " + selected.getRoomNumber() + 
                        " is now available.\nRoom marked as dirty for housekeeping.", 
                        Alert.AlertType.INFORMATION);
                    
                    // Refresh
                    roomCombo.getItems().clear();
                    for (Room room : service.getHotel().getAllRooms()) {
                        if (!room.isAvailable()) {
                            roomCombo.getItems().add(room);
                        }
                    }
                    guestLabel.setText("Guest: -");
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to process departure: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.add(roomLabel, 0, 0);
        form.add(roomCombo, 1, 0);
        form.add(guestLabel, 1, 1);

        panel.getChildren().addAll(title, infoLabel, form, leaveBtn);
        panel.setAlignment(Pos.TOP_CENTER);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    @FXML
    public void showCheckout() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel");
        panel.setMaxWidth(700);

        Label title = new Label("Checkout & Payment");
        title.getStyleClass().add("panel-title");

        // Select occupied room
        Label roomLabel = new Label("Select Room to Checkout:");
        ComboBox<Room> roomCombo = new ComboBox<>();
        try {
            for (Room room : service.getHotel().getAllRooms()) {
                if (!room.isAvailable()) {
                    roomCombo.getItems().add(room);
                }
            }
            if (roomCombo.getItems().isEmpty()) {
                showAlert("Info", "No occupied rooms to checkout", Alert.AlertType.INFORMATION);
            }
        } catch (Exception ex) {
            showAlert("Error", "Failed to load rooms: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        roomCombo.setPromptText("Choose occupied room");
        roomCombo.setPrefWidth(300);

        Label guestLabel = new Label("Guest: -");
        Label amountLabel = new Label("Amount Due: $0");

        roomCombo.setOnAction(e -> {
            try {
                Room selected = roomCombo.getValue();
                if (selected != null) {
                    String guest = guestNames.getOrDefault(selected, "Unknown");
                    guestLabel.setText("Guest: " + guest);
                    
                    Invoice invoice = roomInvoices.get(selected);
                    if (invoice != null) {
                        amountLabel.setText("Amount Due: $" + invoice.getTotalAmount());
                    } else {
                        amountLabel.setText("Amount Due: $0");
                        showAlert("Warning", "No invoice found for this room", Alert.AlertType.WARNING);
                    }
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to load room details: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Payment method
        Label paymentLabel = new Label("Payment Method:");
        ComboBox<String> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("Cash", "Credit Card", "Check");
        paymentCombo.setValue("Cash");
        paymentCombo.setPrefWidth(200);

        // Payment details panel
        VBox paymentDetails = new VBox(10);
        
        // Cash fields
        Label cashLabel = new Label("Cash Amount:");
        TextField cashField = new TextField();
        cashField.setPromptText("Enter cash amount");
        VBox cashBox = new VBox(5, cashLabel, cashField);

        // Credit card fields
        Label cardNameLabel = new Label("Cardholder Name:");
        TextField cardNameField = new TextField();
        Label zipLabel = new Label("ZIP Code:");
        TextField zipField = new TextField();
        VBox cardBox = new VBox(5, cardNameLabel, cardNameField, zipLabel, zipField);
        cardBox.setVisible(false);

        // Check fields
        Label bankLabel = new Label("Bank Name:");
        TextField bankField = new TextField();
        Label checkNumLabel = new Label("Check Number:");
        TextField checkNumField = new TextField();
        VBox checkBox = new VBox(5, bankLabel, bankField, checkNumLabel, checkNumField);
        checkBox.setVisible(false);

        paymentDetails.getChildren().addAll(cashBox, cardBox, checkBox);

        paymentCombo.setOnAction(e -> {
            String method = paymentCombo.getValue();
            cashBox.setVisible(method.equals("Cash"));
            cardBox.setVisible(method.equals("Credit Card"));
            checkBox.setVisible(method.equals("Check"));
        });

        Button processBtn = new Button("💳 Process Payment & Checkout");
        processBtn.getStyleClass().add("btn-success");
        processBtn.setOnAction(e -> {
            try {
                Room selected = roomCombo.getValue();
                if (selected == null) {
                    showAlert("Validation Error", "Please select a room", Alert.AlertType.ERROR);
                    return;
                }

                Invoice invoice = roomInvoices.get(selected);
                if (invoice == null) {
                    showAlert("Error", "No invoice found for this room", Alert.AlertType.ERROR);
                    return;
                }

                double amount = invoice.getTotalAmount();
                if (amount <= 0) {
                    showAlert("Error", "Invalid invoice amount", Alert.AlertType.ERROR);
                    return;
                }

                String method = paymentCombo.getValue();
                if (method == null || method.isEmpty()) {
                    showAlert("Validation Error", "Please select a payment method", Alert.AlertType.ERROR);
                    return;
                }

                BillTransaction transaction = null;

                try {
                    if (method.equals("Cash")) {
                        String cashText = cashField.getText().trim();
                        if (cashText.isEmpty()) {
                            showAlert("Validation Error", "Please enter cash amount", Alert.AlertType.ERROR);
                            return;
                        }
                        
                        double cash = Double.parseDouble(cashText);
                        if (cash < 0) {
                            showAlert("Validation Error", "Cash amount cannot be negative", Alert.AlertType.ERROR);
                            return;
                        }
                        if (cash < amount) {
                            showAlert("Validation Error", "Insufficient cash. Amount due: $" + amount, Alert.AlertType.ERROR);
                            return;
                        }
                        transaction = new CashTransaction(amount, cash);
                        
                    } else if (method.equals("Credit Card")) {
                        String name = cardNameField.getText().trim();
                        String zip = zipField.getText().trim();
                        
                        if (name.isEmpty()) {
                            showAlert("Validation Error", "Please enter cardholder name", Alert.AlertType.ERROR);
                            return;
                        }
                        if (name.length() < 2) {
                            showAlert("Validation Error", "Cardholder name must be at least 2 characters", Alert.AlertType.ERROR);
                            return;
                        }
                        if (zip.isEmpty()) {
                            showAlert("Validation Error", "Please enter ZIP code", Alert.AlertType.ERROR);
                            return;
                        }
                        if (!zip.matches("\\d{5}")) {
                            showAlert("Validation Error", "ZIP code must be 5 digits", Alert.AlertType.ERROR);
                            return;
                        }
                        transaction = new CreditCardTransaction(amount, name, zip);
                        
                    } else if (method.equals("Check")) {
                        String bank = bankField.getText().trim();
                        String checkNum = checkNumField.getText().trim();
                        
                        if (bank.isEmpty()) {
                            showAlert("Validation Error", "Please enter bank name", Alert.AlertType.ERROR);
                            return;
                        }
                        if (checkNum.isEmpty()) {
                            showAlert("Validation Error", "Please enter check number", Alert.AlertType.ERROR);
                            return;
                        }
                        if (!checkNum.matches("\\d+")) {
                            showAlert("Validation Error", "Check number must contain only digits", Alert.AlertType.ERROR);
                            return;
                        }
                        transaction = new CheckTransaction(amount, bank, checkNum);
                    }

                    if (transaction != null && transaction.initiateTransaction()) {
                        // Checkout successful
                        selected.setAvailability(true);
                        completedInvoices.add(invoice);
                        roomInvoices.remove(selected);
                        
                        // Add payment to cash balance
                        dbService.addToCashBalance(amount);
                        updateCashBalanceDisplay();
                        
                        // Mark room as dirty for housekeeping
                        for (HouseKeeping hk : houseKeepingList) {
                            if (hk.getRoom().equals(selected)) {
                                hk.markDirty();
                                break;
                            }
                        }

                        // Save to database
                        saveRoomToDB(selected);

                        String msg = "Payment successful!\nRoom " + selected.getRoomNumber() + 
                                    " checked out.\nGuest: " + guestNames.get(selected) +
                                    "\nAmount paid: $" + String.format("%.2f", amount);
                        
                        if (transaction instanceof CashTransaction) {
                            double change = ((CashTransaction) transaction).getChange();
                            msg += "\nChange: $" + String.format("%.2f", change);
                        }

                        guestNames.remove(selected);
                        showAlert("Success", msg, Alert.AlertType.INFORMATION);
                        
                        // Refresh
                        roomCombo.getItems().clear();
                        for (Room room : service.getHotel().getAllRooms()) {
                            if (!room.isAvailable()) {
                                roomCombo.getItems().add(room);
                            }
                        }
                        guestLabel.setText("Guest: -");
                        amountLabel.setText("Amount Due: $0");
                        cashField.clear();
                        cardNameField.clear();
                        zipField.clear();
                        bankField.clear();
                        checkNumField.clear();
                    } else {
                        showAlert("Payment Failed", "Payment transaction failed. Please verify your payment details.", Alert.AlertType.ERROR);
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Validation Error", "Invalid number format. Please enter valid numeric values.", Alert.AlertType.ERROR);
                } catch (IllegalArgumentException ex) {
                    showAlert("Validation Error", "Invalid input: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            } catch (NullPointerException ex) {
                showAlert("Error", "Missing required information. Please fill all fields.", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Error", "Payment processing failed: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.add(roomLabel, 0, 0);
        form.add(roomCombo, 1, 0);
        form.add(guestLabel, 1, 1);
        form.add(amountLabel, 1, 2);
        form.add(paymentLabel, 0, 3);
        form.add(paymentCombo, 1, 3);

        panel.getChildren().addAll(title, form, paymentDetails, processBtn);
        panel.setAlignment(Pos.TOP_CENTER);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(panel);
    }

    @FXML
    public void showHousekeeping() {
        try {
            VBox panel = new VBox(15);
            panel.getStyleClass().add("panel");

            Label title = new Label("Housekeeping Management");
            title.getStyleClass().add("panel-title");

            TableView<HouseKeeping> table = new TableView<>();
            table.setPrefHeight(400);

            TableColumn<HouseKeeping, Integer> roomCol = new TableColumn<>("Room #");
            roomCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRoom().getRoomNumber()));
            roomCol.setPrefWidth(150);

            TableColumn<HouseKeeping, String> statusCol = new TableColumn<>("Cleaning Status");
            statusCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
            statusCol.setPrefWidth(200);

            TableColumn<HouseKeeping, String> availCol = new TableColumn<>("Room Status");
            availCol.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getRoom().isAvailable() ? "Available" : "Occupied"));
            availCol.setPrefWidth(200);

            table.getColumns().addAll(roomCol, statusCol, availCol);

            ObservableList<HouseKeeping> hkList = FXCollections.observableArrayList(houseKeepingList);
            table.setItems(hkList);

            Button cleanBtn = new Button("✅ Mark as Clean");
            cleanBtn.getStyleClass().add("btn-success");
            cleanBtn.setOnAction(e -> {
                try {
                    HouseKeeping selected = table.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        // Check if room is occupied
                        if (!selected.getRoom().isAvailable()) {
                            showAlert("Cannot Clean", "Cannot mark occupied room as clean. Guest is still in the room.", Alert.AlertType.WARNING);
                            return;
                        }
                        
                        selected.markClean();
                        table.refresh();
                        
                        // Save to database
                        saveRoomToDB(selected.getRoom());
                        
                        showAlert("Success", "Room " + selected.getRoom().getRoomNumber() + 
                                 " marked as clean", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Validation Error", "Please select a room from the table", Alert.AlertType.ERROR);
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update room status: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            Button dirtyBtn = new Button("❌ Mark as Dirty");
            dirtyBtn.getStyleClass().add("btn-danger");
            dirtyBtn.setOnAction(e -> {
                try {
                    HouseKeeping selected = table.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        selected.markDirty();
                        table.refresh();
                        
                        // Save to database
                        saveRoomToDB(selected.getRoom());
                        
                        showAlert("Success", "Room " + selected.getRoom().getRoomNumber() + 
                                 " marked as dirty", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Validation Error", "Please select a room from the table", Alert.AlertType.ERROR);
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Failed to update room status: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });

            HBox btnBox = new HBox(10, cleanBtn, dirtyBtn);
            btnBox.setAlignment(Pos.CENTER_LEFT);

            panel.getChildren().addAll(title, table, btnBox);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(panel);
        } catch (Exception ex) {
            showAlert("Error", "Failed to load housekeeping management: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleExit() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("All data has been saved to the database.");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                dbService.close();
                System.exit(0);
            }
        } catch (Exception ex) {
            dbService.close();
            System.exit(0);
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private double getRoomPrice(Room room) {
        try {
            if (room == null || room.getRoomStyle() == null) {
                return 50.0;
            }
            switch (room.getRoomStyle()) {
                case "Single": return 50.0;
                case "Double": return 80.0;
                case "Suite": return 150.0;
                default: return 50.0;
            }
        } catch (Exception ex) {
            return 50.0;
        }
    }

    private double getTotalRevenue() {
        try {
            double total = 0;
            for (Invoice inv : completedInvoices) {
                if (inv != null) {
                    total += inv.getTotalAmount();
                }
            }
            return total;
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
