package tk.twilightlemon.lemonapp.layouts;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import tk.twilightlemon.lemonapp.Fragments.FirstFragment;
import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.Helpers.Settings;

public class Adaptivelayout extends AppCompatActivity {
    private InfoHelper.AdaptiveData adaptData;
    public static Handler Close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adaptivelayout);
        SetWindow();
        adaptData = Settings.AdapData;
        Settings.AdapData = null;
        LoadControls();
        Close=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };
    }

    public void SetWindow() {
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void LoadControls() {
        Settings.Callback_Close = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                adaptData = null;
                Settings.AdapData = null;
                finish();
            }
        };
        TextView Adaptive_title = findViewById(R.id.Adaptive_title);
        Adaptive_title.setText(adaptData.title);
        final ListView lv = findViewById(R.id.Adaptive_list);
        lv.setOnItemClickListener(adaptData.ListOnClick);
        lv.setAdapter(adaptData.CSData);
        FirstFragment.setListViewHeightBasedOnChildren(lv);
        LinearLayout ll = findViewById(R.id.Chooses);
        for (int i = 0; i < adaptData.ChooseData.size(); i++) {
            final RadioButton choose = new RadioButton(this);
            choose.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            choose.setText(adaptData.ChooseData.get(i)[0]);
            final int finalI = i;
            choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Adaptivelayout.this);
                    builder.setTitle(adaptData.title);
                    final String[] cie = adaptData.ChooseData.get(finalI);
                    builder.setItems(cie, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            choose.setText(cie[which]);
                            Message msg = new Message();
                            msg.what = which;
                            msg.obj = lv;
                            adaptData.ChooseCallBack.get(finalI).sendMessage(msg);
                        }
                    });
                    builder.show();
                }
            });
            ll.addView(choose);
        }
    }
}
