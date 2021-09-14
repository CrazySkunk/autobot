package com.example.autobot1.activities.mechanics.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.NotificationViewModel;
import com.example.autobot1.adapters.NotificationAdapter;
import com.example.autobot1.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;


public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationAdapter adapter;
    private NotificationViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        inflateListView();
        return binding.getRoot();
    }

    private void inflateListView() {
        viewModel.getNotification(FirebaseAuth.getInstance().getUid()).observe(getViewLifecycleOwner(), notifications -> {
            if (notifications.isEmpty()) {
                binding.noNotificationsYetIm.setVisibility(View.VISIBLE);
                binding.noNotificationsYetTv.setVisibility(View.VISIBLE);
                binding.notificationLv.setVisibility(View.GONE);
            } else {
                adapter = new NotificationAdapter(requireContext(), R.layout.notification_item, notifications);
                binding.noNotificationsYetIm.setVisibility(View.GONE);
                binding.noNotificationsYetTv.setVisibility(View.GONE);
                binding.notificationLv.setVisibility(View.VISIBLE);
                binding.notificationLv.setClipToPadding(false);
                binding.notificationLv.setAdapter(adapter);
                adapter.setOnLongClickListener(position -> {
                    PopupMenu popupMenu = new PopupMenu(requireContext(), binding.notificationLv);
                    popupMenu.getMenu().add("Delete");
                    popupMenu.getMenu().add("Cancel");
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        if (menuItem.getTitle().equals("Delete")) {
                            doDelete(menuItem.getTitle());
                        } else {
                            popupMenu.dismiss();
                        }
                        return true;
                    });
                    popupMenu.show();
                });
            }
        });

    }

    private void doDelete(CharSequence title) {
        Toast.makeText(requireContext(), "Delete " + title + " clicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}