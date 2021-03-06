package com.led.led;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ledControl extends AppCompatActivity {

    Button btnOn, btnOff, btnDis;
    //SeekBar brightness;
    TextView lumn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_led_control);

        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        //view of the ledControl layout
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn = (Button)findViewById(R.id.btnOn);
        btnOff = (Button)findViewById(R.id.btnOff);
        btnDis = (Button)findViewById(R.id.btnDisconnect);


        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnOn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOnLed();      //method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });
    }

    private void Disconnect()
    {
        if(btSocket != null)//if btSocket is busy
        {
            try {
                btSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();//return to the first layout
    }

    private void turnOffLed()
    {
        if(btSocket != null)
        {
            try {
                btSocket.getOutputStream().write("TF".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void turnOnLed()
    {
        if(btSocket != null)
        {
            try {
                btSocket.getOutputStream().write("TO".toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void,Void,Void>{

        private boolean ConnectSuccess = true;

        private void msg(String s)
        {
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this,"Connecting...","Please wait!!!");

        }

        @Override
        protected Void doInBackground(Void... params) {
            //while the progress is show, the connection is done in background
            try
            {
                if(btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    //connect to the device address
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    //Create an RFCOMM(SPP) connection
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//startConnection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //after doBackground, check if everything went fine
            super.onPostExecute(aVoid);
            if(!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
