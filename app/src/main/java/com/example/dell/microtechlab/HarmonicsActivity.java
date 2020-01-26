package com.example.dell.microtechlab;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import static com.example.dell.microtechlab.MainActivity.sync;
import static com.example.dell.microtechlab.Processing.Harmonics;
import static com.example.dell.microtechlab.Shared.timeStamp;

public class HarmonicsActivity extends Activity{

    GraphView graph;
    BarGraphSeries<DataPoint> series;
    //this was7
    public static final int SIZE=4;
    DataPoint[] dataPoint=new DataPoint[SIZE];
    RadioGroup group;
    RadioButton b1,b2,b3,b4,b5,b6;
    private Handler myhandler;
    private Runnable myrunnable;
    private int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harmonics);


        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        int height=dm.heightPixels;
        int width=dm.widthPixels;
        getWindow().setLayout((int)(width*.85),(int)(height*.85));


        init();



        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
              AdeGraphInit();
            }
        });

        myhandler=new android.os.Handler();
        myrunnable=new Runnable() {
            @Override
            public void run() {

               // AdeGraphInit();
                if(sync){
                   AdeGraphInit();
                    sync=false;
                }
                myhandler.postDelayed(this,timeStamp);
            }
        };
        myhandler.post(myrunnable);


    }

    void init(){

            graph=(GraphView)findViewById(R.id.graph);
            group=(RadioGroup)findViewById(R.id.radioSex);
            b1=(RadioButton)findViewById(R.id.radiol1);
            b2=(RadioButton)findViewById(R.id.radiol2);
            b3=(RadioButton)findViewById(R.id.radiol3);
            b4=(RadioButton)findViewById(R.id.radioi1);
            b5=(RadioButton)findViewById(R.id.radioi2);
            b6=(RadioButton)findViewById(R.id.radioi3);

            series = new BarGraphSeries<>();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            b1.setChecked(true);
            b4.setChecked(false);
            AdeGraphInit();

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    if(value!=0)return ""+(int)value;

                    else return "";
                } else {
                    // show currency for y values
                    if(value!=0){
                        if(i==0)return super.formatLabel(value, isValueX) + " V";
                        else return super.formatLabel(value, isValueX) + " A";

                    }
                    else return "0";
                }
            }
        });


    }

    void AdeGraphInit(){

        //remove previous graph draws
        graph.removeAllSeries();
        double dft[]=new double[SIZE];


        if(b1.isChecked()){
            i=0;
            dft=Harmonics(1,HarmonicsActivity.this);

        }
        else if(b2.isChecked()){
            i=0;
            dft=Harmonics(1,HarmonicsActivity.this);
        }
        else if(b3.isChecked()){
            i=0;
            dft=Harmonics(1,HarmonicsActivity.this);
        }
        else if(b4.isChecked()){
            i=1;
            dft=Harmonics(0,HarmonicsActivity.this);
        }
        else if(b5.isChecked()){
            i=1;
            dft=Harmonics(0,HarmonicsActivity.this);
        }
        else if(b6.isChecked()){
            i=1;
            dft=Harmonics(0,HarmonicsActivity.this);
        }

        int w=1;
        for(int j=0;j<SIZE;j++) dataPoint[j]=new DataPoint(j+w,dft[j]);

        //calculate thd
        double thd;

        thd=Processing.getTHD(i);
        //thd=0.03;
        thd=Math.floor(thd*1000.0)/1000.0;


        series=new BarGraphSeries<>(dataPoint);
        series.setColor(Color.BLUE);
        series.setSpacing(100);
        series.setDataWidth(10);
        series.setDrawValuesOnTop(true);
        series.setTitle("THD : "+thd+" %");

        if(i==0){
            graph.getViewport().setMaxY(220);
            graph.getViewport().setMinY(0);
        }else {
            graph.getViewport().setMaxY(10);
            graph.getViewport().setMinY(0);
        }

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.addSeries(series);


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.TRANSPARENT);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)finish();

        return super.onTouchEvent(event);
    }

}
