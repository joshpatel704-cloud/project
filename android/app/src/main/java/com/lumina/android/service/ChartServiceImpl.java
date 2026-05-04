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

        // Trim arrays to actual data size
        float[] trimmedY = new float[i];
        String[] trimmedX = new String[i];
        System.arraycopy(yValues, 0, trimmedY, 0, i);
        System.arraycopy(xLabels, 0, trimmedX, 0, i);

        double change = 0;
        if (firstVal != null && lastVal != null && firstVal != 0) {
            change = ((lastVal - firstVal) / firstVal) * 100;
        }

        callback.onDataPrepared(new ChartData(trimmedY, trimmedX, change));
    }
}
