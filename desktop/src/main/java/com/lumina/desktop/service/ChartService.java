package com.lumina.desktop.service;

import javafx.scene.chart.XYChart;
import java.util.List;

public interface ChartService {
    XYChart.Series<String, Number> generateSeries(String from, String to);
}
