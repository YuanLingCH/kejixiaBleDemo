package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.TTlockListBean;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by lingyuan on 2018/6/27.
 */

public class lockListSmartAdapter extends RecyclerView.Adapter<lockListSmartAdapter.lockViewHolder> {

    private List< TTlockListBean.KeyListBean > mDatas;
    private Context mContext;
    private LayoutInflater inflater;


    public lockListSmartAdapter( List< TTlockListBean.KeyListBean > mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);
    }


    @Override
    public lockListSmartAdapter.lockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.lock_item_smart_layout,parent,false);
        final lockViewHolder holder=new lockViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(lockListSmartAdapter.lockViewHolder holder, int position) {


        TTlockListBean.KeyListBean userLockBean = mDatas.get(position);
        String lockMac = userLockBean.getLockMac();
        //   long startTime1 = userLockBean.getStartDate();
        //  long end = userLockBean.getEndDate();
        int electricQuantity = userLockBean.getElectricQuantity();
        String userType = userLockBean.getUserType();   // 钥匙用户类型：110301-管理员钥匙，110302-普通用户钥匙。

        String lockName = userLockBean.getLockAlias();

        //  SharedUtils.putString("keyId",keyId);
        String Starttime1 =  userLockBean.getStartDate()+"";
        int startTime1=0;
        String Starttime=null;
        if (!Starttime1.equals("0")){
            Starttime = Starttime1.substring(0, Starttime1.length() - 3);
            startTime1 = Integer.parseInt(Starttime);
        }

        String endtime1 = userLockBean.getEndDate()+"";
        int end=0;
        String endtime=null;
        if (!endtime1.equals("0")){
            endtime = endtime1.substring(0, endtime1.length() - 3);
            end = Integer.parseInt(endtime);
        }


  /*      if (uid.equals(adminUserId)){
            holder.iv_admin.setVisibility(View.VISIBLE);
            holder.tv_admin.setVisibility(View.VISIBLE);
            holder.lock_state.setBackgroundResource(R.color.yishengxiao);
        }*/



        if (!Starttime1.equals("0")&&!endtime1.equals("0")){

            final int[] current = {0};
            new Thread(){
                @Override
                public void run() {
                    super.run();

                    String websiteDatetime = unixTime.getWebsiteDatetime("http://www.baidu.com")+"";
                    String substring = websiteDatetime.substring(0, websiteDatetime.length() - 3);
                    current[0] = Integer.parseInt(substring);

                }
            }.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

/*
            long timeStampSec = System.currentTimeMillis()/1000;
            String timestamp = String.format("%010d", timeStampSec);
            Log.d("TAG",""+timestamp);
            int current = Integer.parseInt(timestamp);*/

            if (startTime1- current[0] >0){
                holder.lock_state.setText("未生效");
                //   holder.lock_state.setTextColor(Color.parseColor("#EE0000"));
                //  holder.re_adapter.setEnabled(false);
                holder.lock_state.setBackgroundResource(R.color.weishengxiao);
                holder.re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));
                holder.iv.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));
            }else if (startTime1- current[0] <0&&end- current[0] >0&&!Starttime.equals(endtime)){
                holder.lock_state.setText("已生效");
                holder.lock_state.setBackgroundResource(R.color.yishengxiao);
                //  holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
                // holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);

            }else if (end- current[0] <0&&!Starttime.equals(endtime)){
                /// holder.iv.setImageResource(R.drawable.door_logo);
                holder.lock_state.setText("已过期");
                holder.lock_state.setBackgroundResource(R.color.yiguoqi);
                //   holder.lock_state.setBackgroundColor(Color.RED);
                //  holder.lock_state.setTextColor(Color.parseColor("#EE0000"));
                //  holder.re_adapter.setEnabled(false);
                holder.re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));
                holder.iv.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));

            }else if(Starttime.equals(endtime)){
                holder.lock_state.setBackgroundResource(R.color.yishengxiao);
                holder.lock_state.setText("已生效");
                //  holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
                // holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);

            }else if(StringUtils.isEmpty(Starttime)){
                holder.lock_state.setText("已生效");
                holder.lock_state.setBackgroundResource(R.color.yishengxiao);
                // holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
                // holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);


            }

        }
    /*    if (!StringUtils.isEmpty(endtime)){
            String substring = endtime.substring(0,endtime.length()-5);
            Log.d("TAG","结束时间"+ substring);
        }*/

        long timeStampSec = System.currentTimeMillis()/1000;
        String timestamp = String.format("%010d", timeStampSec);

        int i = Integer.parseInt(timestamp);

        //  String s = unixTime.dateToStamp(Starttime);


        //   Log.d("TAG","开始时间"+s  );
        int electricity = Integer.parseInt(userLockBean.getElectricQuantity()+"");
        if (electricity>80){
            Drawable drawableLeft =mContext.getResources().getDrawable(
                    R.mipmap.power);
            holder.lock_elect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            holder.lock_elect.setCompoundDrawablePadding(5);
        }else if (electricity>20){
            Drawable drawableLeft =mContext.getResources().getDrawable(
                    R.mipmap.power_middle);
            holder.lock_elect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            holder.lock_elect.setCompoundDrawablePadding(5);
        }else if (electricity<20){
            Drawable drawableLeft =mContext.getResources().getDrawable(
                    R.mipmap.power_low);
            holder.lock_elect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            holder.lock_elect.setCompoundDrawablePadding(5);
        }
        holder.lock_elect.setText(userLockBean.getElectricQuantity()+"%");

        String endTime =  userLockBean.getEndDate()+"";
        String startTime =  userLockBean.getStartDate()+"";
        MainApplication.getInstence().setEndTime(endTime);
        MainApplication.getInstence().setStartTime(startTime);
        holder.lock_name.setText(lockName);
        if (endTime.equals("0")&&startTime.equals("0")){
            holder.lock_time.setText("永久");
            holder.lock_state.setText("已生效");
            holder.lock_state.setBackgroundResource(R.color.yishengxiao);
            // holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
            // holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
            holder.re_adapter.setEnabled(true);

        }else if (endTime.equals(Starttime)){
            String substringStart = startTime.substring(0, startTime.length() - 5);
            String kaishi = startTime.substring(0, startTime.length() - 9);

            holder.lock_time.setText("永久");

        }else {
            // String substringStart = startTime.substring(0, startTime.length() - 5);
            //  String substringendTime  = endTime .substring(0, endTime .length() - 5);
            String substringStart = unixTime.stampToTime(startTime1);

            String substringendTime = unixTime.stampToTime(end );
            holder.lock_time.setText(substringStart+"至"+substringendTime);
        }


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    class lockViewHolder extends RecyclerView.ViewHolder{
            TextView lock_name,lock_elect,lock_time,lock_state;
        LinearLayout re_adapter;
        ImageView iv;
        public lockViewHolder(View itemView) {
            super(itemView);
            lock_name= (TextView) itemView.findViewById(R.id.lock_name);
            lock_elect= (TextView) itemView.findViewById(R.id.lock_elcet);
            lock_time= (TextView) itemView.findViewById(R.id.lock_time);
            lock_state= (TextView) itemView.findViewById(R.id.lock_state_smart);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            iv= (ImageView) itemView.findViewById(R.id.iv);
        }
    }

}
