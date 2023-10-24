package com.example.raindriveiter1_10.ui.Settings;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.activity.NotificationActivity;
import com.example.raindriveiter1_10.R;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingsRootFragment extends Fragment {
    private MaterialCardView mcvNotification;
    private MaterialCardView mcvHelp;
    private FragmentManager fragmentManager;
    private Fragment fragment;




    public SettingsRootFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings__root_, container, false);
        MainActivity.toolbar.setTitle("Settings");
        mcvNotification = view.findViewById(R.id.mcv_notification);
        mcvHelp = view.findViewById(R.id.mcv_help);
        fragmentManager = getParentFragmentManager();
        mcvNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                fragment = new com.example.raindriveiter1_10.ui.Settings.NotificationFragment();
//                fragmentManager
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, fragment)
//                        .addToBackStack(null)
//                        .commit();
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        mcvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisclaimerDialog();
            }
        });
        return view;
    }
    private void openDisclaimerDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.layout_disclaimer_dialog);
        Button btnAccept = dialog.findViewById(R.id.btn_accept);
        btnAccept.setText("OK");
        Button btnDecline = dialog.findViewById(R.id.btn_decline);
        btnDecline.setVisibility(View.GONE);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int)(displayRectangle.width()),(int)(displayRectangle.height()));

    }

}