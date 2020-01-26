package com.example.dell.microtechlab;


import android.content.Context;

import static com.example.dell.microtechlab.MainActivity.g;
import static com.example.dell.microtechlab.MainActivity.power_text;
import static com.example.dell.microtechlab.MainActivity.speedSegment;
import static com.example.dell.microtechlab.MainActivity.units_text;
import static com.example.dell.microtechlab.Shared.ENCODER_MAX_RPM;
import static com.example.dell.microtechlab.Shared.ENCODER_PULSES;
import static com.example.dell.microtechlab.Shared.Kilos;
import static com.example.dell.microtechlab.Shared.RPM_SCALE;

import static com.example.dell.microtechlab.Shared.finalResults;
import static com.example.dell.microtechlab.Shared.results;
import static com.example.dell.microtechlab.Shared.unity;



public class DB {



    public static void segmentDisplay(Context context){


        double vdc=results[7]-results[6];

        float rpm=(float)results[13];
       // Toast.makeText(context,"hertz : "+rpm,Toast.LENGTH_SHORT).show();
        rpm = (100000/rpm);
        rpm/=ENCODER_PULSES;

        if(rpm>99000)rpm=0;
        rpm=rpm*60;
        float rpm_analog=rpm/RPM_SCALE;
        if(rpm_analog>ENCODER_MAX_RPM){
            rpm_analog=ENCODER_MAX_RPM;
        }

        //CHANGE THIS TO 14

        for(int i=0;i<14;i++){


            if(i<3){

                // VAC
                if(results[i]<100 && results[i]>=10) MainActivity.vac_text[i].setText("0"+(int)results[i]);
                else if(results[i]<10)MainActivity.vac_text[i].setText("00"+(int)results[i]);
                else MainActivity.vac_text[i].setText(""+(int)results[i]);
                finalResults[i]=results[i];

            }else if(i>=3 && i<6 ||i==8){

                //il1 il2 il3 and idc
                String output="";

                results[i]=Math.floor((results[i])*10.0)/10.0;
                finalResults[i]=results[i];

                if(results[i]<100 && results[i]>=10)output+=""+results[i];
                else if(results[i]<10 && results[i]>=0)output+="0"+results[i];
                else output+="00.0";
                if(i==8){
                    double idc=results[i];
                    if(idc>=0){

                        if(idc<100 && idc>=10)output="+"+idc;
                        else if(idc<10 && idc>=0)output="+0"+idc;
                        else if(idc==0)output=" 000";
                    } else {

                        double idcNeg=Math.abs(idc);
                        if(idcNeg<100 && idcNeg>=10)output="-"+idcNeg;
                        else if(idcNeg<10 && idcNeg>=0)output="-0"+idcNeg;
                    }

                    MainActivity.dc_text[1].setText(output);
                }
                else MainActivity.iac_text[i-3].setText(output);



            }else if(i==6 || i==7){
                // VDC 6 & 7

                    finalResults[i]=vdc;
                    vdc=(int)vdc;

                    if(vdc>=0){

                        if(vdc<100 && vdc>=10) MainActivity.dc_text[0].setText("+0"+(int)vdc);
                        else if(vdc<10 && vdc>=0)MainActivity.dc_text[0].setText("+00"+(int)vdc);
                        else if(vdc<1000 && vdc >=100)MainActivity.dc_text[0].setText("+"+(int)vdc);
                        else if(vdc==0) MainActivity.dc_text[0].setText(" 000");
                    } else {

                        double vdcNeg=Math.abs(vdc);
                        if(vdcNeg<100 && vdcNeg>=10) MainActivity.dc_text[0].setText("-0"+(int)vdcNeg);
                        else if(vdcNeg<10 && vdcNeg>=0)MainActivity.dc_text[0].setText("-00"+(int)vdcNeg);
                        else if(vdcNeg<1000 && vdcNeg >=100)MainActivity.dc_text[0].setText("-"+(int)vdcNeg);
                    }



            }else if(i==13){

                //modify this
                finalResults[i]=rpm;

                g.moveToValue(rpm_analog);
                if(rpm<100 && rpm>=10) speedSegment.setText("00"+(int)rpm);
                else if(rpm<10 && rpm>=0) speedSegment.setText("000"+(int)rpm);
                else if(rpm<1000 && rpm>=100) speedSegment.setText("0"+(int)rpm);
                else speedSegment.setText("0001");


            }else if(i==11){

                //power factor
                String output="";
                results[i]=Math.floor((results[i])*100.0)/100.0;
                finalResults[i]=results[i];
                //Toast.makeText(context,"PF = "+results[i],Toast.LENGTH_SHORT).show();
                if(results[i]<10 && results[i]>0){

                    if(results[i]>=1)output="1.00";
                    else{
                        if(results[i]%0.1==0)output=""+results[i]+"0";
                        else output=""+results[i];
                    }

                }
                else output="00.0";
                power_text[i-9].setText(output);
            }
            else{

                String output="";
                //Toast.makeText(context,"POWER = "+i+" /"+results[i],Toast.LENGTH_SHORT).show();
                if(results[i]>=1000){
                    results[i]/=1000;
                    if(results[i]>=10)results[i]=Math.floor((results[i])*10.0)/10.0;
                    else results[i]=Math.floor((results[i])*100.0)/100.0;
                    output=""+results[i];
                    units_text[i-9].setText(Kilos[i-9]);
                    //add units to killo
                }else{
                    if(results[i]<1000 && results[i]>=100)output=""+(int)results[i];
                    else if(results[i]<100 && results[i]>=10){
                        results[i]=results[i]=Math.floor((results[i])*10.0)/10.0;
                        output="0"+(int)results[i];
                    }
                    else if(results[i]<10 && results[i]>=0){
                        results[i]=results[i]=Math.floor((results[i])*100.0)/100.0;
                        output="00"+(int)results[i];
                    }
                    else output="000";
                    //add units
                    units_text[i-9].setText(unity[i-9]);
                }
                finalResults[i]=results[i];
                power_text[i-9].setText(output);
            }

        }

    }

}
