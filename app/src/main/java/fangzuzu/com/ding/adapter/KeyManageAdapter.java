package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import fangzuzu.com.ding.bean.keyManagerBean;
import fangzuzu.com.ding.ui.activity.keyManagerDetialActivity;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;


/**
 * Created by yuanling on 2018/5/3.
 */

public class KeyManageAdapter extends RecyclerView.Adapter<KeyManageAdapter.keyViewHolder>  {


    private List<keyManagerBean.ListBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    private KeyManageAdapter.OnItemLongClickListener mOnItemLongClickListener;
    public KeyManageAdapter(List<keyManagerBean.ListBean> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        inflater=LayoutInflater.from(mContext);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position,String id,String userId);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public keyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.key_manage_item,parent,false);
        keyViewHolder holder=new keyViewHolder(view);
    ;
        return holder;
    }

    @Override
    public void onBindViewHolder(final keyViewHolder holder, int position) {

        keyManagerBean.ListBean dataBean = mDatas.get(position);


        String username = dataBean.getUsername();
        final int keyId = dataBean.getKeyId();
        final String[] strArray = username.split("_");
        System.out.println(strArray[1].trim());
        String senderUsername = dataBean.getSenderUsername();
        final String[] strArraysenderUsername = senderUsername.split("_");
        final String startTime = dataBean.getStartDate()+"";
        final String endTime = dataBean.getEndDate()+"";
      //  final String keyName = dataBean.getKeyName();

       holder.name.setText(strArray[1].trim());
     //   holder.startTime.setText(substringstartTime+"至"+substringendTime);
        long timeStampSec = System.currentTimeMillis()/1000;
        final String timestamp = String.format("%010d", timeStampSec);

        String date = dataBean.getDate()+"";
        String   sendTime = date.substring(0, date.length() - 3);
        int TTend = Integer.parseInt(sendTime);

        final String TTsendTime = unixTime.stampToTime(TTend );

        if (!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime)){
            if (!startTime.equals("0")&&!endTime.equals("0")){


            String substring = startTime.substring(0,startTime.length()-2);
            Log.d("TAG","时间"+ substring);
            String startDate = dataBean.getStartDate()+"";
            String   startTime1 = startDate.substring(0, startDate.length() - 3);
            int start = Integer.parseInt(startTime1);
            Log.d("TAG","时间"+ startTime);
            String substringstartTime = unixTime.stampToTime(start );

            String substringt = endTime.substring(0,endTime.length()-2);
            Log.d("TAG","时间"+ substring);
            String endDate = dataBean.getEndDate()+"";
            String   endTime1 = endDate.substring(0, endDate.length() - 3);
            int end = Integer.parseInt(endTime1);
            Log.d("TAG","结束时间撮"+ end);
            String substringendTime = unixTime.stampToTime(end );

            holder.startTime.setText(substringstartTime+"至"+substringendTime);


            Log.d("TAG",""+timestamp);
            int current = Integer.parseInt(timestamp);
            Log.d("TAG","当前时间错"+current);
            if (start-current>0){
                holder.state.setText("未生效");
                holder.state.setTextColor(Color.parseColor("#778899"));
                ;
            }else if (start-current<0&&end-current>0){
                holder.state.setText("已生效");
                holder.state.setTextColor(Color.parseColor("#2E8B57"));



            }else if (end-current<0&&!startTime.equals(endTime)){

                holder.state.setText("已过期");
                holder.state.setTextColor(Color.parseColor("#FF0000"));
                holder.re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));
                holder.iv.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.icon_key_manage_unable));
            }

            }else {

                holder.state.setText("已生效");
                holder.state.setTextColor(Color.parseColor("#2E8B57"));
                holder.startTime.setText("永久");
            }
        }


        Log.d("TAG",""+timestamp);

        if(mOnItemLongClickListener != null){
           holder. itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position =holder. getLayoutPosition();
                   // mOnItemLongClickListener.onItemLongClick(holder.itemView,position, id,userId);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }
            holder.re_adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, keyManagerDetialActivity.class);
                    intent.putExtra("KeyId",keyId+"");
                    intent.putExtra("username",strArray[1].trim());
                    intent.putExtra("senderUsername",strArraysenderUsername[1].trim());
                    intent.putExtra("startTime",startTime);
                    intent.putExtra("endTime",endTime);
                    intent.putExtra("TTsendTime",TTsendTime);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    Log.d("TAG",""+"keyId..."+keyId);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    class keyViewHolder extends RecyclerView.ViewHolder{
        TextView name,startTime,state;
        LinearLayout re_adapter;
        ImageView iv;
        public keyViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
            startTime= (TextView) itemView.findViewById(R.id.date_start);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            iv= (ImageView) itemView.findViewById(R.id.iv);
            state= (TextView) itemView.findViewById(R.id.state);
        }
    }

}
