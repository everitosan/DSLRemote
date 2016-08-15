package rocks.evesan.dsrlremote;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private int mode = 0;
    private final int UNPLUGED_MODE = 0;
    private final int FOCUS_MODE = 1;
    private final int RELEASE_MODE = 2;
    private boolean goBack = false;

    private ImageButton trigger;
    private Switch mSwitch;
    private RelativeLayout container;

    private String focusTxt;
    private String triggerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = (RelativeLayout) findViewById(R.id.activityContainer);
        mSwitch = (Switch) findViewById(R.id.switch1);
        trigger = (ImageButton) findViewById(R.id.imageButton);
        trigger.setOnTouchListener(triggerButtonListener);

        mSwitch.setOnCheckedChangeListener(switchModeListener);

        mode = FOCUS_MODE;
        focusTxt = getResources().getString(R.string.focus);
        triggerTxt = getResources().getString(R.string.trigger);
    }


    public CompoundButton.OnCheckedChangeListener switchModeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mode = RELEASE_MODE;
                mSwitch.setText( triggerTxt );
            } else {
                mode = FOCUS_MODE;
                mSwitch.setText( focusTxt );
            }

        }
    };

    public View.OnTouchListener triggerButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    trigger.setImageDrawable(getResources().getDrawable(R.drawable.button_pressed) );

                    if(mode == FOCUS_MODE) {
                        BluetoothList.mBluetoothAdapter.write(Integer.toString(FOCUS_MODE));
                    } else if (mode == RELEASE_MODE) {
                        BluetoothList.mBluetoothAdapter.write( Integer.toString(RELEASE_MODE));
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    trigger.setImageDrawable(getResources().getDrawable(R.drawable.button) );
                    BluetoothList.mBluetoothAdapter.write( Integer.toString(UNPLUGED_MODE) );
                    break;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if(!goBack) {
            Snackbar snackbar = Snackbar
                    .make(container, getResources().getString(R.string.backAdvice), Snackbar.LENGTH_LONG);

            snackbar.show();
            goBack = true;
        } else {
            if(BluetoothList.mBluetoothAdapter.disconnect()) {
                finish();
            }


        }
    }


}
