package com.example.autobot1.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.autobot1.R;
import com.example.autobot1.models.Request;
import com.example.autobot1.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingsAdapter extends ArrayAdapter<Request> {
    private static final String TAG = "BookingsAdapter";
    private final Context context;
    private final int resource;
    private final List<Request> bookings;
    private int lastPosition = -1;
    private User u;
    private OnItemClick listener;

    public BookingsAdapter(@NonNull Context context, int resource, @NonNull List<Request> bookings) {
        super(context, resource, bookings);
        this.context = context;
        this.resource = resource;
        this.bookings = bookings;
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClick listener){
        this.listener = listener;
    }

    @Override
    public Request getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        View result;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.booking_item_image);
            viewHolder.name = convertView.findViewById(R.id.booking_item_name);
            viewHolder.meetUpTime = convertView.findViewById(R.id.time);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        User user = getUser(bookings.get(position).getFrom());
        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.account_circle).into(viewHolder.imageView);
        viewHolder.name.setText(bookings.get(position).getFromName());
        viewHolder.meetUpTime.setText(simpleDateFormat.format(bookings.get(position).getTime()));
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        result.setOnClickListener(view -> listener.onItemClick(position));
        return convertView;
    }
    private User getUser(String uid) {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User user1 = user.getValue(User.class);
                            if (user1 != null)
                                if (user1.getUid().equals(uid)){
                                    u =user1;
                                }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: getTypeOfUser -> " + error.getMessage());
                    }
                });
        return u;
    }

    private static class ViewHolder {
        public TextView name, meetUpTime;
        public CircleImageView imageView;
    }
}
