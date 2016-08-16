package rocks.evesan.dsrlremote;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Set;

import rocks.evesan.dsrlremote.Adapter.BtAdapter;

public class BluetoothList extends AppCompatActivity {

    private ListView list;
    public static BtAdapter mBluetoothAdapter;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);

        getSupportActionBar().hide();

        context = BluetoothList.this;
        mBluetoothAdapter = new BtAdapter(this);
        list =  (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(selectDeviceListener);

        checkPermission();
    }

    public void checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                //Scan for devices
                isBluetoothEnabled();
            } else {
                //Ask permission
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, BtAdapter.REQUEST_BT_PREMISSION);
            }
        } else { //Permission granted
            isBluetoothEnabled();
        }
    }

    public void isBluetoothEnabled() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BtAdapter.REQUEST_ENABLE_BT);
        } else { //scan for devices
            showPairedDevices();
        }

    }

    public void showPairedDevices() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, 0);
        list.setAdapter(listAdapter);

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                listAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // Receive the response from turning on Bluetooth
        if (requestCode == BtAdapter.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                showPairedDevices();
            } else {
                //finish the app
                finish();
            }
        }
    }

    private AdapterView.OnItemClickListener selectDeviceListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView deviceName = (TextView) view;
            String MAC = deviceName.getText().toString();
            MAC = MAC.substring( MAC.length() - 17 , MAC.length());
            mBluetoothAdapter.connectTo(MAC);
        }
    };

    public static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if(message.arg1 == 1) {
                Intent i = new Intent(context , MainActivity.class );
                context.startActivity(i);
            }
        }
    }
}
