package edu.uoc.uocycle.view;

import com.google.gson.*;
import edu.uoc.uocycle.controller.UOCycleController;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlayViewController {

    private UOCycleController controller;

    @FXML
    private Pane stationsPane;

    @FXML
    private Label mouseCoordinatesLabel;

    private static final double TOP_LAT    = 41.433240;
    private static final double LEFT_LON   =  2.123448;
    private static final double BOTTOM_LAT = 41.370002;
    private static final double RIGHT_LON  =  2.232561;

    private static final double MAP_WIDTH   = 650.0;
    private static final double MAP_HEIGHT  = 500.0;

    private static final DateTimeFormatter TRIP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private StackPane modalOverlay;
    private BorderPane modalCard;
    private Label stationTitleLabel;
    private FlowPane bikesContainer;
    private ToggleGroup bikesToggleGroup;
    private String selectedStationId;
    private String selectedBicycleId;

    private StackPane userOverlay;
    private BorderPane userCard;
    private TextField userNameField;
    private Label userErrorLabel;

    private StackPane tripOverlay;
    private BorderPane tripCard;
    private Label tripTitleLabel;
    private Label tripSummaryLabel;
    private StackPane tripBikeIconWrapper;
    private Label tripBikeInfoLabel;

    private StackPane tripsOverlay;
    private BorderPane tripsCard;
    private VBox tripsListContainer;
    private Label tripsTitleLabel;

    private Button tripsButton;
    private StackPane tripsButtonContainer;

    private Button selectBikeButton;

    private Label tripStatusLabel;

    private final Map<String, Circle> stationDots = new HashMap<>();

    private Label stationErrorLabel;

    private StackPane destinationOverlay;
    private BorderPane destinationCard;
    private Label destinationTitleLabel;
    private Label destinationMessageLabel;
    private Label destinationErrorLabel;
    private String pendingDestinationStationId;

    private static final double BIKE_TILE_WIDTH = 120;
    private static final double H_GAP = 10;
    private static final double SIDE_PADDING = 12;

    private static final double BIKES_CONTENT_WIDTH =
            SIDE_PADDING * 2 + BIKE_TILE_WIDTH * 3 + H_GAP * 2;

    private static final double MODAL_EXTRA = 24;

    private static final double MODAL_WIDTH = BIKES_CONTENT_WIDTH + MODAL_EXTRA;

    private static final double MODAL_HEIGHT = 420.0;

    @FXML
    public void initialize() {
        try {
            controller = new UOCycleController("stations.txt", "bicycles.txt", "station_bicycles.txt");
        } catch (Exception e) {
            System.err.println("Error initializing UOCycleController: " + e.getMessage());
            return;
        }

        buildUserWindow();
        buildModalWindow();
        buildDestinationWindow();
        buildTripSummaryWindow();
        buildTripsWindow();
        buildTripsButton();

        loadStations();
        initTripStatusLabel();
        updateTripStatusMessage();

        stationsPane.setDisable(true);

        stationsPane.setOnMouseMoved(e -> {
            double lon = xToLongitude(e.getX());
            double lat = yToLatitude(e.getY());

            if (mouseCoordinatesLabel != null) {
                mouseCoordinatesLabel.setText(
                        String.format("lat: %.6f\nlon: %.6f", lat, lon)
                );
            }
        });

        stationsPane.setOnMouseExited(e -> {
            if (mouseCoordinatesLabel != null) {
                mouseCoordinatesLabel.setText("lat: -\nlon: -");
            }
        });

        showUserDialog();
    }

    private void initTripStatusLabel() {
        tripStatusLabel = new Label();
        tripStatusLabel.getStyleClass().add("uoc-trip-status-label");
        tripStatusLabel.setPadding(new Insets(0, 12, 6, 12));
        tripStatusLabel.setVisible(false);

        HBox tripStatusBar = new HBox(tripStatusLabel);
        tripStatusBar.setAlignment(Pos.CENTER);
        tripStatusBar.setFillHeight(false);
        tripStatusBar.setPickOnBounds(false);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(tripStatusBar);
            AnchorPane.setTopAnchor(tripStatusBar, 8.0);
            AnchorPane.setLeftAnchor(tripStatusBar, 0.0);
            AnchorPane.setRightAnchor(tripStatusBar, 0.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(tripStatusBar);
            tripStatusBar.setLayoutY(8);
            tripStatusBar.prefWidthProperty().bind(pane.widthProperty());
        } else {
            stationsPane.getChildren().add(tripStatusBar);
            tripStatusBar.setLayoutY(8);
            tripStatusBar.prefWidthProperty().bind(stationsPane.widthProperty());
        }
    }

    private void updateTripStatusMessage() {
        if (tripStatusLabel == null || controller == null) return;

        if (controller.isTripStarted()) {
            tripStatusLabel.setText("Select your destination");
        } else {
            tripStatusLabel.setText("Select a station to start a trip");
            stationDots.values().forEach(dot -> dot.setDisable(false));
        }
    }

    private void buildUserWindow() {
        userOverlay = new StackPane();
        userOverlay.setVisible(false);
        userOverlay.setPickOnBounds(true);
        userOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        userOverlay.setFocusTraversable(true);

        userCard = new BorderPane();
        userCard.setPrefSize(420, 120);
        userCard.setMaxSize(420, 120);
        userCard.getStyleClass().add("uoc-modal-card");

        StackPane.setAlignment(userCard, Pos.CENTER);
        StackPane.setMargin(userCard, new Insets(8));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 10, 16));
        header.setSpacing(12);

        Label title = new Label("Create user");
        title.getStyleClass().add("uoc-title-secondary");
        HBox.setHgrow(title, Priority.ALWAYS);

        header.getChildren().add(title);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStyleClass().add("uoc-separator");
        VBox.setMargin(sep, new Insets(0, 16, 0, 16));

        VBox center = new VBox(10);
        center.setPadding(new Insets(16, 16, 8, 16));
        center.setAlignment(Pos.TOP_LEFT);

        Label info = new Label("Please enter your user name to start using the system:");
        info.setWrapText(true);

        userNameField = new TextField();
        userNameField.getStyleClass().add("uoc-text-field");
        userNameField.setPromptText("User name");
        userNameField.setPrefWidth(260);
        userNameField.setOnAction(ev -> handleCreateUser());

        userErrorLabel = new Label();
        userErrorLabel.setWrapText(true);
        userErrorLabel.setVisible(false);
        userErrorLabel.setStyle(
                "-fx-text-fill: #d32f2f;" +
                        "-fx-font-size: 11px;"
        );

        center.getChildren().addAll(info, userNameField, userErrorLabel);

        VBox centerWrapper = new VBox(sep, center);

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);
        footer.setPadding(new Insets(10, 16, 16, 16));

        Button continueBtn = new Button("Continue");
        continueBtn.getStyleClass().add("uoc-btn-primary");
        continueBtn.setCursor(Cursor.HAND);
        continueBtn.setDefaultButton(true);
        continueBtn.setPickOnBounds(false);

        StackPane continueBtnContainer = new StackPane();
        continueBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region continueHoverLayer = new Region();
        continueHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        continueHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        continueBtnContainer.getChildren().addAll(continueHoverLayer, continueBtn);
        StackPane.setAlignment(continueHoverLayer, Pos.CENTER);
        StackPane.setAlignment(continueBtn, Pos.CENTER);

        UiEffects.applyHoverTranslate(continueBtn, continueBtnContainer, 4, 4, Duration.millis(120));

        footer.getChildren().add(continueBtnContainer);

        userCard.setTop(header);
        userCard.setCenter(centerWrapper);
        userCard.setBottom(footer);

        userOverlay.getChildren().add(userCard);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(userOverlay);
            AnchorPane.setTopAnchor(userOverlay, 0.0);
            AnchorPane.setRightAnchor(userOverlay, 0.0);
            AnchorPane.setBottomAnchor(userOverlay, 0.0);
            AnchorPane.setLeftAnchor(userOverlay, 0.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(userOverlay);
            userOverlay.prefWidthProperty().bind(pane.widthProperty());
            userOverlay.prefHeightProperty().bind(pane.heightProperty());
        } else {
            stationsPane.getChildren().add(userOverlay);
            userOverlay.prefWidthProperty().bind(stationsPane.widthProperty());
            userOverlay.prefHeightProperty().bind(stationsPane.heightProperty());
        }

        userOverlay.setOnMouseClicked(Event::consume);
        userCard.setOnMouseClicked(Event::consume);
        userOverlay.setOnKeyPressed(Event::consume);

        continueBtn.setOnAction(ev -> handleCreateUser());
    }

    private void showUserDialog() {
        stationsPane.setDisable(true);
        userNameField.clear();

        userErrorLabel.setText("");
        userErrorLabel.setVisible(false);

        if (!userOverlay.isVisible()) {
            userOverlay.setOpacity(0);
            userCard.setScaleX(0.98);
            userCard.setScaleY(0.98);
            userOverlay.setVisible(true);

            FadeTransition ft = new FadeTransition(Duration.millis(150), userOverlay);
            ft.setFromValue(0);
            ft.setToValue(1);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), userCard);
            st.setFromX(0.98);
            st.setFromY(0.98);
            st.setToX(1.0);
            st.setToY(1.0);

            ft.play();
            st.play();
        }

        userOverlay.requestFocus();
    }

    private void handleCreateUser() {
        String name = userNameField.getText() != null ? userNameField.getText().trim() : "";

        userErrorLabel.setText("");
        userErrorLabel.setVisible(false);

        try {
            controller.createUser(name);

            stationsPane.setDisable(false);
            tripStatusLabel.setVisible(true);

            if (tripsButtonContainer != null) {
                tripsButtonContainer.setVisible(true);
            }

            FadeTransition ft = new FadeTransition(Duration.millis(120), userOverlay);
            ft.setFromValue(userOverlay.getOpacity());
            ft.setToValue(0);
            ft.setOnFinished(e -> {
                userOverlay.setVisible(false);
                userOverlay.setOpacity(1);
            });
            ft.play();
        } catch (Exception e) {
            userErrorLabel.setText(e.getMessage());
            userErrorLabel.setVisible(true);
        }
    }

    private void loadStations() {
        if (controller == null) {
            return;
        }

        stationsPane.getChildren().clear();
        stationDots.clear();
        final double RADIUS = 7.0;

        for (Object station : controller.getStations()) {
            final String stationInfo = String.valueOf(station);

            try {
                JsonObject root = JsonParser.parseString(stationInfo).getAsJsonObject();

                Double latitude = findFirstNumberByKey(root, "latitude");
                Double longitude = findFirstNumberByKey(root, "longitude");

                if (latitude == null || longitude == null) {
                    System.err.println("Missing latitude/longitude in station: " + stationInfo);
                    continue;
                }

                double x = longitudeToX(longitude);
                double y = latitudeToY(latitude);

                String id = findFirstStringByKey(root, "id");
                String address = findFirstStringByKey(root, "address");

                String label = firstNonNull(address, id, "(station)");

                Circle dot = new Circle(x, y, RADIUS);
                dot.getStyleClass().add("station-dot");
                dot.setCursor(Cursor.HAND);
                Tooltip.install(dot, new Tooltip(label));

                if (id != null) {
                    stationDots.put(id, dot);
                }

                final String stationId = id;
                final String title = label;

                dot.setOnMouseClicked(e -> {
                    if (stationId == null) return;

                    if (controller.isTripStarted()) {
                        Circle originDot = stationDots.get(stationId);
                        if (originDot != null && originDot.isDisabled()) {
                            return;
                        }
                        showDestinationDialog(stationId, title);
                    } else {
                        try {
                            Object[] bikes = controller.getBicyclesByStation(stationId);
                            showStationDialog(stationId, title, bikes);
                        } catch (Exception ex) {
                            System.err.println("Error fetching bicycles for station " + stationId + ": " + ex.getMessage());
                        }
                    }
                });

                stationsPane.getChildren().add(dot);

            } catch (IllegalArgumentException | JsonSyntaxException ex) {
                System.err.println("Invalid JSON: " + stationInfo + " -> " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Error creating the station in PlayViewController: " + ex.getMessage());
            }
        }
    }

    private Double findFirstNumberByKey(JsonElement element, String targetKey) {
        if (element == null || element.isJsonNull()) return null;

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            if (obj.has(targetKey) && !obj.get(targetKey).isJsonNull()) {
                JsonElement v = obj.get(targetKey);
                Double parsed = tryParseAsDouble(v);
                if (parsed != null) return parsed;
            }

            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                Double found = findFirstNumberByKey(e.getValue(), targetKey);
                if (found != null) return found;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                Double found = findFirstNumberByKey(item, targetKey);
                if (found != null) return found;
            }
        }

        return null;
    }

    private String findFirstStringByKey(JsonElement element, String targetKey) {
        if (element == null || element.isJsonNull()) return null;

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            if (obj.has(targetKey) && !obj.get(targetKey).isJsonNull()) {
                JsonElement v = obj.get(targetKey);
                if (v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
                    String s = v.getAsString();
                    return (s != null && !s.isBlank()) ? s : null;
                }
                if (v.isJsonPrimitive()) {
                    String s = v.getAsString();
                    return (s != null && !s.isBlank()) ? s : null;
                }
            }

            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                String found = findFirstStringByKey(e.getValue(), targetKey);
                if (found != null) return found;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                String found = findFirstStringByKey(item, targetKey);
                if (found != null) return found;
            }
        }

        return null;
    }

    private Double tryParseAsDouble(JsonElement v) {
        try {
            if (v.isJsonPrimitive()) {
                JsonPrimitive p = v.getAsJsonPrimitive();
                if (p.isNumber()) return p.getAsDouble();
                if (p.isString()) {
                    String s = p.getAsString();
                    if (s != null) {
                        s = s.trim();
                        if (!s.isEmpty()) return Double.parseDouble(s);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private double getDouble(JsonObject jo, String key) {
        if (!jo.has(key) || jo.get(key).isJsonNull()) return 0;
        return jo.get(key).getAsDouble();
    }

    private String getString(JsonObject jo, String key) {
        if (!jo.has(key) || jo.get(key).isJsonNull()) return null;
        return jo.get(key).getAsString();
    }

    private boolean getBoolean(JsonObject jo) {
        try {
            if (!jo.has("electric") || jo.get("electric").isJsonNull()) return false;
            return jo.get("electric").getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public static double xToLongitude(double x) {
        double lonRange = RIGHT_LON - LEFT_LON;
        return LEFT_LON + (x / (MAP_WIDTH - 1)) * lonRange;
    }

    public static double yToLatitude(double y) {
        double latRange = TOP_LAT - BOTTOM_LAT;
        return TOP_LAT - (y / (MAP_HEIGHT - 1)) * latRange;
    }

    public static double longitudeToX(double lon) {
        double lonRange = RIGHT_LON - LEFT_LON;
        double ratio = (lon - LEFT_LON) / lonRange;
        return ratio * (MAP_WIDTH - 1);
    }

    public static double latitudeToY(double lat) {
        double latRange = TOP_LAT - BOTTOM_LAT;
        double ratio = (TOP_LAT - lat) / latRange;
        return ratio * (MAP_HEIGHT - 1);
    }

    private String firstNonNull(String... opts) {
        for (String s : opts) if (s != null && !s.isEmpty()) return s;
        return null;
    }

    private String formatDateTime(String raw) {
        if (raw == null || raw.isBlank()) return "-";
        try {
            return TRIP_FORMATTER.format(LocalDateTime.parse(raw));
        } catch (Exception e) {
            return raw;
        }
    }

    private void updateTripsButtonState() {
        if (tripsButton == null) return;

        boolean historyVisible = tripsOverlay != null && tripsOverlay.isVisible();
        boolean tripVisible    = tripOverlay  != null && tripOverlay.isVisible();

        tripsButton.setDisable(historyVisible || tripVisible);
    }

    private void buildModalWindow() {
        modalOverlay = new StackPane();
        modalOverlay.setVisible(false);
        modalOverlay.setPickOnBounds(true);
        modalOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        modalOverlay.setFocusTraversable(true);

        modalCard = new BorderPane();
        modalCard.setPrefSize(MODAL_WIDTH, MODAL_HEIGHT);
        modalCard.setMaxSize(MODAL_WIDTH, MODAL_HEIGHT);

        modalCard.getStyleClass().add("uoc-modal-card");

        StackPane.setAlignment(modalCard, Pos.CENTER);
        StackPane.setMargin(modalCard, new Insets(8));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 10, 16));
        header.setSpacing(12);

        stationTitleLabel = new Label("Station");
        stationTitleLabel.getStyleClass().add("uoc-title-secondary");
        HBox.setHgrow(stationTitleLabel, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setMinSize(32, 32);
        closeBtn.setPrefSize(32, 32);
        closeBtn.setCursor(Cursor.HAND);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 16;" +
                        "-fx-text-fill: #333333;"
        );
        closeBtn.setOnAction(e -> hideStationDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(stationTitleLabel, spacer, closeBtn);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStyleClass().add("uoc-separator");
        VBox.setMargin(sep, new Insets(0, 16, 0, 16));

        final double BIKE_TILE_WIDTH = 120;
        final double H_GAP           = 10;
        final double SIDE_PADDING    = 12;

        final double BIKES_CONTENT_WIDTH =
                SIDE_PADDING * 2 + BIKE_TILE_WIDTH * 3 + H_GAP * 2;

        bikesContainer = new FlowPane(H_GAP, 10);
        bikesContainer.setPadding(new Insets(8, SIDE_PADDING, 8, SIDE_PADDING));
        bikesContainer.setAlignment(Pos.TOP_LEFT);

        bikesContainer.setPrefWrapLength(BIKES_CONTENT_WIDTH);
        bikesContainer.setPrefWidth(BIKES_CONTENT_WIDTH);
        bikesContainer.setMaxWidth(Region.USE_PREF_SIZE);

        ScrollPane bikesScroll = new ScrollPane(bikesContainer);
        bikesScroll.setFitToWidth(false);
        bikesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        bikesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        bikesScroll.setPrefViewportHeight(MODAL_HEIGHT - 160);

        bikesScroll.setPrefViewportWidth(BIKES_CONTENT_WIDTH);
        bikesScroll.setMinViewportWidth(BIKES_CONTENT_WIDTH);
        bikesScroll.setMaxWidth(Region.USE_PREF_SIZE);

        HBox bikesWrapper = new HBox(bikesScroll);
        bikesWrapper.setAlignment(Pos.CENTER);
        bikesWrapper.setPadding(new Insets(0, 0, 0, 0));

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);
        footer.setPadding(new Insets(0, 16, 16, 16));

        StackPane cancelBtnContainer = new StackPane();
        cancelBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region hoverLayer = new Region();
        hoverLayer.getStyleClass().add("uoc-btn-container-white-hover");

        hoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button cancelBtn = new Button("Close");
        cancelBtn.getStyleClass().add("uoc-btn-primary");
        cancelBtn.setPickOnBounds(false);

        cancelBtn.setOnAction(ev -> hideStationDialog());
        cancelBtn.setCancelButton(true);

        cancelBtnContainer.getChildren().addAll(hoverLayer, cancelBtn);

        StackPane.setAlignment(hoverLayer, Pos.CENTER);
        StackPane.setAlignment(cancelBtn, Pos.CENTER);

        UiEffects.applyHoverTranslate(cancelBtn, cancelBtnContainer, 4, 4, Duration.millis(120));

        selectBikeButton = createSelectBikeButton();
        selectBikeButton.getStyleClass().add("uoc-btn-primary");
        selectBikeButton.setPickOnBounds(false);
        selectBikeButton.setDisable(true);

        stationErrorLabel = new Label();
        stationErrorLabel.setWrapText(true);
        stationErrorLabel.setVisible(false);
        stationErrorLabel.setStyle(
                "-fx-text-fill: #d32f2f;" +
                        "-fx-font-size: 11px;"
        );

        StackPane confirmBtnContainer = new StackPane();
        confirmBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region confirmHoverLayer = new Region();
        confirmHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        confirmHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        confirmBtnContainer.getChildren().addAll(confirmHoverLayer, selectBikeButton);
        StackPane.setAlignment(confirmHoverLayer, Pos.CENTER);
        StackPane.setAlignment(selectBikeButton, Pos.CENTER);

        confirmBtnContainer.mouseTransparentProperty().bind(selectBikeButton.disabledProperty());

        UiEffects.applyHoverTranslate(selectBikeButton, confirmBtnContainer, 4, 4, Duration.millis(120));

        footer.getChildren().addAll(cancelBtnContainer, confirmBtnContainer, stationErrorLabel);

        VBox center = new VBox(sep, bikesWrapper);
        VBox.setVgrow(bikesWrapper, Priority.ALWAYS);

        modalCard.setTop(header);
        modalCard.setCenter(center);
        modalCard.setBottom(footer);

        modalOverlay.getChildren().add(modalCard);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(modalOverlay);
            AnchorPane.setTopAnchor(modalOverlay, 0.0);
            AnchorPane.setRightAnchor(modalOverlay, 0.0);
            AnchorPane.setBottomAnchor(modalOverlay, 0.0);
            AnchorPane.setLeftAnchor(modalOverlay, 0.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(modalOverlay);
            modalOverlay.prefWidthProperty().bind(pane.widthProperty());
            modalOverlay.prefHeightProperty().bind(pane.heightProperty());
        } else {
            stationsPane.getChildren().add(modalOverlay);
            modalOverlay.prefWidthProperty().bind(stationsPane.widthProperty());
            modalOverlay.prefHeightProperty().bind(stationsPane.heightProperty());
        }

        modalOverlay.setOnMouseClicked(Event::consume);
        modalCard.setOnMouseClicked(Event::consume);

        modalOverlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                hideStationDialog();
            }
        });
        modalOverlay.visibleProperty().addListener((obs, oldV, vis) -> {
            if (vis) modalOverlay.requestFocus();
        });
    }

    private void buildDestinationWindow() {
        destinationOverlay = new StackPane();
        destinationOverlay.setVisible(false);
        destinationOverlay.setPickOnBounds(true);
        destinationOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        destinationOverlay.setFocusTraversable(true);

        destinationCard = new BorderPane();
        destinationCard.setPrefSize(380, 160);
        destinationCard.setMaxSize(380, 160);
        destinationCard.getStyleClass().add("uoc-modal-card");

        StackPane.setAlignment(destinationCard, Pos.CENTER);
        StackPane.setMargin(destinationCard, new Insets(8));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 10, 16));
        header.setSpacing(12);

        destinationTitleLabel = new Label("Destination");
        destinationTitleLabel.getStyleClass().add("uoc-title-secondary");
        HBox.setHgrow(destinationTitleLabel, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setMinSize(32, 32);
        closeBtn.setPrefSize(32, 32);
        closeBtn.setCursor(Cursor.HAND);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 16;" +
                        "-fx-text-fill: #333333;"
        );
        closeBtn.setOnAction(e -> hideDestinationDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(destinationTitleLabel, spacer, closeBtn);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStyleClass().add("uoc-separator");
        VBox.setMargin(sep, new Insets(0, 16, 0, 16));

        VBox center = new VBox(10);
        center.setPadding(new Insets(16, 16, 8, 16));
        center.setAlignment(Pos.TOP_LEFT);

        destinationMessageLabel = new Label();
        destinationMessageLabel.setWrapText(true);

        destinationErrorLabel = new Label();
        destinationErrorLabel.setWrapText(true);
        destinationErrorLabel.setVisible(false);
        destinationErrorLabel.setStyle(
                "-fx-text-fill: #d32f2f;" +
                        "-fx-font-size: 11px;"
        );

        center.getChildren().addAll(destinationMessageLabel, destinationErrorLabel);

        VBox centerWrapper = new VBox(sep, center);
        VBox.setVgrow(center, Priority.ALWAYS);

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);
        footer.setPadding(new Insets(10, 16, 16, 16));

        StackPane cancelBtnContainer = new StackPane();
        cancelBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region cancelHoverLayer = new Region();
        cancelHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        cancelHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("uoc-btn-primary");
        cancelBtn.setPickOnBounds(false);
        cancelBtn.setOnAction(ev -> hideDestinationDialog());

        cancelBtnContainer.getChildren().addAll(cancelHoverLayer, cancelBtn);
        StackPane.setAlignment(cancelHoverLayer, Pos.CENTER);
        StackPane.setAlignment(cancelBtn, Pos.CENTER);

        UiEffects.applyHoverTranslate(cancelBtn, cancelBtnContainer, 4, 4, Duration.millis(120));

        Button confirmDestinationButton = new Button("Select");
        confirmDestinationButton.getStyleClass().add("uoc-btn-primary");
        confirmDestinationButton.setCursor(Cursor.HAND);
        confirmDestinationButton.setPickOnBounds(false);

        StackPane confirmBtnContainer = new StackPane();
        confirmBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region confirmHoverLayer = new Region();
        confirmHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        confirmHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        confirmBtnContainer.getChildren().addAll(confirmHoverLayer, confirmDestinationButton);
        StackPane.setAlignment(confirmHoverLayer, Pos.CENTER);
        StackPane.setAlignment(confirmDestinationButton, Pos.CENTER);

        UiEffects.applyHoverTranslate(confirmDestinationButton, confirmBtnContainer, 4, 4, Duration.millis(120));

        confirmDestinationButton.setOnAction(ev -> handleConfirmDestination());

        footer.getChildren().addAll(cancelBtnContainer, confirmBtnContainer);

        destinationCard.setTop(header);
        destinationCard.setCenter(centerWrapper);
        destinationCard.setBottom(footer);

        destinationOverlay.getChildren().add(destinationCard);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(destinationOverlay);
            AnchorPane.setTopAnchor(destinationOverlay, 0.0);
            AnchorPane.setRightAnchor(destinationOverlay, 0.0);
            AnchorPane.setBottomAnchor(destinationOverlay, 0.0);
            AnchorPane.setLeftAnchor(destinationOverlay, 0.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(destinationOverlay);
            destinationOverlay.prefWidthProperty().bind(pane.widthProperty());
            destinationOverlay.prefHeightProperty().bind(pane.heightProperty());
        } else {
            stationsPane.getChildren().add(destinationOverlay);
            destinationOverlay.prefWidthProperty().bind(stationsPane.widthProperty());
            destinationOverlay.prefHeightProperty().bind(stationsPane.heightProperty());
        }

        destinationOverlay.setOnMouseClicked(Event::consume);
        destinationCard.setOnMouseClicked(Event::consume);

        destinationOverlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                hideDestinationDialog();
            }
        });
        destinationOverlay.visibleProperty().addListener((obs, oldV, vis) -> {
            if (vis) destinationOverlay.requestFocus();
        });
    }

    private void handleConfirmDestination() {
        if (pendingDestinationStationId != null) {
            try {
                String jsonTrip = controller.endTrip(pendingDestinationStationId);

                hideDestinationDialog();
                updateTripStatusMessage();

                if (jsonTrip != null && !jsonTrip.isBlank()) {
                    showTripSummary(jsonTrip);
                }
            } catch (Exception e) {
                destinationErrorLabel.setText(e.getMessage());
                destinationErrorLabel.setVisible(true);
            }
        }
    }

    private void showDestinationDialog(String stationId, String stationTitle) {
        pendingDestinationStationId = stationId;
        destinationTitleLabel.setText(stationTitle);
        destinationMessageLabel.setText(
                "Do you want to set this station as your trip destination?"
        );

        if (destinationErrorLabel != null) {
            destinationErrorLabel.setText("");
            destinationErrorLabel.setVisible(false);
        }

        if (!destinationOverlay.isVisible()) {
            destinationOverlay.setOpacity(0);
            destinationCard.setScaleX(0.98);
            destinationCard.setScaleY(0.98);
            destinationOverlay.setVisible(true);

            FadeTransition ft = new FadeTransition(Duration.millis(150), destinationOverlay);
            ft.setFromValue(0);
            ft.setToValue(1);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), destinationCard);
            st.setFromX(0.98);
            st.setFromY(0.98);
            st.setToX(1.0);
            st.setToY(1.0);

            ft.play();
            st.play();
        }
    }

    private void hideDestinationDialog() {
        if (!destinationOverlay.isVisible()) return;

        FadeTransition ft = new FadeTransition(Duration.millis(120), destinationOverlay);
        ft.setFromValue(destinationOverlay.getOpacity());
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            destinationOverlay.setVisible(false);
            destinationOverlay.setOpacity(1);
            pendingDestinationStationId = null;
        });
        ft.play();
    }

    private void buildTripSummaryWindow() {
        tripOverlay = new StackPane();
        tripOverlay.setVisible(false);
        tripOverlay.setPickOnBounds(true);
        tripOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        tripOverlay.setFocusTraversable(true);

        tripCard = new BorderPane();
        tripCard.setPrefWidth(460);
        tripCard.setMaxWidth(460);
        tripCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
        tripCard.setMaxHeight(Region.USE_PREF_SIZE);
        tripCard.getStyleClass().add("uoc-modal-card");

        StackPane.setAlignment(tripCard, Pos.CENTER);
        StackPane.setMargin(tripCard, new Insets(8));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 10, 16));
        header.setSpacing(12);

        tripTitleLabel = new Label("Trip summary");
        tripTitleLabel.getStyleClass().add("uoc-title-secondary");
        HBox.setHgrow(tripTitleLabel, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setMinSize(32, 32);
        closeBtn.setPrefSize(32, 32);
        closeBtn.setCursor(Cursor.HAND);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 16;" +
                        "-fx-text-fill: #333333;"
        );
        closeBtn.setOnAction(e -> hideTripSummaryDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(tripTitleLabel, spacer, closeBtn);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStyleClass().add("uoc-separator");
        VBox.setMargin(sep, new Insets(0, 16, 0, 16));

        VBox center = new VBox(12);
        center.setPadding(new Insets(16, 16, 8, 16));
        center.setAlignment(Pos.TOP_LEFT);

        tripBikeIconWrapper = new StackPane();
        tripBikeIconWrapper.setPrefSize(90, 70);
        tripBikeIconWrapper.setMaxSize(90, 70);

        tripBikeInfoLabel = new Label();
        tripBikeInfoLabel.setWrapText(true);

        HBox bikeRow = new HBox(12);
        bikeRow.setAlignment(Pos.CENTER_LEFT);
        bikeRow.getChildren().addAll(tripBikeIconWrapper, tripBikeInfoLabel);

        tripSummaryLabel = new Label();
        tripSummaryLabel.setWrapText(true);

        center.getChildren().addAll(bikeRow, tripSummaryLabel);

        VBox centerWrapper = new VBox(sep, center);
        VBox.setVgrow(center, Priority.ALWAYS);

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);
        footer.setPadding(new Insets(10, 16, 16, 16));

        StackPane historyBtnContainer = new StackPane();
        historyBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region historyHoverLayer = new Region();
        historyHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        historyHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button tripHistoryButton = new Button("Trips history");
        tripHistoryButton.getStyleClass().add("uoc-btn-primary");
        tripHistoryButton.setPickOnBounds(false);
        tripHistoryButton.setCursor(Cursor.HAND);

        historyBtnContainer.getChildren().addAll(historyHoverLayer, tripHistoryButton);
        StackPane.setAlignment(historyHoverLayer, Pos.CENTER);
        StackPane.setAlignment(tripHistoryButton, Pos.CENTER);

        UiEffects.applyHoverTranslate(tripHistoryButton, historyBtnContainer, 4, 4, Duration.millis(120));

        tripHistoryButton.setOnAction(ev -> {
            if (tripOverlay != null && tripOverlay.isVisible()) {
                FadeTransition ft = new FadeTransition(Duration.millis(120), tripOverlay);
                ft.setFromValue(tripOverlay.getOpacity());
                ft.setToValue(0);
                ft.setOnFinished(e -> {
                    tripOverlay.setVisible(false);
                    tripOverlay.setOpacity(1);
                    tripSummaryLabel.setText("");
                    showTripsListDialog();
                });
                ft.play();
            } else {
                showTripsListDialog();
            }
        });

        StackPane closeBtnContainer = new StackPane();
        closeBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region closeHoverLayer = new Region();
        closeHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        closeHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button okBtn = new Button("Close");
        okBtn.getStyleClass().add("uoc-btn-primary");
        okBtn.setPickOnBounds(false);
        okBtn.setOnAction(ev -> hideTripSummaryDialog());

        closeBtnContainer.getChildren().addAll(closeHoverLayer, okBtn);
        StackPane.setAlignment(closeHoverLayer, Pos.CENTER);
        StackPane.setAlignment(okBtn, Pos.CENTER);

        UiEffects.applyHoverTranslate(okBtn, closeBtnContainer, 4, 4, Duration.millis(120));

        footer.getChildren().addAll(historyBtnContainer, closeBtnContainer);

        tripCard.setTop(header);
        tripCard.setCenter(centerWrapper);
        tripCard.setBottom(footer);

        tripOverlay.getChildren().add(tripCard);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(tripOverlay);
            AnchorPane.setTopAnchor(tripOverlay, 0.0);
            AnchorPane.setRightAnchor(tripOverlay, 0.0);
            AnchorPane.setBottomAnchor(tripOverlay, 0.0);
            AnchorPane.setLeftAnchor(tripOverlay, 0.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(tripOverlay);
            tripOverlay.prefWidthProperty().bind(pane.widthProperty());
            tripOverlay.prefHeightProperty().bind(pane.heightProperty());
        } else {
            stationsPane.getChildren().add(tripOverlay);
            tripOverlay.prefWidthProperty().bind(stationsPane.widthProperty());
            tripOverlay.prefHeightProperty().bind(stationsPane.heightProperty());
        }

        tripOverlay.setOnMouseClicked(Event::consume);
        tripCard.setOnMouseClicked(Event::consume);

        tripOverlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                hideTripSummaryDialog();
            }
        });
        tripOverlay.visibleProperty().addListener((obs, oldV, vis) -> {
            if (vis) tripOverlay.requestFocus();
            updateTripsButtonState();
        });
    }

    private void showTripSummary(String jsonTrip) {
        if (jsonTrip == null || jsonTrip.isBlank()) {
            return;
        }

        if (tripsOverlay != null && tripsOverlay.isVisible()) {
            tripsOverlay.setVisible(false);
            tripsOverlay.setOpacity(1);
            if (tripsListContainer != null) {
                tripsListContainer.getChildren().clear();
            }
        }

        try {
            JsonObject root = JsonParser.parseString(jsonTrip).getAsJsonObject();

            String bikeId = "-";
            boolean isElectric = false;

            if (root.has("bicycle") && root.get("bicycle").isJsonObject()) {
                JsonObject bike = root.getAsJsonObject("bicycle");

                if (bike.has("id") && !bike.get("id").isJsonNull()) {
                    bikeId = bike.get("id").getAsString();
                }

                if (bike.has("electric")) {
                    isElectric = getBoolean(bike);
                } else {
                    String type = firstNonNull(
                            getString(bike, "type"),
                            getString(bike, "bicycleType"),
                            getString(bike, "kind"),
                            ""
                    );
                    if (type != null) {
                        String norm = type.trim().toUpperCase();
                        isElectric = norm.contains("ELECTRIC") || norm.equals("E");
                    }
                }
            }

            if (tripBikeIconWrapper != null) {
                tripBikeIconWrapper.getChildren().clear();
                Node bikeIcon = createBikeIcon(isElectric);
                tripBikeIconWrapper.getChildren().add(bikeIcon);
            }

            if (tripBikeInfoLabel != null) {
                String bikeTypeText = isElectric ? "Electric bicycle" : "Mechanical bicycle";
                tripBikeInfoLabel.setText(
                        bikeTypeText + "\nID: " + bikeId
                );
            }

            String startLabel = "-";
            if (root.has("startStation") && root.get("startStation").isJsonObject()) {
                JsonObject startStation = root.getAsJsonObject("startStation");
                String sId = startStation.has("id") && !startStation.get("id").isJsonNull()
                        ? startStation.get("id").getAsString()
                        : null;

                String sAddress = null;
                if (startStation.has("location") && startStation.get("location").isJsonObject()) {
                    JsonObject loc = startStation.getAsJsonObject("location");
                    if (loc.has("address") && !loc.get("address").isJsonNull()) {
                        sAddress = loc.get("address").getAsString();
                    }
                }
                startLabel = firstNonNull(sAddress, sId, "-");
            }

            String endLabel = "-";
            if (root.has("endStation") && root.get("endStation").isJsonObject()) {
                JsonObject endStation = root.getAsJsonObject("endStation");
                String eId = endStation.has("id") && !endStation.get("id").isJsonNull()
                        ? endStation.get("id").getAsString()
                        : null;

                String eAddress = null;
                if (endStation.has("location") && endStation.get("location").isJsonObject()) {
                    JsonObject loc = endStation.getAsJsonObject("location");
                    if (loc.has("address") && !loc.get("address").isJsonNull()) {
                        eAddress = loc.get("address").getAsString();
                    }
                }
                endLabel = firstNonNull(eAddress, eId, "-");
            }

            String startTime = root.has("startTime") && !root.get("startTime").isJsonNull()
                    ? root.get("startTime").getAsString()
                    : null;

            String endTime = root.has("endTime") && !root.get("endTime").isJsonNull()
                    ? root.get("endTime").getAsString()
                    : null;

            Double distance = null;
            if (root.has("distance") && !root.get("distance").isJsonNull()) {
                distance = root.get("distance").getAsDouble();
            }

            tripTitleLabel.setText("Trip summary");

            StringBuilder sb = new StringBuilder();
            sb.append("From: ").append(startLabel).append("\n");
            sb.append("To: ").append(endLabel).append("\n");

            if (startTime != null) {
                sb.append("Start time: ").append(formatDateTime(startTime)).append("\n");
            }
            if (endTime != null) {
                sb.append("End time: ").append(formatDateTime(endTime)).append("\n");
            }
            if (distance != null) {
                sb.append("Distance: ").append(String.format("%.2f", distance)).append(" km");
            }

            tripSummaryLabel.setText(sb.toString());

            if (!tripOverlay.isVisible()) {
                tripOverlay.setOpacity(0);
                tripCard.setScaleX(0.98);
                tripCard.setScaleY(0.98);
                tripOverlay.setVisible(true);

                FadeTransition ft = new FadeTransition(Duration.millis(150), tripOverlay);
                ft.setFromValue(0);
                ft.setToValue(1);

                ScaleTransition st = new ScaleTransition(Duration.millis(150), tripCard);
                st.setFromX(0.98);
                st.setFromY(0.98);
                st.setToX(1.0);
                st.setToY(1.0);

                ft.play();
                st.play();
            }

        } catch (Exception ex) {
            System.err.println("Error parsing trip JSON: " + ex.getMessage());
        }
    }

    private void hideTripSummaryDialog() {
        if (tripOverlay == null || !tripOverlay.isVisible()) return;

        FadeTransition ft = new FadeTransition(Duration.millis(120), tripOverlay);
        ft.setFromValue(tripOverlay.getOpacity());
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            tripOverlay.setVisible(false);
            tripOverlay.setOpacity(1);
            tripSummaryLabel.setText("");
        });
        ft.play();
    }

    private void buildTripsButton() {
        tripsButton = new Button("Trips");
        tripsButton.getStyleClass().add("uoc-btn-primary");
        tripsButton.setCursor(Cursor.HAND);
        tripsButton.setPickOnBounds(false);

        tripsButtonContainer = new StackPane();
        tripsButtonContainer.getStyleClass().add("uoc-btn-primary-container");

        Region hoverLayer = new Region();
        hoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        hoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        tripsButtonContainer.getChildren().addAll(hoverLayer, tripsButton);
        StackPane.setAlignment(hoverLayer, Pos.CENTER);
        StackPane.setAlignment(tripsButton, Pos.CENTER);

        tripsButtonContainer.mouseTransparentProperty().bind(tripsButton.disabledProperty());

        UiEffects.applyHoverTranslate(tripsButton, tripsButtonContainer, 4, 4, Duration.millis(120));

        tripsButton.setOnAction(ev -> showTripsListDialog());

        tripsButtonContainer.setVisible(false);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(tripsButtonContainer);
            AnchorPane.setBottomAnchor(tripsButtonContainer, 16.0);
            AnchorPane.setRightAnchor(tripsButtonContainer, 16.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(tripsButtonContainer);
            tripsButtonContainer.layoutXProperty().bind(
                    pane.widthProperty().subtract(tripsButtonContainer.widthProperty()).subtract(16)
            );
            tripsButtonContainer.layoutYProperty().bind(
                    pane.heightProperty().subtract(tripsButtonContainer.heightProperty()).subtract(16)
            );
        } else {
            stationsPane.getChildren().add(tripsButtonContainer);
            tripsButtonContainer.layoutXProperty().bind(
                    stationsPane.widthProperty().subtract(tripsButtonContainer.widthProperty()).subtract(16)
            );
            tripsButtonContainer.layoutYProperty().bind(
                    stationsPane.heightProperty().subtract(tripsButtonContainer.heightProperty()).subtract(16)
            );
        }

        updateTripsButtonState();
    }

    private void buildTripsWindow() {
        tripsOverlay = new StackPane();
        tripsOverlay.setVisible(false);
        tripsOverlay.setPickOnBounds(true);
        tripsOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        tripsOverlay.setFocusTraversable(true);

        tripsCard = new BorderPane();
        tripsCard.setPrefWidth(520);
        tripsCard.setMaxWidth(520);
        tripsCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
        tripsCard.setMaxHeight(Region.USE_PREF_SIZE);
        tripsCard.getStyleClass().add("uoc-modal-card");

        StackPane.setAlignment(tripsCard, Pos.CENTER);
        StackPane.setMargin(tripsCard, new Insets(8));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 10, 16));
        header.setSpacing(12);

        tripsTitleLabel = new Label("Trips history");
        tripsTitleLabel.getStyleClass().add("uoc-title-secondary");
        HBox.setHgrow(tripsTitleLabel, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setMinSize(32, 32);
        closeBtn.setPrefSize(32, 32);
        closeBtn.setCursor(Cursor.HAND);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 16;" +
                        "-fx-text-fill: #333333;"
        );
        closeBtn.setOnAction(e -> hideTripsListDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(tripsTitleLabel, spacer, closeBtn);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.getStyleClass().add("uoc-separator");
        VBox.setMargin(sep, new Insets(0, 16, 0, 16));

        tripsListContainer = new VBox(6);
        tripsListContainer.setPadding(new Insets(8, 16, 12, 16));
        tripsListContainer.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(tripsListContainer);
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(240);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        tripsListContainer.setStyle("-fx-background-color: transparent;");

        VBox centerWrapper = new VBox(sep, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);
        footer.setPadding(new Insets(10, 16, 16, 16));

        StackPane closeBtnContainer = new StackPane();
        closeBtnContainer.getStyleClass().add("uoc-btn-primary-container");

        Region closeHoverLayer = new Region();
        closeHoverLayer.getStyleClass().add("uoc-btn-container-white-hover");
        closeHoverLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button okBtn = new Button("Close");
        okBtn.getStyleClass().add("uoc-btn-primary");
        okBtn.setPickOnBounds(false);
        okBtn.setOnAction(ev -> hideTripsListDialog());

        closeBtnContainer.getChildren().addAll(closeHoverLayer, okBtn);
        StackPane.setAlignment(closeHoverLayer, Pos.CENTER);
        StackPane.setAlignment(okBtn, Pos.CENTER);

        UiEffects.applyHoverTranslate(okBtn, closeBtnContainer, 4, 4, Duration.millis(120));

        footer.getChildren().add(closeBtnContainer);

        tripsCard.setTop(header);
        tripsCard.setCenter(centerWrapper);
        tripsCard.setBottom(footer);

        tripsOverlay.getChildren().add(tripsCard);

        Node parent = stationsPane.getParent();
        if (parent instanceof AnchorPane anchor) {
            anchor.getChildren().add(tripsOverlay);
            AnchorPane.setTopAnchor(tripsOverlay, 0.0);
            AnchorPane.setRightAnchor(tripsOverlay, 0.0);
            AnchorPane.setBottomAnchor(tripsOverlay, 0.0);
            AnchorPane.setLeftAnchor(tripsOverlay, 0.0);
        } else if (parent instanceof Pane pane) {
            pane.getChildren().add(tripsOverlay);
            tripsOverlay.prefWidthProperty().bind(pane.widthProperty());
            tripsOverlay.prefHeightProperty().bind(pane.heightProperty());
        } else {
            stationsPane.getChildren().add(tripsOverlay);
            tripsOverlay.prefWidthProperty().bind(stationsPane.widthProperty());
            tripsOverlay.prefHeightProperty().bind(stationsPane.heightProperty());
        }

        tripsOverlay.setOnMouseClicked(Event::consume);
        tripsCard.setOnMouseClicked(Event::consume);

        tripsOverlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                hideTripsListDialog();
            }
        });
        tripsOverlay.visibleProperty().addListener((obs, oldV, vis) -> {
            if (vis) tripsOverlay.requestFocus();
            updateTripsButtonState();
        });
    }

    private void showTripsListDialog() {
        String username = null;
        if (controller != null) {
            username = controller.getUserName();
        }

        if (tripsTitleLabel != null) {
            if (username != null && !username.isBlank()) {
                tripsTitleLabel.setText("Trips history of " + username);
            } else {
                tripsTitleLabel.setText("Trips history");
            }
        }

        populateTripsList();

        if (!tripsOverlay.isVisible()) {
            tripsOverlay.setOpacity(0);
            tripsCard.setScaleX(0.98);
            tripsCard.setScaleY(0.98);
            tripsOverlay.setVisible(true);

            FadeTransition ft = new FadeTransition(Duration.millis(150), tripsOverlay);
            ft.setFromValue(0);
            ft.setToValue(1);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), tripsCard);
            st.setFromX(0.98);
            st.setFromY(0.98);
            st.setToX(1.0);
            st.setToY(1.0);

            ft.play();
            st.play();
        }
    }

    private void hideTripsListDialog() {
        if (tripsOverlay == null || !tripsOverlay.isVisible()) return;

        FadeTransition ft = new FadeTransition(Duration.millis(120), tripsOverlay);
        ft.setFromValue(tripsOverlay.getOpacity());
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            tripsOverlay.setVisible(false);
            tripsOverlay.setOpacity(1);
            tripsListContainer.getChildren().clear();
        });
        ft.play();
    }

    private void populateTripsList() {
        tripsListContainer.getChildren().clear();

        if (controller == null) {
            return;
        }

        Object[] trips = controller.getTrips();

        if (trips == null || trips.length == 0) {
            Label empty = new Label("No trips have been made yet.");
            empty.setStyle("-fx-text-fill: #666666;");
            empty.setWrapText(true);
            tripsListContainer.getChildren().add(empty);
            return;
        }

        for (int i = trips.length - 1; i >= 0; i--) {
            String jsonTrip = String.valueOf(trips[i]);
            try {
                Node row = createTripRow(jsonTrip);
                tripsListContainer.getChildren().add(row);
            } catch (Exception ex) {
                System.err.println("Invalid trip JSON in list: " + ex.getMessage());
            }
        }
    }

    private Node createTripRow(String jsonTrip) {
        JsonObject root = JsonParser.parseString(jsonTrip).getAsJsonObject();

        boolean isElectric = false;

        if (root.has("bicycle") && root.get("bicycle").isJsonObject()) {
            JsonObject bike = root.getAsJsonObject("bicycle");

            if (bike.has("electric")) {
                isElectric = getBoolean(bike);
            } else {
                String type = firstNonNull(
                        getString(bike, "type"),
                        getString(bike, "bicycleType"),
                        getString(bike, "kind"),
                        ""
                );
                if (type != null) {
                    String norm = type.trim().toUpperCase();
                    isElectric = norm.contains("ELECTRIC") || norm.equals("E");
                }
            }
        }

        Node bikeIcon = createBikeIcon(isElectric);

        String startLabel = "-";
        if (root.has("startStation") && root.get("startStation").isJsonObject()) {
            JsonObject startStation = root.getAsJsonObject("startStation");
            String sId = startStation.has("id") && !startStation.get("id").isJsonNull()
                    ? startStation.get("id").getAsString()
                    : null;

            String sAddress = null;
            if (startStation.has("location") && startStation.get("location").isJsonObject()) {
                JsonObject loc = startStation.getAsJsonObject("location");
                if (loc.has("address") && !loc.get("address").isJsonNull()) {
                    sAddress = loc.get("address").getAsString();
                }
            }
            startLabel = firstNonNull(sAddress, sId, "-");
        }

        String endLabel = "-";
        if (root.has("endStation") && root.get("endStation").isJsonObject()) {
            JsonObject endStation = root.getAsJsonObject("endStation");
            String eId = endStation.has("id") && !endStation.get("id").isJsonNull()
                    ? endStation.get("id").getAsString()
                    : null;

            String eAddress = null;
            if (endStation.has("location") && endStation.get("location").isJsonObject()) {
                JsonObject loc = endStation.getAsJsonObject("location");
                if (loc.has("address") && !loc.get("address").isJsonNull()) {
                    eAddress = loc.get("address").getAsString();
                }
            }
            endLabel = firstNonNull(eAddress, eId, "-");
        }

        String startTime = formatDateTime(
                root.has("startTime") && !root.get("startTime").isJsonNull()
                        ? root.get("startTime").getAsString()
                        : null
        );

        String endTime = formatDateTime(
                root.has("endTime") && !root.get("endTime").isJsonNull()
                        ? root.get("endTime").getAsString()
                        : null
        );

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 10, 8, 10));
        row.setCursor(Cursor.HAND);

        StackPane iconWrapper = new StackPane(bikeIcon);
        iconWrapper.setPrefSize(90, 70);
        iconWrapper.setMaxSize(90, 70);

        Label line1 = new Label("From: " + startLabel + "   →   To: " + endLabel);
        Label line2 = new Label("Start: " + startTime + "   End: " + endTime);
        line1.setWrapText(true);
        line2.setWrapText(true);

        line1.setStyle("-fx-text-fill: #333333;");
        line2.setStyle("-fx-text-fill: #333333;");

        VBox textBox = new VBox(4, line1, line2);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        row.getChildren().addAll(iconWrapper, textBox);

        row.setMaxWidth(Double.MAX_VALUE);

        row.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: transparent;"
        );

        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: #e8f0fe;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #4a7afe;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: transparent;"
        ));

        row.setOnMouseClicked(e -> showTripSummary(jsonTrip));

        return row;
    }

    private Button createSelectBikeButton() {
        Button confirmBtn = new Button("Select bike");
        confirmBtn.setCursor(Cursor.HAND);
        confirmBtn.setOnAction(ev -> {
            boolean wasTripStarted = controller.isTripStarted();

            try {
                controller.startTrip(selectedStationId, selectedBicycleId);
            } catch (Exception e) {
                stationErrorLabel.setText(e.getMessage());
                stationErrorLabel.setVisible(true);
                return;
            }

            if (!wasTripStarted) {
                Circle originDot = stationDots.get(selectedStationId);
                if (originDot != null) {
                    originDot.setDisable(true);
                }
            }

            updateTripStatusMessage();
            hideStationDialog();
        });
        confirmBtn.setDefaultButton(true);
        return confirmBtn;
    }

    private void showStationDialog(String stationId, String stationTitle, Object[] bicycles) {
        selectedStationId = stationId;
        stationTitleLabel.setText(stationTitle);

        selectedBicycleId = null;
        if (selectBikeButton != null) {
            selectBikeButton.setDisable(true);
        }

        populateBicycles(bicycles);

        if (!modalOverlay.isVisible()) {
            modalOverlay.setOpacity(0);
            modalCard.setScaleX(0.98);
            modalCard.setScaleY(0.98);
            modalOverlay.setVisible(true);

            FadeTransition ft = new FadeTransition(Duration.millis(150), modalOverlay);
            ft.setFromValue(0);
            ft.setToValue(1);

            ScaleTransition st = new ScaleTransition(Duration.millis(150), modalCard);
            st.setFromX(0.98);
            st.setFromY(0.98);
            st.setToX(1.0);
            st.setToY(1.0);

            ft.play();
            st.play();
        }
    }

    private void hideStationDialog() {
        if (!modalOverlay.isVisible()) return;

        FadeTransition ft = new FadeTransition(Duration.millis(120), modalOverlay);
        ft.setFromValue(modalOverlay.getOpacity());
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            modalOverlay.setVisible(false);
            modalOverlay.setOpacity(1);
            selectedBicycleId = null;
            bikesContainer.getChildren().clear();
            if (selectBikeButton != null) {
                selectBikeButton.setDisable(true);
            }
        });
        ft.play();
    }

    private void populateBicycles(Object[] bicycles) {
        bikesContainer.getChildren().clear();
        bikesToggleGroup = new ToggleGroup();
        selectedBicycleId = null;

        if (selectBikeButton != null) {
            selectBikeButton.setDisable(true);
        }

        if (bicycles == null || bicycles.length == 0) {
            Label empty = new Label("There are no bicycles available at this station.");
            empty.setWrapText(true);
            empty.setStyle("-fx-text-fill: #666666;");
            bikesContainer.getChildren().add(empty);
            return;
        }

        Arrays.stream(bicycles).forEach(b -> {
            String bikeInfo = String.valueOf(b);
            try {
                JsonObject root = JsonParser.parseString(bikeInfo).getAsJsonObject();

                String id = firstNonNull(
                        getString(root, "id"),
                        getString(root, "bikeId"),
                        getString(root, "code"),
                        "(bike)"
                );

                boolean isElectric;
                if (root.has("electric")) {
                    isElectric = getBoolean(root);
                } else {
                    String type = firstNonNull(
                            getString(root, "type"),
                            getString(root, "bicycleType"),
                            getString(root, "kind"),
                            ""
                    );
                    assert type != null;
                    String norm = type.trim().toUpperCase();
                    isElectric = norm.contains("ELECTRIC") || norm.equals("E");
                }

                String tooltipText = buildBikeTooltip(root, isElectric);

                Node bikeIcon = createBikeIcon(isElectric);
                ToggleButton item = createBikeToggle(id, bikeIcon, tooltipText);
                item.setToggleGroup(bikesToggleGroup);

                item.setOnAction(ev -> {
                    selectedBicycleId = item.isSelected() ? id : null;
                    if (selectBikeButton != null) {
                        selectBikeButton.setDisable(selectedBicycleId == null);
                    }
                });

                bikesContainer.getChildren().add(item);
            } catch (Exception ex) {
                System.err.println("Invalid bicycle JSON: " + bikeInfo + " -> " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        });
    }


    private String buildBikeTooltip(JsonObject root, boolean isElectric) {
        StringBuilder sb = new StringBuilder();
        sb.append(isElectric ? "Electric" : "Mechanical");

        if (root == null) return sb.toString();

        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if ("type".equalsIgnoreCase(key) || "bicycleType".equalsIgnoreCase(key) || "kind".equalsIgnoreCase(key)) {
                continue;
            }

            if (value == null || value.isJsonNull()) {
                continue;
            }

            if (value.isJsonPrimitive()) {
                sb.append("\n").append(formatLabel(key)).append(": ").append(primitiveToString(value));
            } else if (value.isJsonObject()) {
                sb.append("\n\n[").append(formatSectionTitle(key)).append("]");
                appendObject(sb, value.getAsJsonObject(), 0);
            } else if (value.isJsonArray()) {
                sb.append("\n").append(formatLabel(key)).append(": ").append(arrayToCompactString(value.getAsJsonArray()));
            }
        }

        return sb.toString();
    }

    private void appendObject(StringBuilder sb, JsonObject obj, int indentLevel) {
        String indent = "  ".repeat(Math.max(0, indentLevel));

        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            String k = e.getKey();
            JsonElement v = e.getValue();

            if (v == null || v.isJsonNull()) {
                continue;
            }

            if (v.isJsonPrimitive()) {
                sb.append("\n").append(indent).append(formatLabel(k)).append(": ").append(primitiveToString(v));
            } else if (v.isJsonObject()) {
                sb.append("\n").append(indent).append("[").append(formatSectionTitle(k)).append("]");
                appendObject(sb, v.getAsJsonObject(), indentLevel + 1);
            } else if (v.isJsonArray()) {
                sb.append("\n").append(indent).append(formatLabel(k)).append(": ").append(arrayToCompactString(v.getAsJsonArray()));
            }
        }
    }

    private String primitiveToString(JsonElement primitiveElement) {
        try {
            return primitiveElement.getAsString();
        } catch (Exception ignored) {
            return String.valueOf(primitiveElement);
        }
    }

    private String arrayToCompactString(JsonArray arr) {
        return arr.toString();
    }

    private String formatLabel(String key) {
        if (key == null || key.isBlank()) return "";
        String spaced = key.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replace('_', ' ')
                .trim();
        if (spaced.isEmpty()) return "";
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    private String formatSectionTitle(String key) {
        return formatLabel(key);
    }

    private ToggleButton createBikeToggle(String id, Node icon, String tooltip) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(icon, new Label(id));

        ToggleButton tb = new ToggleButton();
        tb.setGraphic(box);
        tb.setCursor(Cursor.HAND);
        tb.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        tb.setPrefSize(120, 110);
        tb.setWrapText(true);
        tb.setStyle(
                "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-color: -fx-control-inner-background;" +
                        "-fx-border-color: #cfcfcf;"
        );

        if (tooltip != null && !tooltip.isBlank()) {
            Tooltip.install(tb, new Tooltip(tooltip));
        }

        tb.selectedProperty().addListener((obs, was, isSel) -> {
            if (isSel) {
                tb.setStyle(
                        "-fx-background-radius: 10;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-color: #e8f0fe;" +
                                "-fx-border-color: #4a7afe;" +
                                "-fx-border-width: 1.5;"
                );
            } else {
                tb.setStyle(
                        "-fx-background-radius: 10;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-color: -fx-control-inner-background;" +
                                "-fx-border-color: #cfcfcf;"
                );
            }
        });

        return tb;
    }

    private Node createBikeIcon(boolean electric) {
        String resource = electric ? "/images/icons/bike_electric.png" : "/images/icons/bike_mechanical.png";
        URL url = getClass().getResource(resource);

        if (url != null) {
            ImageView iv = new ImageView(url.toExternalForm());
            iv.setFitWidth(64);
            iv.setFitHeight(64);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            StackPane wrapper = new StackPane(iv);
            wrapper.setPrefSize(90, 70);
            wrapper.setMaxSize(90, 70);
            return wrapper;
        } else {
            Label ph = new Label(electric ? "E-bike" : "Bike");
            ph.setStyle("-fx-font-weight: bold;");
            StackPane wrapper = new StackPane(ph);
            wrapper.setPrefSize(90, 70);
            wrapper.setMaxSize(90, 70);
            System.err.println("Icon resource not found: " + resource + " (using placeholder)");
            return wrapper;
        }
    }

}
