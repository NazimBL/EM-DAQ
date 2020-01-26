package com.example.dell.microtechlab;


import android.content.Context;

import static com.example.dell.microtechlab.Shared.DATA_SIZE;
import static com.example.dell.microtechlab.Shared.Iscale;
import static com.example.dell.microtechlab.Shared.Pscale;
import static com.example.dell.microtechlab.Shared.Sscale;
import static com.example.dell.microtechlab.Shared.Vscale;
import static com.example.dell.microtechlab.Shared.data;
import static com.example.dell.microtechlab.Shared.ia;
import static com.example.dell.microtechlab.Shared.ib;
import static com.example.dell.microtechlab.Shared.il1;
import static com.example.dell.microtechlab.Shared.il2;
import static com.example.dell.microtechlab.Shared.il3;
import static com.example.dell.microtechlab.Shared.maxReg;
import static com.example.dell.microtechlab.Shared.p;
import static com.example.dell.microtechlab.Shared.q;
import static com.example.dell.microtechlab.Shared.results;
import static com.example.dell.microtechlab.Shared.va;
import static com.example.dell.microtechlab.Shared.vb;
import static com.example.dell.microtechlab.Shared.vl1;
import static com.example.dell.microtechlab.Shared.vl2;
import static com.example.dell.microtechlab.Shared.vl3;

/**
 * Created by Nazim BL on 30/09/2018.
 */

public class Processing {

    public static void ByPhaseCalc(){
        p=0;
        q=0;

        for(int j=0;j<DATA_SIZE;j++){

            vb[j]= vl2[j]-vl3[j];
            vb[j]/=3;
            vb[j]*=Math.sqrt(3);

            vb[j]*=3;
            vb[j]/=Math.sqrt(2);

            ib[j]= il2[j]-il3[j];
            ib[j]/=3;
            ib[j]*=Math.sqrt(3);

            ib[j]*=3;
            ib[j]/=Math.sqrt(2);


            ia[j]=(2*il1[j])-il2[j]-il3[j];
            ia[j]/=3;

            ia[j]*=3;
            ia[j]/=Math.sqrt(2);

            va[j]=(2*vl1[j])-vl2[j]-vl3[j];
            va[j]/=3;

            va[j]*=3;
            va[j]/=Math.sqrt(2);


            p=p+((va[j]*ia[j])+(vb[j]*ib[j]));
            q=q+((vb[j]*ia[j])-(va[j]*ib[j]));

        }
    }

    public static double fetchAdeDisplay(long dat,int code){
        double result;

        if(code==0){
            //voltage
            result=(double)dat/maxReg;
            result*=Vscale;
        }else if(code==1){
            result=(double)dat/maxReg;
            result*=Iscale;
        }else if(code==2){
            result=(double)dat*Pscale;

        }else{
            result=(double)dat*Sscale;

        }

        return result;
    }
    public static double[] FetchAdeData(Context context){

        double[] results=new double[14];

        results[0]=fetchAdeDisplay(data[4],  0);
        results[1]=fetchAdeDisplay(data[2],  0);
        results[2]=fetchAdeDisplay(data[0],  0);
        results[6]=0;
        results[7]=0;
        results[3]=fetchAdeDisplay(data[5],  1);
        results[4]=fetchAdeDisplay(data[3],  1);
        results[5]=fetchAdeDisplay(data[1],  1);
        results[8]=0;
        long Psum=data[11]+data[9]+data[7];
        long Ssum=data[10]+data[8]+data[6];
        results[9]=fetchAdeDisplay(Psum,  2);
        results[12]=fetchAdeDisplay(Ssum,  3);

        //pf
        results[11]=results[9]/results[12];
        double angle=Math.acos(results[11]);

        //reactive power
        results[10]=results[12]*Math.sin(angle);
        results[13]=0;

        return results;
    }



    public static double[] Harmonics(int voltage,Context context){


        double H[]=new double[4];
        int j=0;
        if(voltage==1){

            for(int i=12;i<16;i++){
                H[j]=fetchAdeDisplay(data[i],  0);
                //Toast.makeText(context,"h= "+H[j]+" data="+data[i],Toast.LENGTH_SHORT).show();
                j++;
            }

        }else{

            for(int i=16;i<20;i++){
                H[j]=fetchAdeDisplay(data[i],  1);
                j++;
            }

        }

        return H;
    }

    public static void graphSet(){


        for(int n=0;n<DATA_SIZE;n++){

            double arg=(2*Math.PI*n)/16;
            double arg2=arg+(2*Math.PI*n/3);
            double arg3=arg+(4*Math.PI*n/3);


            arg2=Math.sin(arg2);
            arg3=Math.sin(arg3);
            arg=Math.sin(arg);

            arg*=Math.sqrt(2);
            arg2*=Math.sqrt(2);
            arg3*=Math.sqrt(2);
            vl3[n]=results[2]*arg;
            vl2[n]=results[1]*arg2;
            vl1[n]=results[0]*arg3;

            il3[n]=results[5]*arg;
            il2[n]=results[4]*arg2;
            il1[n]=results[3]*arg3;

        }
    }

    public static double getTHD(int voltage){

    double thd;
    long dat;
    //1 is current 0 is voltage sorry

    if(voltage==1)dat=data[20];
    else dat=data[21];

        thd=(double)dat/0x7fffff;
        thd*=3.999999;

    return thd;

    }


}
