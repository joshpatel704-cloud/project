package com.lumina.android.ui.home;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.lumina.android.R;

public class ChartMarkerView extends MarkerView {

    private final TextView tvDate;
    private final TextView tvValue;
    private String symbol = "EUR";

    public ChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvDate = findViewById(R.id.tv_marker_date);
        tvValue = findViewById(R.id.tv_marker_value);
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvValue.setText(String.format("%.4f %s", e.getY(), symbol));
        // For date, we could pass labels to the marker if needed, 
        // for now just showing index or mock date
        tvDate.setText("MARKET DATA");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
