package com.lumina.android.service;

import java.util.Map;

public interface ChartService {
    void prepareChartData(Map<String, Map<String, Double>> rawData, String targetCurrency, Callback callback);

    interface Callback {
        void onDataPrepared(ChartData data);
    }

    class ChartData {
        public final float[] yValues;
        public final String[] xLabels;
        public final double percentageChange;

        public ChartData(float[] yValues, String[] xLabels, double percentageChange) {
            this.yValues = yValues;
            this.xLabels = xLabels;
            this.percentageChange = percentageChange;
        }
    }
}
