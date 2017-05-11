package com.insnergy.sample.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.ApiResult;

import java.util.ArrayList;
import java.util.List;

public class LineChartActivity extends AppCompatActivity {
    public static final String DATA = "data";

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        initLineChart();

        setupLineChartData(getLineChartData());
    }

    private void initLineChart() {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);
        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setDescription("");

        // set an alternative background color
         mChart.setBackgroundColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void setupLineChartData(List< Pair<String, Float> > dotData) {
        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < dotData.size(); i++) {
            xVals.add(dotData.get(i).first);
            yVals.add(new Entry(dotData.get(i).second, i));
        }

        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getLineDataSet(yVals, "label"));
        LineData data = new LineData(xVals, dataSets);
        mChart.setData(data);

        mChart.invalidate(); // refresh the drawing
    }

    private LineDataSet getLineDataSet(List<Entry> entryList, String label) {
        LineDataSet lineDataSet = new LineDataSet(entryList, label);
        lineDataSet.setLineWidth(3);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleSize(5f);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleColorHole(Color.RED);
        lineDataSet.setHighlightEnabled(false);
        return lineDataSet;
    }

    private List< Pair<String, Float> > getLineChartData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String dataJson = extras.getString(DATA);
            Gson gson = new Gson();
            ApiResult apiResult = gson.fromJson(dataJson, ApiResult.class);
            // TODO:
        }


        List< Pair<String, Float> > dotData = new ArrayList<>();
        dotData.add(new Pair<>("a", 1f));
        dotData.add(new Pair<>("b", 2f));
        dotData.add(new Pair<>("c", 3f));
        return dotData;
    }
}
