package com.lptiyu.tanke.messagesystem.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.database.Message;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.Inflater;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.List;


/**
 * author:wamcs
 * date:2016/6/4
 * email:kaili@hustunique.com
 */
public class PushAdapter extends MessageBaseAdapter {

    private List<Message> messageList;

    private static final int VIEW_TYPE_TIME = 1;
    private static final int VIEW_TYPE_MESSAGE = 2;
    private long currentTime;
    private Context context;
    private Drawable avatar;

    public PushAdapter(Context context,List<Message> messageList) {
        this.messageList = messageList;
        this.context = context;
        currentTime = TimeUtils.getCurrentDate();
        init();

    }

    private void init(){
        int size = (int) context.getResources().getDimension(R.dimen.message_img_size);
        Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap avatarBitmap = createCircleImage(
                convertDrawable2Bitmap(drawable),
                size
        );
        avatar = new BitmapDrawable(avatarBitmap);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TIME:
                return new TimeViewHolder(Inflater.inflate(R.layout.layout_message_time, parent, false));
            case VIEW_TYPE_MESSAGE:
                return new MessageViewHolder(Inflater.inflate(R.layout.layout_message_item_left, parent, false));
            default:
                return new MessageViewHolder(Inflater.inflate(R.layout.layout_message_item_left, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW_TYPE_TIME:
                TimeViewHolder holder1 = (TimeViewHolder) holder;
                long time = messageList.get(position).getTime();
                if (time>= currentTime){
                    holder1.mTime.setText(TimeUtils.parsePartTime(time));
                }else {
                    holder1.mTime.setText(TimeUtils.parseCompleteTime(time));
                }
                break;
            case VIEW_TYPE_MESSAGE:
                MessageViewHolder holder2 = (MessageViewHolder) holder;
                holder2.mAvatar.setBackground(avatar);
                holder2.mMessage.setText(addURLLink(messageList.get(position).getAlert()));
                break;



        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getType() == Conf.TIME_TYPE) {
            return VIEW_TYPE_TIME;
        }
        return VIEW_TYPE_MESSAGE;
    }



    private Bitmap createCircleImage(Bitmap source, int size){
        final Paint paint =new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(size/2,size/2,size/2,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,0,0,paint);
        return target;
    }

    private Bitmap convertDrawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
