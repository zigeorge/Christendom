package christian.network.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import christian.network.R;

/**
 * Created by ppobd_six on 6/3/2016.
 */
public class NetworkDialogFragment extends DialogFragment {

    TextView tvDismiss, tvSettings;
    Context context;

    public NetworkDialogFragment() {

    }

    public static NetworkDialogFragment newInstance() {
        NetworkDialogFragment networkDialogFragment = new NetworkDialogFragment();
        return networkDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.network_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        initUI(view);
        addClickListener();
    }

    private void initUI(View view) {
        tvDismiss = (TextView) view.findViewById(R.id.tvDismiss);
        tvSettings = (TextView) view.findViewById(R.id.tvSettings);
    }

    private void addClickListener() {
        tvDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
    }

}
