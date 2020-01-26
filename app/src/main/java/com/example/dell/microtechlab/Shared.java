package com.example.dell.microtechlab;

import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

/**
 * Created by DELL on 27/09/2018.
 */

public class Shared {

    public static byte[] bytes;
    public static final int DATA_SIZE=32;

    public static TextView l1,l2,l3;
    public static double[] vl1 = new double[DATA_SIZE];
    public static double[] vl2 = new double[DATA_SIZE];
    public static double[] vl3 = new double[DATA_SIZE];
    public static double[] il1 = new double[DATA_SIZE];
    public static double[] il2 = new double[DATA_SIZE];
    public static double[] il3 = new double[DATA_SIZE];
    public static double[] vdc1 = new double[DATA_SIZE];
    public static double[] vdc2 = new double[DATA_SIZE];
    public static double[] idc = new double[DATA_SIZE];
    public static long[] data=new long[22];

    public static double q=0,p=0;
    public static double finalResults[] = new double[14];
    public static double results[] = new double[14];



    public static final long timeStamp = 600;//in millis 490

    public static UsbManager manager;
    public static UsbInterface usbInterface;
    public static UsbEndpoint outEnd, inEnd;
    public static UsbDevice stmUsb;


    public final static int[] Vac_ids={R.id.vl1,R.id.vl2,R.id.vl3};
    public final static int[] Iac_ids={R.id.il1,R.id.il2,R.id.il3};
    public final static int[] Dc_ids={R.id.vdc,R.id.idc};
    public final static int[] power_id={R.id.power,R.id.reactive,R.id.pfactor,R.id.apparent};
    public final static int[] units_ids={R.id.KW_id,R.id.KVAR_id,R.id.pf_id,R.id.KVA_id};
    public final static String[] Kilos={"KW","KVAR"," ","KVA"};
    public final static String[] unity={"W  ","VAR "," ","VA "};

    public final static int VENDOR_ID=4660;

    public static int ENCODER_PULSES=1024;
    public static int ENCODER_MAX_RPM=3000;
    public static int RPM_SCALE=10;
    public final static int VMAX=250;
    public final static int IMAX=10;


    public static double[] va=new double[DATA_SIZE];
    public static double[] vb=new double[DATA_SIZE];
    public static double[] ia=new double[DATA_SIZE];
    public static double[] ib=new double[DATA_SIZE];


    public static Typeface tp;
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";

    public static String HELP="EM-DAQ Electrical Power Measurment Station\n\n" +
            "Developed by MicroTech Lab\n\n" +
            "Website : www.microtech-lab.com\n\n"+
            "Email : info@microtech-lab.com\n\n"+
            "Portable : (+213) 5 60 09 58 85\n\n"+
            "Correctif : (+213) 24 91 20 97\n\n"+
            "Fax : (+213) 24 91 20 97\n\n";

    public static String NAZIM="Software developed by Bellabaci Nazim\n" +
            "INELEC promo 2014\n" +
            "Wameedh Scientific Club President 2017\n" +
            "DeadLine Technologies Co-Founder\n"+
            "Facebook : Nazim BL\n"+
            "Email : bellabaci.nazim@gmail.com\n";

    public static final long maxReg=0x514791;
    public static final double Sscale=0.00390,Pscale=0.0038816111;
    public static final double Vscale=220*2.489,Iscale=23.98;

}
