package com.example.abhilash.lala;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1; //Bluetooth Permission set to 1
    private ListView myListView;
    private ArrayAdapter<String> mArrayAdapter;
    String Device_Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myListView = (ListView) findViewById(R.id.listView1);
        mArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        myListView.setAdapter(mArrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Full = parent.getItemAtPosition(position).toString();
                String[] Device_Details = Full.split("\n");
                Device_Address = Device_Details[1];
                Toast.makeText(getApplicationContext(),Device_Details[1], Toast.LENGTH_LONG).show();
            }
        });

    }

    BluetoothAdapter mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

    //Enable BT button
    public void buttonOnClick(View v) {
        Button button = (Button) v;
        if (mBluetoothAdapter == null) {
            System.out.println("Bluetooth adapter not functioning"); //For console debugging
            Toast.makeText(getApplicationContext(), "Bluetooth adapter not found", Toast.LENGTH_LONG).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            System.out.println("Is Enabled function"); //For console debugging
            ((Button) v).setText("Bluetooth Started");
            mBluetoothAdapter.enable();
                //Replace by following lines if Bluetooth Should be asked before turning on
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (mBluetoothAdapter.isEnabled()){
            ((Button) v).setText("Bluetooth Started");
        }
    }


    public void start_d_buttononclick(View v){
        Button button = (Button) v;
        if (mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Bluetooth adapter not found", Toast.LENGTH_LONG).show();
        }
        else if(!mBluetoothAdapter.isDiscovering() && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() == true) { //If discovery is not on, change Start discovery
            ((Button) v).setText("Stop Discovery");
            System.out.println("Starting discovery");
            mArrayAdapter.clear();
            mBluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
        else if(!mBluetoothAdapter.isEnabled() && mBluetoothAdapter != null) {
            //((Button) v).setText("Bluetooth Started"); //Change this, wrong view will be changed
            mBluetoothAdapter.enable();
            //Replace by following lines if Bluetooth should be asked before turning on
            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Bluetooth adapter not found", Toast.LENGTH_LONG).show();
        }
        else{
            mBluetoothAdapter.cancelDiscovery();
            ((Button) v).setText("Start Discovery");
        }
    }
    String part_of_the_name = "light";      //Console debugging change this
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                System.out.println("Bluetooth Device Found");//Console debugging
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());

                if(device.getName().contains(part_of_the_name))
                {
                    System.out.println("Device containing LIGHT found");
                    if(device.getBondState() == BluetoothDevice.BOND_NONE && android.os.Build.VERSION.SDK_INT >= 19)
                    {
                        System.out.println("No bond present and android version appropriate");
                        boolean flag = device.createBond();
                        if(!flag){
                            Toast.makeText(getApplicationContext(), "Unable to Create Bond", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Successfully Created Bond", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        System.out.println("Bond state not right or android build not right");
                        System.out.println(Build.VERSION.SDK_INT);
                        System.out.println(device.getBondState());
                    }
                }

            }
        }
    };


    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(bReceiver);
        super.onDestroy();
    }
}
