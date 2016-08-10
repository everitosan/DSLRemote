package rocks.evesan.dsrlremote.Adapter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import rocks.evesan.dsrlremote.BluetoothList;

/**
 * Created by evesan on 8/7/16.
 */
public class BtAdapter {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;
    public final static int REQUEST_ENABLE_BT = 1;
    public final static int REQUEST_BT_PREMISSION = 2;

    private Context context;
    private ProgressDialog progress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBtConnected = false;


    public BtAdapter(Context ctx){
        context = ctx;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public Set<BluetoothDevice> getBondedDevices(){
     return mBluetoothAdapter.getBondedDevices();
    }

    public void connectTo(String MAC){
        new ConnectBT(this.context, MAC).execute();
    }

    public void write(String msg) {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(msg.toString().getBytes());
            } catch (IOException e) {

            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private Context context;
        private String MAC;

        private Handler mHandler = null;
        private Messenger mMessenger = null;

        public ConnectBT(Context ctx, String MAC) {
            this.context = ctx;
            this.MAC = MAC;
        }
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(this.context, "Connecting...", "Please wait!!!");
            mHandler = new BluetoothList.MessageHandler();
            mMessenger = new Messenger(mHandler);
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(this.MAC);//connection to the device
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(this.context, "Fail", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this.context, "Connected", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
                //communicates with Bluetooth List activity
                try {
                    Message msg1 = new Message();
                    msg1.arg1 = 1;
                    mMessenger.send(msg1);
                } catch (RemoteException e) {
                    Toast.makeText(this.context, "Fail exception", Toast.LENGTH_SHORT).show();
                }

            }
            progress.dismiss();
        }
    }

}
