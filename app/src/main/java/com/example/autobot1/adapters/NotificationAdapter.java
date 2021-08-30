package com.example.autobot1.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.autobot1.R;
import com.example.autobot1.models.Notification;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    final private Context context;
    final private int resource;
    final private List<Notification> notifications;
    private int lastPosition = -1;
    private OnLongClick listener;

    public NotificationAdapter(@NonNull Context context, int resource, @NonNull List<Notification> notifications) {
        super(context, resource, notifications);
        this.context = context;
        this.resource = resource;
        this.notifications = notifications;
    }
    public interface OnLongClick{
        void onLongClick(int position);
    }
    public void setOnLongClickListener(OnLongClick listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View result;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.title = convertView.findViewById(com.example.autobot1.R.id.notification_title_tv);
            viewHolder.description = convertView.findViewById(com.example.autobot1.R.id.notification_description_tvv);
            viewHolder.time = convertView.findViewById(com.example.autobot1.R.id.notification_date_time_tv);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.time.setText(notifications.get(position).getTitle());
        viewHolder.description.setText(notifications.get(position).getDescription());
        viewHolder.time.setText(notifications.get(position).getTime());
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        convertView.setOnLongClickListener(view -> {
            if (position!=-1){
                listener.onLongClick(position);
            }
            return true;
        });
        return convertView;
    }

    static class ViewHolder {
        TextView title, description, time;
    }
}
