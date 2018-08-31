package com.example.dell.microtechlab;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;



public class DB {


    static int[] Vac_ids={R.id.vl1,R.id.vl2,R.id.vl3};
    static int[] Iac_ids={R.id.il1,R.id.il2,R.id.il3};
    static int[] Dc_ids={R.id.vdc,R.id.idc};
    static int[] power_id={R.id.power,R.id.reactive,R.id.pfactor};
    public static int VMAX=250,VDCMAX=600;
    public static int IMAX=20;



    public static double fetchDisplay(short[] measure,int rms,int voltage){

        double result=0;
        double ACref,DCref;

        if(voltage==1){
            ACref=VMAX*Math.sqrt(2);
            DCref=VDCMAX;
        }
        else {
            ACref=IMAX*Math.sqrt(2);
            DCref=IMAX;
        }
        //rms formula
        if(rms==1){

            for(int i=0;i<32;i++) {

               //kount dayerha hna ta3 tna7i l offset
                measure[i]=(short)((measure[i]*ACref)/2048);
                result=result+(measure[i]*measure[i]);
            }
            result/=32;
            result=Math.sqrt(result);

        }else{
            for(int i=0;i<32;i++) {
                measure[i]=(short)((measure[i]*DCref)/2048);
                result+=measure[i];
            }
            result=result/32;

        }

        return result;
    }

    public static void segmentDisplay(){

        double[] results=new double[9];
        results[0]=fetchDisplay(MainActivity.vl1,1,1);
        results[1]=fetchDisplay(MainActivity.vl2,1,1);
        results[2]=fetchDisplay(MainActivity.vl3,1,1);
        results[3]=fetchDisplay(MainActivity.il1,1,0);
        results[4]=fetchDisplay(MainActivity.il2,1,0);
        results[5]=fetchDisplay(MainActivity.il3,1,0);
        results[6]=fetchDisplay(MainActivity.vdc1,0,1);
        results[7]=fetchDisplay(MainActivity.vdc2,0,1);
        results[8]=fetchDisplay(MainActivity.idc,0,0);
        double vdc=results[7]-results[6];
        double realPower=vdc*results[8];
        //double apparent power
        //double power factor

        for(int i=0;i<9;i++){

            if(i<3){

                // VAC
                if(results[i]<100 && results[i]>=10) MainActivity.vac_text[i].setText("0"+(int)results[i]);
                else if(results[i]<10)MainActivity.vac_text[i].setText("00"+(int)results[i]);
                else MainActivity.vac_text[i].setText(""+(int)results[i]);

                MainActivity.finalResults[i]=results[i];
            }else if(i>=3 && i<6 ||i==8){
                //il1 il2 il3 and idc
                //assuming max 20 Amper
                results[i]=(int)(Math.round((results[i]*4.8828/1000)*10.0)/10.0);
                String output="";
                MainActivity.finalResults[i]=results[i];
                if(results[i]<=20 && results[i]>=10)output=""+results[i];
                else output="0"+results[i];
                if(i==8)MainActivity.dc_text[1].setText(output);
                else MainActivity.iac_text[i-3].setText(output);

            }else{
                // VDC 6 & 7
                vdc=vdc*60000/4095;
                vdc/=100;
                MainActivity.finalResults[i]=results[i];
                if(vdc<100 && vdc>=10) MainActivity.dc_text[0].setText("0"+(int)results[i]);
                else if(vdc<10)MainActivity.dc_text[0].setText("00"+(int)vdc);
                else MainActivity.dc_text[0].setText(""+(int)vdc);

            }
            //realPower=(realPower*12)/4095
            //MainActivity.power_text[0].setText(""+realPower);
            //display apparent and pf

        }


    }

    void findFileName(){

        File file2=new File("/mnt/usbhost1");
        File[] fList2 = file2.listFiles();

        for (File fi : fList2) {
           // Toast.makeText(MainActivity.this,"Yo :"+fi.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }

//                FileOutputStream fos = new FileOutputStream(file,true);
//                fos.write(msg.getBytes());
////                 FileWriter fos = new FileWriter(file,true); //the true will append the new data
////                 fos.write(msg);//appends the string to the file
//                 fos.close();
//            }catch(Exception e){
//                Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
//
//            }

    }


    //turns floats to segment value
    public int[] getSegmentArray(double value,int significantF){

        int[] array=new int[significantF];
        value*=100;
        value+=0.01;
        int v=(int)value;
        int i=0;

        while(v>0){

            array[i]=v%10;
            v=v/10;
            i++;
        }

        return array;
    }
    public  void setSegmentValues(int[] array,ImageView[] imgs){

        for(int i=0;i<5;i++){

            //imgs[i].setImageDrawable(id[array[i]]);
        }
    }

}
