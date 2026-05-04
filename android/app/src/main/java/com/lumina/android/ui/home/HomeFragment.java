package com.lumina.android.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.lumina.android.databinding.FragmentHomeBinding;
import com.lumina.android.controller.CurrencyController;
import com.lumina.android.model.ConversionResult;
import com.lumina.android.service.CurrencyService;
import com.lumina.android.service.CurrencyServiceImpl;
import com.lumina.android.service.ChartService;
import com.lumina.android.service.ChartServiceImpl;
import com.lumina.android.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HomeFragment extends Fragment implements CurrencyController.View {

    private FragmentHomeBinding binding;
    private String currentTimeFilter = "30";
    private CurrencyController controller;
    private CurrencyService currencyService;
    private ChartService chartService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        controller = new CurrencyController(this);
        currencyService = new CurrencyServiceImpl();
        chartService = new ChartServiceImpl();

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

        currencyService.getHistoricalRates(from, to, days, new CurrencyService.Callback<Map<String, Map<String, Double>>>() {
            @Override
            public void onSuccess(Map<String, Map<String, Double>> result) {
                android.util.Log.d("CHART_API", "Received raw data size: " + result.size());
                
                chartService.prepareChartData(result, to, data -> {
                    requireActivity().runOnUiThread(() -> {
                        if (data.yValues.length == 0) {
                            android.util.Log.d("CHART", "No displayable entries found");
                            binding.chart.setVisibility(View.GONE);
                            binding.llEmptyState.setVisibility(View.VISIBLE);
                            binding.tvChartChange.setVisibility(View.GONE);
                            return;
                        }

                        binding.chart.setVisibility(View.VISIBLE);
                        binding.llEmptyState.setVisibility(View.GONE);
                        binding.tvChartChange.setVisibility(View.VISIBLE);

                        android.util.Log.d("CHART", "Preparing to render entries: " + data.yValues.length);

                        List<Entry> entries = new ArrayList<>();
                        for (int i = 0; i < data.yValues.length; i++) {
                            entries.add(new Entry(i, data.yValues[i]));
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
                        
                        dataSet.setHighlightEnabled(true);
                        dataSet.setHighLightColor(Color.WHITE);
                        dataSet.setDrawHorizontalHighlightIndicator(false);
                        dataSet.setDrawVerticalHighlightIndicator(true);

                        binding.tvChartChange.setText(String.format("%s%.2f%%", data.percentageChange >= 0 ? "+" : "", data.percentageChange));
                        binding.tvChartChange.setTextColor(data.percentageChange >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

                        binding.chart.setData(new LineData(dataSet));
                        binding.chart.animateX(800);
                        
                        if (binding.chart.getMarker() instanceof ChartMarkerView) {
                            ((ChartMarkerView) binding.chart.getMarker()).setSymbol(to);
                        }
                        binding.chart.invalidate();
                        android.util.Log.d("CHART", "Chart refreshed and invalidated on UI thread");
                    });
                });
            }

            @Override
            public void onError(String error) {
                // showError(error);
            }
        });
    }

    private void performConversion() {
        String amountStr = binding.etAmount.getText().toString();
        if (!CurrencyUtils.isValidInput(amountStr)) return;

        double amount = Double.parseDouble(amountStr);
        String from = binding.tvFrom.getText().toString();
        String to = binding.tvTo.getText().toString();

        controller.convert(from, to, amount);
    }

    @Override
    public void showConversionResult(ConversionResult result) {
        String to = binding.tvTo.getText().toString();
        binding.tvResult.setText(String.format("%s %s", CurrencyUtils.formatAmount(result.getConvertedAmount()), to));
    }

    @Override
    public void showLoading(boolean loading) {
        // Show/hide progress bar
    }

    @Override
    public void showError(String message) {
        // Show toast or snackbar
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
