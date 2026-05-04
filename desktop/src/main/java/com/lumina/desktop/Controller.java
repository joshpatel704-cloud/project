package com.lumina.desktop;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> fromCurrency;
    @FXML private ComboBox<String> toCurrency;
    @FXML private Button convertButton;
    @FXML private Text resultText;
    @FXML private LineChart<String, Number> performanceChart;
    @FXML private Text chartTitle;
    @FXML private Text chartChange;

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
        String amount = amountField.getText();
        if (amount != null && !amount.isEmpty()) {
            double rate = 0.9423; // Mock rate
            double result = Double.parseDouble(amount) * rate;
            resultText.setText(String.format("%.4f", result));
            updateChart();
        }
    }

    private void updateChart() {
        performanceChart.getData().clear();
        String fromFull = fromCurrency.getValue();
        String toFull = toCurrency.getValue();
        String from = fromFull.split(" - ")[0];
        String to = toFull.split(" - ")[0];
        chartTitle.setText(from + " to " + to + " Chart");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(from + "/" + to);
        
        double currentVal = 0.9 + Math.random() * 0.1;
        double firstVal = currentVal;
        for (int i = 1; i <= 15; i++) {
            currentVal += (Math.random() - 0.5) * 0.02;
            series.getData().add(new XYChart.Data<>("" + (i + 15), currentVal));
        }
        
        double diff = ((currentVal - firstVal) / firstVal) * 100;
        chartChange.setText(String.format("%s%.2f%%", diff >= 0 ? "+" : "", diff));
        chartChange.setStyle(diff >= 0 ? "-fx-fill: #4CAF50;" : "-fx-fill: #F44336;");

        performanceChart.getData().add(series);
    }

    private void setupChart() {
        performanceChart.setCreateSymbols(false);
        performanceChart.setAnimated(true);
        performanceChart.setLegendVisible(false);
        updateChart();
    }
}
