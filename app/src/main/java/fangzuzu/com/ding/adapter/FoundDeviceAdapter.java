package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.List;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.utils.ViewHolder;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class FoundDeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<ExtendedBluetoothDevice> mLeDevices;

    public FoundDeviceAdapter(Context context, List<ExtendedBluetoothDevice> mLeDevices) {
        mContext = context;
        this.mLeDevices = mLeDevices;
    }

    /**
     * update scan device
     * @param extendedBluetoothDevice
     */
    public void updateDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
        boolean contain = false;
        boolean update = false;
        for(ExtendedBluetoothDevice device : mLeDevices) {
            if(device.equals(extendedBluetoothDevice)) {
                contain = true;
                if(device.isSettingMode() != extendedBluetoothDevice.isSettingMode()) {
                    device.setSettingMode(extendedBluetoothDevice.isSettingMode());
                    update = true;
                }
            }
        }
        if(!contain) {
            mLeDevices.add(extendedBluetoothDevice);
            update = true;
        }
        if(update)
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mLeDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return ((ExtendedBluetoothDevice) getItem(position)).isSettingMode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView, R.layout.item_device_list);
        TextView deviceName  = viewHolder.getView(R.id.mDeviceName);
        TextView macAddress = viewHolder.getView(R.id.mDeviceMacAddress);
        ImageView addIcon = viewHolder.getView(R.id.iv_devices);
        ImageView iv_dev=viewHolder.getView(R.id.iv_device);

        if(mLeDevices.get(position).isSettingMode()){
            addIcon.setVisibility(View.VISIBLE);
            iv_dev.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.list_lock));
        } else{
            addIcon.setVisibility(View.GONE);
            iv_dev.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));
        }


        deviceName.setText(mLeDevices.get(position).getName());
        macAddress.setText(mLeDevices.get(position).getAddress());
        return viewHolder.getConvertView();
    }
}
