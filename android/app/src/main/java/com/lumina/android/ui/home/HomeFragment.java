package com.lumina.android.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.lumina.android.databinding.FragmentHomeBinding;
import com.lumina.android.model.ConversionRequest;
import com.lumina.android.model.ConversionResponse;
import com.lumina.android.network.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import android.graphics.Color;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String currentTimeFilter = "30";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupUI();
        setupChartStyle();
        updateHistory();
        return root;
    }

    private final String[] currencyCodes = {
        "USD - US Dollar", "EUR - Euro", "INR - Indian Rupee", "GBP - British Pound", "JPY - Japanese Yen",
        "AUD - Australian Dollar", "CAD - Canadian Dollar", "CNY - Chinese Yuan", "CHF - Swiss Franc",
        "AED - UAE Dirham", "ZAR - South African Rand", "SGD - Singapore Dollar", "NZD - New Zealand Dollar",
        "RUB - Russian Ruble", "BRL - Brazilian Real", "HKD - Hong Kong Dollar", "KRW - South Korean Won"
    };

    private void setupUI() {
        binding.btnConvert.setOnClickListener(v -> performConversion());
        binding.btnSync.setOnClickListener(v -> performConversion());
        
        binding.tvFrom.setOnClickListener(v -> showCurrencySelector(true));
        binding.tvTo.setOnClickListener(v -> showCurrencySelector(false));

        binding.btnSwapGroup.setOnClickListener(v -> {
            String from = binding.tvFrom.getText().toString();
            String to = binding.tvTo.getText().toString();
            binding.tvFrom.setText(to);
            binding.tvTo.setText(from);
            performConversion();
            updateHistory();
        });

        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == binding.btn_1w.getId()) currentTimeFilter = "7";
                else if (checkedId == binding.btn_1m.getId()) currentTimeFilter = "30";
                else if (checkedId == binding.btn_1y.getId()) currentTimeFilter = "365";
                else if (checkedId == binding.btn_5y.getId()) currentTimeFilter = "1825";
                updateHistory();
            }
        });
        binding.toggleGroup.check(binding.btn_1m.getId());
    }

    private void setupChartStyle() {
        LineChart chart = binding.chart;
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextColor(Color.parseColor("#40FFFFFF"));
        chart.getXAxis().setDrawAxisLine(false);
        
        chart.getAxisLeft().setTextColor(Color.parseColor("#40FFFFFF"));
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridColor(Color.parseColor("#10FFFFFF"));
        chart.getAxisLeft().setGridLineWidth(1f);
        chart.getAxisLeft().enableGridDashedLine(10f, 10f, 0f);
        chart.getAxisLeft().setDrawAxisLine(false);
        
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        
        // Highlight / Crosshair
        chart.setHighlightPerDragEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        
        ChartMarkerView marker = new ChartMarkerView(getContext(), R.layout.chart_marker_view);
        marker.setChartView(chart);
        chart.setMarker(marker);
    }

    private void updateHistory() {
        String from = binding.tvFrom.getText().toString();
        String to = binding.tvTo.getText().toString();
        int days = Integer.parseInt(currentTimeFilter);
        
        binding.tvChartTitle.setText(String.format("%s to %s Chart", from, to));

        ApiClient.getInstance().getService().getHistoricalRates(from, to, days).enqueue(new Callback<HistoricalRatesResponse>() {
            @Override
            public void onResponse(Call<HistoricalRatesResponse> call, Response<HistoricalRatesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Map<String, Double>> data = response.body();
                    TreeMap<String, Map<String, Double>> sortedData = new TreeMap<>(data);
                    
                    List<Entry> entries = new ArrayList<>();
                    int i = 0;
                    Double firstVal = null;
                    Double lastVal = null;
                    
                    for (Map<String, Double> dayData : sortedData.values()) {
                        Double val = dayData.get(to);
                        if (val != null) {
                            if (firstVal == null) firstVal = val;
                            lastVal = val;
                            entries.add(new Entry(i++, val.floatValue()));
                        }
                    }

                    if (firstVal != null && lastVal != null) {
                        double diff = ((lastVal - firstVal) / firstVal) * 100;
                        binding.tvChartChange.setText(String.format("%s%.2f%%", diff >= 0 ? "+" : "", diff));
                        binding.tvChartChange.setTextColor(diff >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Exchange Rate");
                    dataSet.setColor(Color.parseColor("#3B82F6"));
                    dataSet.setLineWidth(2.5f);
                    dataSet.setDrawCircles(false);
                    dataSet.setDrawValues(false);
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet.setDrawFilled(true);
                    dataSet.setFillColor(Color.parseColor("#3B82F6"));
                    dataSet.setFillAlpha(30);
                    
                    // Crosshair styling
                    dataSet.setHighlightEnabled(true);
                    dataSet.setHighLightColor(Color.WHITE);
                    dataSet.setDrawHorizontalHighlightIndicator(false);
                    dataSet.setDrawVerticalHighlightIndicator(true);

                    LineData lineData = new LineData(dataSet);
                    binding.chart.setData(lineData);
                    binding.chart.animateX(800);
                    
                    if (binding.chart.getMarker() instanceof ChartMarkerView) {
                        ((ChartMarkerView) binding.chart.getMarker()).setSymbol(to);
                    }
                    
                    binding.chart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<HistoricalRatesResponse> call, Throwable t) {
                // Log error
            }
        });
    }

    private void performConversion() {
        String amountStr = binding.etAmount.getText().toString();
        if (amountStr.isEmpty()) return;

        double amount = Double.parseDouble(amountStr);
        String from = binding.tvFrom.getText().toString();
        String to = binding.tvTo.getText().toString();

        ConversionRequest request = new ConversionRequest(from, to, amount);
        
        ApiClient.getInstance().getService().convert(request).enqueue(new Callback<ConversionResponse>() {
            @Override
            public void onResponse(Call<ConversionResponse> call, Response<ConversionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.tvResult.setText(String.format("%.2f %s", response.body().getResult(), to));
                }
            }

            @Override
            public void onFailure(Call<ConversionResponse> call, Throwable t) {
                // Handle error
            }
        });
    }

    private void showCurrencySelector(boolean isFrom) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Currency");
        builder.setItems(currencyCodes, (dialog, which) -> {
            String selectedFull = currencyCodes[which];
            String code = selectedFull.split(" - ")[0];
            if (isFrom) {
                binding.tvFrom.setText(code);
            } else {
                binding.tvTo.setText(code);
            }
            performConversion();
            updateHistory();
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
