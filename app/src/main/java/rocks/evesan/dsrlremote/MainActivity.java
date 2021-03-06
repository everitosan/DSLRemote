package rocks.evesan.dsrlremote;

import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int mode = 0;
    private final int UNPLUGED_MODE = 0;
    private final int FOCUS_MODE = 1;
    private final int RELEASE_MODE = 2;
    private boolean goBack = false;

    private ImageButton trigger;
    private Switch mSwitch;
    private RelativeLayout container;
    private TextView modeText;

    private String focusTxt;
    private String triggerTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        container = (RelativeLayout) findViewById(R.id.activityContainer);
        modeText = (TextView) findViewById(R.id.mode);
        mSwitch = (Switch) findViewById(R.id.switch1);
        trigger = (ImageButton) findViewById(R.id.imageButton);
        trigger.setOnTouchListener(triggerButtonListener);

        mSwitch.setOnCheckedChangeListener(switchModeListener);

        mode = FOCUS_MODE;
        focusTxt = getResources().getString(R.string.focus);
        triggerTxt = getResources().getString(R.string.trigger);

        setFont();
    }

    public void setFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Futura_Medium_Italic_font.ttf");
        modeText.setTypeface(font);
    }

    public CompoundButton.OnCheckedChangeListener switchModeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mode = RELEASE_MODE;
                modeText.setText( triggerTxt );
                trigger.setImageDrawable(getResources().getDrawable(R.drawable.trigger) );
            } else {
                mode = FOCUS_MODE;
                modeText.setText( focusTxt );
                trigger.setImageDrawable(getResources().getDrawable(R.drawable.focus) );
            }

        }
    };

    public View.OnTouchListener triggerButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:


                    if(mode == FOCUS_MODE) {
                        BluetoothList.mBluetoothAdapter.write(Integer.toString(FOCUS_MODE));
                        trigger.setImageDrawable(getResources().getDrawable(R.drawable.focus_pressed) );
                    } else if (mode == RELEASE_MODE) {
                        BluetoothList.mBluetoothAdapter.write( Integer.toString(RELEASE_MODE));
                        trigger.setImageDrawable(getResources().getDrawable(R.drawable.trigger_pressed) );
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    BluetoothList.mBluetoothAdapter.write( Integer.toString(UNPLUGED_MODE) );
                    if (mode == FOCUS_MODE) {
                        trigger.setImageDrawable(getResources().getDrawable(R.drawable.focus) );
                    } else {
                        trigger.setImageDrawable(getResources().getDrawable(R.drawable.trigger) );
                    }
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
