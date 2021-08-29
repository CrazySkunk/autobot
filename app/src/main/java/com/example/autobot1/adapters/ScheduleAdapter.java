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
import com.example.autobot1.activities.mechanics.models.Bookings;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleAdapter extends ArrayAdapter<Bookings> {
    private final Context context;
    private final int resource;
    private final List<Bookings> bookings;
    private int lastPosition = -1;

    public ScheduleAdapter(@NonNull Context context, int resource, @NonNull List<Bookings> bookings) {
        super(context, resource, bookings);
        this.context = context;
        this.resource = resource;
        this.bookings = bookings;
    }

    @Override
    public Bookings getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View result;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.booking_item_image);
            viewHolder.name = convertView.findViewById(R.id.booking_item_name);
            viewHolder.meetUpTime = convertView.findViewById(R.id.date_time_tv);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        Picasso.get().load(bookings.get(position).getByImageUrl()).placeholder(R.drawable.account_circle).into(viewHolder.imageView);
        viewHolder.name.setText(bookings.get(position).getByName());
        viewHolder.meetUpTime.setText(bookings.get(position).getMeetUpTime());
        viewHolder.pickUpTime.setText(bookings.get(position).getPickUpTime());
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        return convertView;
    }

    private static class ViewHolder {
        public TextView name, meetUpTime, pickUpTime;
        public CircleImageView imageView;
    }
}
