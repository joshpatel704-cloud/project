package com.lumina.desktop;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import com.lumina.desktop.service.ChartService;
import com.lumina.desktop.service.ChartServiceImpl;

public class Controller {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> fromCurrency;
    @FXML private ComboBox<String> toCurrency;
    @FXML private Button convertButton;
    @FXML private Text resultText;
    @FXML private LineChart<String, Number> performanceChart;
    @FXML private VBox emptyState;
    @FXML private Text chartTitle;
    @FXML private Text chartChange;

    private final CurrencyService currencyService;
    private final ChartService chartService;

    public Controller() {
        this.currencyService = new MockCurrencyService();
        this.chartService = new ChartServiceImpl();
    }

    @FXML
    public void initialize() {
        String[] currencies = {
            "USD - US Dollar", "EUR - Euro", "INR - Indian Rupee", "GBP - British Pound", "JPY - Japanese Yen",
            "AUD - Australian Dollar", "CAD - Canadian Dollar", "CNY - Chinese Yuan", "CHF - Swiss Franc",
            "AED - UAE Dirham", "ZAR - South African Rand", "SGD - Singapore Dollar", "NZD - New Zealand Dollar",
            "RUB - Russian Ruble", "BRL - Brazilian Real", "HKD - Hong Kong Dollar", "KRW - South Korean Won"
        };
        fromCurrency.getItems().addAll(currencies);
        toCurrency.getItems().addAll(currencies);
        
        fromCurrency.setValue("USD - US Dollar");
        toCurrency.setValue("EUR - Euro");

        setupChart();
    }

    @FXML
    private void handleTimeFilter() {
        updateChart();
    }

    @FXML
    private void handleSwap() {
        String from = fromCurrency.getValue();
        String to = toCurrency.getValue();
        fromCurrency.setValue(to);
        toCurrency.setValue(from);
        updateChart();
        handleConvert();
    }

    @FXML
    private void handleConvert() {
        String amountText = amountField.getText();
        if (amountText == null || amountText.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountText);
            String fromFull = fromCurrency.getValue();
            String toFull = toCurrency.getValue();
            String from = fromFull.split(" - ")[0];
            String to = toFull.split(" - ")[0];

            currencyService.convert(from, to, amount).thenAccept(result -> {
                Platform.runLater(() -> {
                    resultText.setText(String.format("%.4f", result.getResult()));
                    updateChart();
                });
            });
        } catch (NumberFormatException e) {
            resultText.setText("Invalid input");
        }
    }

    private void updateChart() {
        System.out.println("[DESKTOP-CHART] Clearing old series data...");
        performanceChart.getData().clear();
        
        String fromFull = fromCurrency.getValue();
        String toFull = toCurrency.getValue();
        String from = fromFull.split(" - ")[0];
        String to = toFull.split(" - ")[0];
        chartTitle.setText(from + " to " + to + " Chart");

        XYChart.Series<String, Number> series = chartService.generateSeries(from, to);
        System.out.println("[DESKTOP-CHART] Generated series with " + series.getData().size() + " data points");
        
        boolean hasData = !series.getData().isEmpty();
        performanceChart.setVisible(hasData);
        emptyState.setVisible(!hasData);
        
        if (hasData) {
            double firstVal = series.getData().get(0).getYValue().doubleValue();
            double lastVal = series.getData().get(series.getData().size() - 1).getYValue().doubleValue();
            double diff = ((lastVal - firstVal) / firstVal) * 100;
            chartChange.setText(String.format("%s%.2f%%", diff >= 0 ? "+" : "", diff));
            chartChange.setStyle(diff >= 0 ? "-fx-fill: #4CAF50;" : "-fx-fill: #F44336;");
            chartChange.getParent().setVisible(true);
            
            performanceChart.getData().add(series);
            System.out.println("[DESKTOP-CHART] Series added to chart");
        } else {
            System.err.println("[DESKTOP-CHART] No data found to display");
            chartChange.setText("0.00%");
            chartChange.getParent().setVisible(false);
        }
    }

    private void setupChart() {
        performanceChart.setCreateSymbols(false);
        performanceChart.setAnimated(true);
        performanceChart.setLegendVisible(false);
        updateChart();
    }
}
