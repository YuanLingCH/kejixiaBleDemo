package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.content.Intent;
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
import fangzuzu.com.ding.bean.Icbean;
import fangzuzu.com.ding.ui.activity.icLockItemMessageActvity;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by lingyuan on 2018/7/5.
 */

public class IcListAdapter extends RecyclerView.Adapter<IcListAdapter.IcViewHolder> {

    private List<Icbean.ListBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }


    public IcListAdapter(List<Icbean.ListBean> mDatas,Context mContext) {
        this.mDatas = mDatas;
        this.mDatas = mDatas;
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);
    }

    @Override
    public IcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.ic_list_layout,parent,false);
        final IcViewHolder holder=new IcViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(IcViewHolder holder, final int position) {
        final Icbean.ListBean  bean= mDatas.get(position);

        final String cardName = bean.getCardName();
        holder.lock_elcet.setText(cardName);

        final String Starttime =  bean.getStartDate()+"";
        final String endtime = bean.getEndDate()+"";
        Log.d("TAG","开始时间"+Starttime);
        Log.d("TAG","结束时间"+endtime);
        if (!Starttime.equals("0")&&!endtime.equals("0")){
            String substring = Starttime.substring(0,Starttime.length()-3);
            Log.d("TAG","时间"+ substring);


            int startTime = Integer.parseInt(substring);
            Log.d("TAG","时间"+ startTime);


            String substringt = endtime.substring(0,endtime.length()-3);
            Log.d("TAG","时间"+ substring);


            int end = Integer.parseInt(substringt);
            Log.d("TAG","时间"+ end);



            long timeStampSec = System.currentTimeMillis()/1000;
            String timestamp = String.format("%010d", timeStampSec);
            Log.d("TAG",""+timestamp);
            int current = Integer.parseInt(timestamp);
            Log.d("TAG",""+current);
            if (startTime-current>0){
                holder.lock_state.setText("未生效");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state.setBackgroundColor(Color.parseColor("#EC8325"));
             //   holder.lock_state.setTextColor(Color.parseColor("#6B6B6B"));
                holder.re_adapter.setEnabled(false);
                holder.re_adapter.setBackgroundColor(220);
                    holder.iv.setImageResource(R.mipmap.ic_unable);


            }else if (startTime-current<0&&end-current>0){

                holder.lock_state.setText("限时");
                holder.lock_state_one.setTextColor(Color.GREEN);
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
             //   holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
             //   holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);

                    holder.iv.setImageResource(R.mipmap.ic_enable);

            }else if (end-current<0&&!Starttime.equals(endtime)){
                //  holder.iv.setImageResource(R.drawable.door_logo);
                holder.lock_state.setText("限时");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已过期");
                holder.lock_state_one.setTextColor(Color.RED);
             //   holder.lock_state.setTextColor(Color.parseColor("#EE0000"));
              //  holder.re_adapter.setEnabled(false);
             //   holder.lock_state.setBackgroundColor(Color.TRANSPARENT);
              //  holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));

                    holder.iv.setImageResource(R.mipmap.ic_unable);


                //  holder.iv.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.door_logo));


            }else if(Starttime.equals(endtime)){
              //  holder.lock_state.setText("已生效");
                holder.lock_state.setText("永久");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
                holder.lock_state_one.setTextColor(Color.GREEN);
              //  holder.lock_state.setTextColor(Color.TRANSPARENT);

              //  holder.lock_state.setBackgroundColor(Color.parseColor("#F14448"));
               // holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);

                    holder.iv.setImageResource(R.mipmap.ic_enable);



            }else if(StringUtils.isEmpty(Starttime)){
                holder.lock_state.setText("永久");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
                holder.lock_state_one.setTextColor(Color.GREEN);
              //  holder.lock_state.setTextColor(Color.TRANSPARENT);
              //  holder.lock_state.setBackgroundColor(Color.parseColor("#F14448"));
              //  holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);

                    holder.iv.setImageResource(R.mipmap.ic_enable);

            }else if (Starttime.equals(endtime)){
                holder.lock_state.setText("永久");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
                holder.lock_state_one.setTextColor(Color.GREEN);
              //  holder.lock_state.setTextColor(Color.TRANSPARENT);
              //  holder.lock_state.setBackgroundColor(Color.parseColor("#F14448"));
             //   holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);

                    holder.iv.setImageResource(R.mipmap.ic_enable);
                }

        }


        long timeStampSec = System.currentTimeMillis()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        Log.d("TAG",""+timestamp);
        int i = Integer.parseInt(timestamp);
        Log.d("TAG",""+i);
        //  String s = unixTime.dateToStamp(Starttime);


        String endTime = bean.getEndDate()+"";
        String startTime = bean.getStartDate()+"";
        if (endTime.equals("0")||startTime.equals("0")){
            holder.lock_state.setText("永久");
            holder.lock_state_one.setTextColor(Color.GREEN);
            holder.lock_state.setTextColor(Color.BLACK);
            holder.lock_state_one.setText("已生效");

                holder.iv.setImageResource(R.mipmap.ic_enable);
            String createDate = bean.getCreateDate()+"";
            String substring = createDate.substring(0, createDate.length() - 3);
            int start = Integer.parseInt(substring);
            Log.d("TAG","结束时间撮"+ start);
            String TTcreateTime = unixTime.stampToTime(start );
            holder.lock_time.setText(TTcreateTime);

        }else {
            String substringStart = startTime.substring(0, startTime.length() - 3);
            int start = Integer.parseInt(substringStart);
            Log.d("TAG","结束时间撮"+ start);
            String TTstart = unixTime.stampToTime(start );
            String substringendTime  = endTime .substring(0, endTime .length() - 3);
            int end = Integer.parseInt(substringendTime);
            Log.d("TAG","结束时间撮"+ end);
            String TTendTime = unixTime.stampToTime(end );
            holder.lock_time.setText(TTstart+"至"+TTendTime);

                holder.iv.setImageResource(R.mipmap.ic_enable);

        }

        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    mOnItemLongClickListener.onItemLongClick(v,position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }

        holder.re_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] split = bean.getUserId().split("_");   //  addType
                String createDate = bean.getCreateDate()+"";
                String substring = createDate.substring(0, createDate.length() - 3);
                int start = Integer.parseInt(substring);
                Log.d("TAG","结束时间撮"+ start);
                String TTcreateTime = unixTime.stampToTime(start );
                String cardNumber = bean.getCardNumber();
                Intent  intent=new Intent(mContext, icLockItemMessageActvity.class);
                intent.putExtra("cardName",cardName );
                intent.putExtra("userId",split[1].trim() );
                if (!Starttime.equals("0")&&!endtime.equals("0")){
                    intent.putExtra("addType","0");  //限时 0
                }else {
                    intent.putExtra("addType","1");
                }
                intent.putExtra("icCardId",bean.getCardId()+"");
                intent.putExtra("TTcreateTime",TTcreateTime);
                intent.putExtra("cardNumber",cardNumber);
                intent.putExtra("type","0");  //  1为指纹  0 ic卡
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class IcViewHolder extends RecyclerView.ViewHolder{
       TextView  lock_time,lock_state,lock_elcet,lock_state_one;
        LinearLayout re_adapter;
        ImageView iv;
        public IcViewHolder(View itemView) {
            super(itemView);
            lock_state= (TextView) itemView.findViewById(R.id.lock_state);
            lock_time= (TextView) itemView.findViewById(R.id.lock_time);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            lock_elcet= (TextView) itemView.findViewById(R.id.lock_elcet);
            iv= (ImageView) itemView.findViewById(R.id.iv);
            lock_state_one=itemView.findViewById(R.id.lock_state_one);
        }
    }
}
