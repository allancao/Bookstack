package bookstack;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import android.content.Context;
import bookstack.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph extends Fragment {

    XYPlot plot;
    private SimpleXYSeries mySeries;

    public Graph() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph, container, false);
        plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);

        // DATA

//        Number[] days =   {};
//        Number[] values = {};

        // this code sucks.

        List<Number> daysList =   new ArrayList<Number>();
        List<Number> valuesList = new ArrayList<Number>();

        MySQLiteHelper db = new MySQLiteHelper(getActivity());

        List<ReadPeriod> rps = db.getAllReadPeriod();

        // these values are weird. just graphs reading periods...
        for (int i=0; i<rps.size(); i++) {
            daysList.add((Number)(new Integer(i)));
            int diff = ( rps.get(i).getEnd() - rps.get(i).getStart() );
            Log.d("diff", Integer.toString(diff));
            int minutes = diff / (60);
            Log.d("minutes", Integer.toString(minutes));
            valuesList.add((Number)(new Integer(minutes)));
        }

        Number[] days = new Number[daysList.size()];
        days = daysList.toArray(days);


        Number[] values = new Number[valuesList.size()];
        values = valuesList.toArray(values);

        Number[] dumbdays =   { 1  , 2   , 3   , 4   , 5   , 6   , 7 };
        Number[] dumbvalues = { 10, 30, 40, 60, 100, 95, 70};

        Log.d("dumbdays", Arrays.toString(dumbdays));
        Log.d("dumbvalues", Arrays.toString(dumbvalues));
        Log.d("days", Arrays.toString(days));
        Log.d("values", Arrays.toString(values));

        XYSeries mySeries = new SimpleXYSeries(
                Arrays.asList(days),
                Arrays.asList(values),
                "Series1");

        LineAndPointFormatter format = new LineAndPointFormatter(
                Color.rgb(200, 200, 200),                   // line color
                Color.parseColor("#00000000"),                   // point color
                Color.parseColor("#59000000"),          // fill color
                new PointLabelFormatter(Color.TRANSPARENT));
        format.getLinePaint().setStrokeWidth(5);
        format.getVertexPaint().setStrokeWidth(10);

        plot.addSeries(mySeries, format);

        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.parseColor("#E6E6E6"));
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.parseColor("#E6E6E6"));

        // Domain
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, days.length);
        plot.setDomainValueFormat(new DecimalFormat("0"));
        plot.setDomainStepValue(1);

        //Range
        plot.setRangeBoundaries(0, 120, BoundaryMode.FIXED);
        plot.setRangeStepValue(5);
        //mySimpleXYPlot.setRangeStep(XYStepMode.SUBDIVIDE, values.length);
        plot.setRangeValueFormat(new DecimalFormat("0"));

        //Remove legend
        plot.getLayoutManager().remove(plot.getLegendWidget());
//            plot.getLayoutManager().remove(plot.getDomainLabelWidget());
//            plot.getLayoutManager().remove(plot.getRangeLabelWidget());
        plot.getLayoutManager().remove(plot.getTitleWidget());
        plot.getLayoutManager().remove(plot.getLegendWidget());

        plot.redraw();
        getActivity().setTitle("Graph");

        return rootView;
    }
}