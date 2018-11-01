package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.passwordManagerBean;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by lingyuan on 2018/7/5.
 */

public class passwordManagerListAdapter extends RecyclerView.Adapter{



    private List<passwordManagerBean.ListBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    private OnItemLongClickListener mOnItemLongClickListener;

    public passwordManagerListAdapter(List mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);


    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int positione,String keyboardPwd1,int keyboardPwdId,int keyboardPwdType);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 根据返回的ViewType，绑定不同的布局文件，这里只有两种

            return new IcViewHolder(inflater.inflate(R.layout.pasw_manager_layout, parent, false));


    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof IcViewHolder) {
            ((IcViewHolder) holder).bind(mDatas, position);
        }

         }

    @Override
    public int getItemCount() {
        return mDatas.size();//recylerview的item的总数目是所有数据数量加一
    }

    public class IcViewHolder extends RecyclerView.ViewHolder{

        TextView lock_name,lock_time,lock_state;
        LinearLayout re_adapter;
        ImageView iv;
        public IcViewHolder(View itemView) {
            super(itemView);
            lock_name= (TextView) itemView.findViewById(R.id.lock_name);
            lock_time= (TextView) itemView.findViewById(R.id.lock_time);
            lock_state =(TextView) itemView.findViewById(R.id.lock_state);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            iv= (ImageView) itemView.findViewById(R.id.iv);
        }
        public void bind(List<passwordManagerBean.ListBean> mDatas, int position) {

            final passwordManagerBean.ListBean dataBean = mDatas.get(position);
            String keyboardPwd = dataBean.getKeyboardPwd();
            lock_name.setText(keyboardPwd);
            String Starttime =  dataBean.getStartDate()+"";
            String endtime =  dataBean.getEndDate()+"";

            if (!StringUtils.isEmpty(Starttime)&&!StringUtils.isEmpty(endtime)){
              /*  String substring = Starttime.substring(0,Starttime.length()-2);
                Log.d("TAG","时间"+ substring);
                String s = unixTime.dateToStamp(substring);*/
                String substring1 = Starttime.substring(0, Starttime.length() - 3);
                int startTime = Integer.parseInt(substring1);



            /*    String substringt = endtime.substring(0,endtime.length()-2);
                Log.d("TAG","时间"+ substring);
                String st = unixTime.dateToStamp(substringt);*/
                int end=0;
                if (!endtime.equals("0")){
                String substring1t = endtime.substring(0, endtime.length() - 3);
                end = Integer.parseInt(substring1t);
            }





                long timeStampSec = System.currentTimeMillis()/1000;
                String timestamp = String.format("%010d", timeStampSec);
                Log.d("TAG",""+timestamp);
                int current = Integer.parseInt(timestamp);

                if (startTime-current>0){
                  lock_state.setText("未生效");
                    iv.setImageResource(R.mipmap.mima);
                   lock_state.setTextColor(Color.parseColor("#778899"));

                }else if (!endtime.equals("0")){
                    //
                    if (startTime-current<0&&end-current>0){
                        lock_state.setText("已生效");
                        iv.setImageResource(R.mipmap.mima);
                        lock_state.setTextColor(Color.parseColor("#2E8B57"));
                    }




                }else if (endtime.equals(Starttime)){
                   lock_state.setText("已生效");
                    iv.setImageResource(R.mipmap.mima);
                    lock_state.setTextColor(Color.parseColor("#2E8B57"));

                }else if (!endtime.equals("0")){
                    //  end-current<0
                    lock_state.setText("已过期");
                   lock_state.setTextColor(Color.parseColor("#FF0000"));

                    iv.setImageResource(R.mipmap.icon_mima_unable);
                    re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));

                }

            }

            long timeStampSec = System.currentTimeMillis()/1000;
            String timestamp = String.format("%010d", timeStampSec);
            Log.d("TAG",""+timestamp);
            int i = Integer.parseInt(timestamp);
            String endTime = dataBean.getEndDate()+"";
            String startTime =  dataBean.getStartDate()+"";
            final int unlockType = dataBean.getKeyboardPwdType();
            String str=null;
         if(unlockType==2){
                str="永久";
            }else if(unlockType==3){
                str="限时";
            }else if(unlockType==4){
                str="删除";
            }else if(unlockType==1){
                str="单次";
            }else {
                str="循环";
            }

            if (!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime)){


                if (endTime.equals(startTime)){
                   // String substringStart = startTime.substring(0, startTime.length() - 5);
                  lock_time.setText(str);
                }else {

                    String substring1 = Starttime.substring(0, Starttime.length() - 3);
                    int kaishiTime = Integer.parseInt(substring1);
                    String substringStart = unixTime.stampToTime(kaishiTime);
                    if (!endtime.equals("0")){
                        String substring = endTime.substring(0, endTime.length() - 3);
                        int jieshuTime = Integer.parseInt(substring);
                        String substringendTime = unixTime.stampToTime(jieshuTime);
                        lock_time.setText(substringStart+"至"+substringendTime+str);
                    }else {
                        lock_time.setText(str);
                    }

                }

            }
            if(mOnItemLongClickListener != null){
               itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getLayoutPosition();
                        String keyboardPwd1 = dataBean.getKeyboardPwd();
                        int keyboardPwdId = dataBean.getKeyboardPwdId();
                        int keyboardPwdType = dataBean.getKeyboardPwdType();
                        mOnItemLongClickListener.onItemLongClick(itemView,position,keyboardPwd1,keyboardPwdId,keyboardPwdType);
                        //返回true 表示消耗了事件 事件不会继续传递
                        return true;
                    }
                });
            }

        }
    }




}
