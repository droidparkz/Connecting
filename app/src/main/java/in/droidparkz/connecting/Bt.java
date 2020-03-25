package in.droidparkz.connecting;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class Bt extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    RelativeLayout discover,exit;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private Set<String> devices;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private ArrayList<BluetoothDevice> mBTDeviceList = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<String> arrayAdapter;
    private DeviceListAdapter deviceArrayAdapter;
    ListView lv;

    private static final String TAG = "BluetoothService";
    private static final String NAME = "BluetoothService";
    UUID MY_UUID = UUID.fromString("72759f9e-6d12-11ea-bc55-0242ac130003");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.list_view_bandlist);
        discover = (RelativeLayout)findViewById(R.id.pairingselectionrefresh);
        exit = (RelativeLayout)findViewById(R.id.pairingselectionexit);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,mDeviceList);
        btON();
        discover.setOnClickListener(this);
        exit.setOnClickListener(this);

        lv.setOnItemClickListener(this);

        BA.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        BA.cancelDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDeviceList.add(device);
                deviceArrayAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDeviceList);
                lv.setAdapter(deviceArrayAdapter);
               // mDeviceList.add(device.getName());
               // arrayAdapter.notifyDataSetChanged();
               // deviceArrayAdapter.notifyDataSetChanged();
               // lv.setAdapter(arrayAdapter);
            }
        }
    };

    public void btON()
    {
        if (!BA.isEnabled())
        {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Bluetooth Turned ON",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Searching Devices ...",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Turned ON",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Searching Devices ...",Toast.LENGTH_LONG).show();
        }
    }

    public  void visible()
    {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);

    }

    @Override
    public void onClick(View view) {

        if (view == discover)
        {
            visible();
        }

        if (view == exit)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        BA.cancelDiscovery();

        String name = mBTDeviceList.get(i).getName();
        String mac  = mBTDeviceList.get(i).getAddress();
        int state = mBTDeviceList.get(i).getBondState();

      /*  Toast.makeText(this,"Name : " + name,Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"MAC : " + mac,Toast.LENGTH_SHORT).show();*/

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Toast.makeText(this,"Pairing with : " + name,Toast.LENGTH_SHORT).show();
            mBTDeviceList.get(i).createBond();
        }
    }
}
