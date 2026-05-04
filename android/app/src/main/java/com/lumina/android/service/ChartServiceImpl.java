package com.lumina.android.service;

import java.util.Map;
import java.util.TreeMap;

public class ChartServiceImpl implements ChartService {

    @Override
    public void prepareChartData(Map<String, Map<String, Double>> rawData, String targetCurrency, Callback callback) {
        TreeMap<String, Map<String, Double>> sortedData = new TreeMap<>(rawData);
        float[] yValues = new float[sortedData.size()];
        String[] xLabels = new String[sortedData.size()];
        
        int i = 0;
        Double firstVal = null;
        Double lastVal = null;

        for (Map.Entry<String, Map<String, Double>> entry : sortedData.entrySet()) {
            Double val = entry.getValue().get(targetCurrency);
            if (val != null) {
                if (firstVal == null) firstVal = val;
                lastVal = val;
                yValues[i] = val.floatValue();
                xLabels[i] = entry.getKey();
                i++;
            }
        }

        double change = 0;
        if (firstVal != null && lastVal != null && firstVal != 0) {
            change = ((lastVal - firstVal) / firstVal) * 100;
        }

        callback.onDataPrepared(new ChartData(yValues, xLabels, change));
    }
}
