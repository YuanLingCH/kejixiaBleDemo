package fangzuzu.com.ding.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.adapter.FoundDeviceAdapter;
import fangzuzu.com.ding.constant.BleConstant;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;

import static fangzuzu.com.ding.MainApplication.bleSession;
import static fangzuzu.com.ding.MainApplication.mTTLockAPI;
import static fangzuzu.com.ding.enumtype.Operation.ADD_ADMIN;

public class FoundDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    boolean isKitKat = false;
    Toolbar toolbar;
    private List<ExtendedBluetoothDevice> devices;
    private ListView listView;
    private FoundDeviceAdapter foundDeviceAdapter;
    String contentName;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if(action.equals(BleConstant.ACTION_BLE_DEVICE)) {
                Bundle bundle = intent.getExtras();
                ExtendedBluetoothDevice device = bundle.getParcelable(BleConstant.DEVICE);
                foundDeviceAdapter.updateDevice(device);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.activity_found_device);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        init();

      //  LogUtil.d("start bluetooth service", DBG);


        if (mTTLockAPI.isBLEEnabled(MainApplication.getInstence())){
            mTTLockAPI.startBleService(this);
            //It need location permission to start bluetooth scan,or it can not scan device
            if(requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                mTTLockAPI.startBTDeviceScan();

            }
        }else {
            mTTLockAPI.requestBleEnable(this);
        }
    }
    protected void setStatusBar() {
        if (isKitKat){

            int statusH = screenAdapterUtils.getStatusHeight(this);
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }
    private void init() {
       devices = new ArrayList<>();
       listView = (ListView) findViewById(R.id.list);
       foundDeviceAdapter = new FoundDeviceAdapter(this, devices);
        listView.setAdapter(foundDeviceAdapter);
        listView.setOnItemClickListener(this);
        registerReceiver(mReceiver, getIntentFilter());

    }

    private IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleConstant.ACTION_BLE_DEVICE);
        intentFilter.addAction(BleConstant.ACTION_BLE_DISCONNECTED);
        return intentFilter;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        View view1 = getLayoutInflater().inflate(R.layout.lock_diaage_layout, null);
        final EditText editText = (EditText) view1.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) view1.findViewById(R.id.add_cancle);
        TextView tv_submit= (TextView) view1.findViewById(R.id.add_submit);
        final AlertDialog dialog = new AlertDialog.Builder(FoundDeviceActivity.this)
                .setView(view1)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        Window window=dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager manager=getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width= (int) (defaultDisplay.getWidth()*0.85);
        dialog.getWindow().setAttributes(p);     //设置生效
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentName = editText.getText().toString().trim();
                if (StringUtils.isEmpty(contentName)){
                    Toast.makeText(FoundDeviceActivity.this, "请给设备命名", Toast.LENGTH_SHORT).show();
                }else {
                    mTTLockAPI.stopBTDeviceScan();
                    bleSession.setOperation(ADD_ADMIN);
                    mTTLockAPI.connect((ExtendedBluetoothDevice) foundDeviceAdapter.getItem(position));
                    bleSession.setBleManager(contentName);

                    showProgressDialog();

                }
            }
        });






    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);

        cancelProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}
