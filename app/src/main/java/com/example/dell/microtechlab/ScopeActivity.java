package com.example.dell.microtechlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.dell.microtechlab.MainActivity.sync;
import static com.example.dell.microtechlab.Shared.IMAX;
import static com.example.dell.microtechlab.Shared.VMAX;
import static com.example.dell.microtechlab.Shared.ia;
import static com.example.dell.microtechlab.Shared.ib;
import static com.example.dell.microtechlab.Shared.idc;
import static com.example.dell.microtechlab.Shared.il1;
import static com.example.dell.microtechlab.Shared.il2;
import static com.example.dell.microtechlab.Shared.il3;
import static com.example.dell.microtechlab.Shared.timeStamp;
import static com.example.dell.microtechlab.Shared.va;
import static com.example.dell.microtechlab.Shared.vb;
import static com.example.dell.microtechlab.Shared.vdc1;
import static com.example.dell.microtechlab.Shared.vdc2;
import static com.example.dell.microtechlab.Shared.vl1;
import static com.example.dell.microtechlab.Shared.vl2;
import static com.example.dell.microtechlab.Shared.vl3;

public class ScopeActivity extends Activity {

    GraphView graph;
    LineGraphSeries<DataPoint> series,series2,seriesl2,seriesl3;
    public static final int SIZE=32;
    DataPoint[] dataPoint=new DataPoint[SIZE];
    DataPoint[] dataPointl2=new DataPoint[SIZE];
    DataPoint[] dataPointl3=new DataPoint[SIZE];
    DataPoint[] dataPoint2=new DataPoint[SIZE];
    public int i=0;
    RadioGroup group;
    RadioButton b1,b2,b3,b4,b5,b6,b7,b8,hold;
    private Handler myhandler;
    private Runnable myrunnable;
    boolean usb=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scope);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height=dm.heightPixels;
        int width=dm.widthPixels;
        getWindow().setLayout((int)(width*.85),(int)(height*.85));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        init();


        myhandler=new android.os.Handler();
        myrunnable=new Runnable() {
            @Override
            public void run() {

               if(sync && usb){
                   graphsInit();
                   sync=false;
               }

                myhandler.postDelayed(this,timeStamp);
            }
        };
        myhandler.post(myrunnable);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)finish();
        return super.onTouchEvent(event);
    }

    void init(){

        graph=(GraphView)findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        series2=new LineGraphSeries<>();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        group=(RadioGroup)findViewById(R.id.radioSex2);
        b1=(RadioButton)findViewById(R.id.radiol12);
        b2=(RadioButton)findViewById(R.id.radiol22);
        b3=(RadioButton)findViewById(R.id.radiol32);
        b4=(RadioButton)findViewById(R.id.radiodc);
        b5=(RadioButton)findViewById(R.id.radioV);
        b6=(RadioButton)findViewById(R.id.radioI);
        b7=(RadioButton)findViewById(R.id.radiova);
        b8=(RadioButton)findViewById(R.id.radioia);
        hold=(RadioButton)findViewById(R.id.hold);
        b1.setChecked(true);

        graphsInit();

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    if(value!=0)return super.formatLabel(value, isValueX);
                    else return "";
                } else {
                    // show currency for y values
                    if(value!=0 && i!=5 && i!=7)return super.formatLabel(value, isValueX) + " V";
                    else if(i==5 || i==7)return super.formatLabel(value, isValueX) + " A";
                    else return "0";
                }
            }
        });

        graph.getSecondScale().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    if(value!=0)return super.formatLabel(value, isValueX);
                    else return "0";
                } else {
                    // show currency for y values
                    if(value!=0)return super.formatLabel(value, isValueX) + " A";
                    else return "";
                }
            }
        });



    }

    void graphsInit() {

        //remove previous graph draws
        graph.removeAllSeries();
        graph.getSecondScale().removeAllSeries();

        if(hold.isChecked()){
            usb=false;
        }else usb=true;
        if (b1.isChecked()) {

            i=0;
            for (int j = 0; j < SIZE; j++) {
                dataPoint[j] = new DataPoint(j,vl1[j]);
                dataPoint2[j] = new DataPoint(j,il1[j]);
            }

        } else if (b2.isChecked()) {

            i=1;
            for (int j = 0; j < SIZE; j++) {
                dataPoint[j] = new DataPoint(j,vl2[j]);
                dataPoint2[j] = new DataPoint(j,il2[j]);
            }

        } else if (b3.isChecked()) {

            i=2;
            for (int j = 0; j < SIZE; j++) {
                dataPoint[j] = new DataPoint(j,vl3[j]);
                dataPoint2[j] = new DataPoint(j,il3[j]);
            }

        } else if(b4.isChecked()){

            i=3;
            for (int j = 0; j < SIZE; j++) {
                //check fetch data
                dataPoint[j] = new DataPoint(j, vdc1[j]-vdc2[j]);
                dataPoint2[j] = new DataPoint(j,idc[j]);
            }

        }else if (b5.isChecked()) {

            i=4;
            for (int j = 0; j < SIZE; j++) {

                dataPoint[j] = new DataPoint(j, vl1[j]);
                dataPointl2[j] = new DataPoint(j, vl2[j]);
                dataPointl3[j] = new DataPoint(j,vl3[j]);
            }
        buildSeries(1);

        }else if (b6.isChecked()) {

            i=5;
            for (int j = 0; j < SIZE; j++) {
                dataPoint[j] = new DataPoint(j,il1[j]);
                dataPointl2[j] = new DataPoint(j,il2[j]);
                dataPointl3[j] = new DataPoint(j, il3[j]);
            }
            buildSeries(0);
        }else if (b7.isChecked()) {

            i=6;
            for (int j = 0; j < SIZE; j++) {
                dataPointl2[j] = new DataPoint(j, va[j]);
                dataPoint[j] = new DataPoint(j, vb[j]);

            }
            buildSeriesa(1);

        } else if (b8.isChecked()) {

            i=7;
            for (int j = 0; j < SIZE; j++) {

                dataPointl2[j] = new DataPoint(j, ia[j]);
                dataPoint[j] = new DataPoint(j, ib[j]);
            }
            buildSeriesa(0);

        }

        series = new LineGraphSeries<>(dataPoint);
        series.setColor(Color.RED);
        series.setThickness(3);
        series.setDataPointsRadius(2);
        series.setDrawDataPoints(true);

        series2 = new LineGraphSeries<>(dataPoint2);
        series2.setThickness(3);
        series2.setDataPointsRadius(2);
        series2.setDrawDataPoints(true);

        if (i < 3) {

            series2.setTitle("I Phase " + (i + 1));
            series.setTitle("V Phase " + (i + 1));
            graph.getViewport().setMaxY(VMAX*Math.sqrt(2));
            graph.getViewport().setMinY(-(VMAX*Math.sqrt(2)));

            graph.getSecondScale().setMinY(-(IMAX+2));
            graph.getSecondScale().setMaxY(IMAX+2);
            graph.getSecondScale().addSeries(series2);
        }
        else if(i==3){

            series.setTitle("V DC ");
            series2.setTitle("I DC ");
            graph.getViewport().setMaxY(605);
            graph.getViewport().setMinY(-605);

            graph.getSecondScale().setMinY(-22);
            graph.getSecondScale().setMaxY(22);
            graph.getSecondScale().addSeries(series2);

        }else if(i==4 || i==5){

            seriesl2.setTitle("Phase 2");
            seriesl3.setTitle("Phase 3");
            series.setTitle("Phase 1");

        }else {

            seriesl2.setTitle("Phase b");
            series.setTitle("Phase a");

        }


        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(SIZE+2);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.addSeries(series);


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.TRANSPARENT);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);

    }


    @Override
    protected void onPause() {
        super.onPause();
        myhandler.removeCallbacks(myrunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myhandler.removeCallbacks(myrunnable);
    }

    void buildSeries(int voltage){

        graph.getSecondScale().removeSeries(series2);
        seriesl2 = new LineGraphSeries<>(dataPointl2);
        seriesl2.setThickness(3);
        seriesl2.setDataPointsRadius(2);
        seriesl2.setDrawDataPoints(true);

        seriesl3 = new LineGraphSeries<>(dataPointl3);
        seriesl3.setColor(Color.parseColor("#ECF00A"));
        seriesl3.setThickness(3);
        seriesl3.setDataPointsRadius(2);
        seriesl3.setDrawDataPoints(true);

        if(voltage==1){

            graph.getViewport().setMaxY(VMAX*Math.sqrt(2));
            graph.getViewport().setMinY(-(VMAX*Math.sqrt(2)));
        }else {

            graph.getViewport().setMaxY(IMAX+2);
            graph.getViewport().setMinY(-(IMAX+2));
        }

        graph.addSeries(seriesl2);
        graph.addSeries(seriesl3);

    }
    void buildSeriesa(int voltage){

        graph.getSecondScale().removeSeries(series2);
        graph.removeAllSeries();

        seriesl2 = new LineGraphSeries<>(dataPointl2);
        seriesl2.setThickness(3);
        seriesl2.setDataPointsRadius(3);
        seriesl2.setDrawDataPoints(true);

        series = new LineGraphSeries<>(dataPoint);
        series.setColor(Color.parseColor("#ECF00A"));
        series.setThickness(3);
        series.setDataPointsRadius(3);
        series.setDrawDataPoints(true);

        if(voltage==1){

            graph.getViewport().setMaxY(VMAX*Math.sqrt(2));
            graph.getViewport().setMinY(-(VMAX*Math.sqrt(2)));
        }else {

            graph.getViewport().setMaxY(IMAX+2);
            graph.getViewport().setMinY(-(IMAX+2));
        }

        graph.addSeries(seriesl2);
        graph.addSeries(series);

    }




}
