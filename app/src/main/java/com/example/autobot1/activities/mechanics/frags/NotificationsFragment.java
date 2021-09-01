package com.example.autobot1.activities.mechanics.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.autobot1.R;
import com.example.autobot1.adapters.NotificationAdapter;
import com.example.autobot1.databinding.FragmentNotificationsBinding;
import com.example.autobot1.models.Notification;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NotificationAdapter(requireContext(),R.layout.notification_item,getNotifications());
    }

    private List<Notification> getNotifications() {
        return new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater,container,false);
        inflateListView();
        return binding.getRoot();
    }

    private void inflateListView() {
        binding.notificationLv.setClipToPadding(false);
        binding.notificationLv.setAdapter(adapter);
        adapter.setOnLongClickListener(position -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(),binding.notificationLv);
            popupMenu.getMenu().add("Delete");
            popupMenu.getMenu().add("Cancel");
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getTitle().equals("Delete")){
                    doDelete(menuItem.getTitle());
                }else {
                    popupMenu.dismiss();
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void doDelete(CharSequence title) {
       //todo:perform delete
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}