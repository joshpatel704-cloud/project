package com.lumina.desktop.service;

import javafx.scene.chart.XYChart;

public class ChartServiceImpl implements ChartService {
    @Override
    public XYChart.Series<String, Number> generateSeries(String from, String to) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(from + "/" + to);
        
        double currentVal = 0.9 + Math.random() * 0.1;
        for (int i = 1; i <= 15; i++) {
            currentVal += (Math.random() - 0.5) * 0.02;
            series.getData().add(new XYChart.Data<>("" + (i + 15), currentVal));
        }
        return series;
    }
}
