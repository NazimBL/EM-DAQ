package com.example.dell.microtechlab;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import static com.example.dell.microtechlab.MainActivity.startprocess;
import static com.example.dell.microtechlab.MainActivity.usbConnected;
import static com.example.dell.microtechlab.Processing.graphSet;
import static com.example.dell.microtechlab.Shared.VENDOR_ID;
import static com.example.dell.microtechlab.Shared.bytes;
import static com.example.dell.microtechlab.Shared.inEnd;
import static com.example.dell.microtechlab.Shared.manager;
import static com.example.dell.microtechlab.Shared.outEnd;
import static com.example.dell.microtechlab.Shared.results;
import static com.example.dell.microtechlab.Shared.usbInterface;

/**
 * Created by Bellabaci Nazim on 15/02/2018.
 */

public class MyUSB {

    public static UsbDeviceConnection connection;

    public static byte[] vl1_bytes=new byte[64];
    public static byte[] il1_bytes=new byte[64];
    public static byte[] vl2_bytes=new byte[64];
    public static byte[] il2_bytes=new byte[64];
    public static byte[] vl3_bytes=new byte[64];
    public static byte[] il3_bytes=new byte[64];
    public static byte[] vdc1_bytes=new byte[64];
    public static byte[] vdc2_bytes=new byte[64];
    public static byte[] idc_bytes=new byte[64];
    public static byte[] encoder_buff=new byte[64];

    public static byte[] first=new byte[64];

    public static UsbInterface findAdbInterface(UsbDevice device) {

        if(device.getVendorId()!=VENDOR_ID)return null;
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {

            UsbInterface intf = device.getInterface(i);
            return intf;

        }
        return null;
    }

    public static void manageEndpoints(){

        outEnd=null;
        inEnd=null;

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {

            UsbEndpoint ep = usbInterface.getEndpoint(i);

            if (ep.getDirection() == UsbConstants.USB_DIR_OUT) outEnd = ep;
            else inEnd = ep;

        }
    }

    public static  void usbCom(UsbDevice device,Context context){

        manageEndpoints();

        if(outEnd!=null && device!=null && usbInterface!=null ) {
            if(device.getVendorId()!=VENDOR_ID)return ;
            usbConnected=true;
            PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,
                    new Intent(UsbManager.EXTRA_PERMISSION_GRANTED),0);
            manager.requestPermission(device,pendingIntent);
            connection=null;
            if(manager.hasPermission(device)) connection=manager.openDevice(device);

            //turn this to write / read methods
            try {
                if (connection != null) {

                    if (connection.claimInterface(usbInterface,true)) {


                        bytes ="OK".getBytes();
                        try {

                            int transfer = connection.bulkTransfer(outEnd,bytes, bytes.length,0);
                            getAdeData();
                            convertData(context);

                            results=Processing.FetchAdeData(context);
                            graphSet();
                            Processing.ByPhaseCalc();
                            long difference=System.currentTimeMillis()-startprocess;

                            //takes around 30 ms
                            //Toast.makeText(context,"usb time : "+difference,Toast.LENGTH_SHORT).show();
                            MainActivity.sync=true;


                        }catch (Exception e){
                            Toast.makeText(context,e.toString()+" "+context.getClass(),Toast.LENGTH_SHORT).show();
                            connection.releaseInterface(usbInterface);
                            connection.close();
                        }

                    } else {
                        Toast.makeText(context,"couldnt claim interface",Toast.LENGTH_SHORT).show();
                        connection.releaseInterface(usbInterface);
                        connection.close();
                    }

                } else {

                    Toast.makeText(context,"couldnt open connection  ",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();

            }

        }else{
            usbConnected=false;
            Toast.makeText(context,"no end point found",Toast.LENGTH_SHORT).show();
        }


    }


    public static void getAdeData(){

        if(connection==null)return;

        int receptionv11=connection.bulkTransfer(inEnd,first,first.length,0);
        connection.releaseInterface(usbInterface);
        connection.close();
    }

    public static void convertData(Context context){

        int j=0;
        int D[]=new int[64];
        for(int i=0;i<64;i++)D[i]=first[i]&0xFF;
        //lookout for this
        for(int k=0;k<22;k++){


          if(k==13 || k==17){
              Shared.data[k]=((D[j]*256)+(D[j+1]));
              j+=2;
          }
          else  {

              Shared.data[k]=(D[j]*65536)+(D[j+1]*256)+(D[j+2]);
              j+=3;
            }

        }

    }




}
