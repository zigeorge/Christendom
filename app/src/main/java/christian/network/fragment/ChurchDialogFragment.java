package christian.network.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import christian.network.R;
import christian.network.entity.Church;
import christian.network.utils.StaticData;

/**
 * Created by ppobd_six on 5/11/2016.
 */
public class ChurchDialogFragment extends DialogFragment {

    ImageView ivChurchCoverImage, ivChurchProfileImage;
    TextView tvChurchName, tvChurchDescription, tvFollowChurch, tvClose;

    OnChurchFollowedListener onChurchFollowedListener;

    Church church;
    Context context;

    public ChurchDialogFragment(){

    }

    public static ChurchDialogFragment newInstance(Church church){
        ChurchDialogFragment churchDialogFragment = new ChurchDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(StaticData.CHURCH,church);
        churchDialogFragment.setArguments(args);
        return churchDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            onChurchFollowedListener = (OnChurchFollowedListener) getActivity();

        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnChurchFollowedListener");
        }
        return inflater.inflate(R.layout.church_fragment_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        church = getArguments().getParcelable(StaticData.CHURCH);
        initUI(view);
        setDataInUI(church);
        initActions();

    }

    private void initUI(View view){
        ivChurchCoverImage = (ImageView) view.findViewById(R.id.ivChurchCoverImage);
        ivChurchProfileImage = (ImageView) view.findViewById(R.id.ivChurchProfileImage);
        tvChurchName = (TextView) view.findViewById(R.id.tvChurchName);
        tvChurchDescription = (TextView) view.findViewById(R.id.tvChurchDescription);
        tvFollowChurch = (TextView) view.findViewById(R.id.tvFollowChurch);
        tvClose = (TextView) view.findViewById(R.id.tvClose);
    }

    private void setDataInUI(Church church){
        Log.e("Cover Image", church.getImage_cover());
        Log.e("Profile Image", church.getImage_pp());
        Picasso.with(context).load(church.getImage_cover()).resize(840,480).into(ivChurchCoverImage);
        Picasso.with(context).load(church.getImage_pp()).resize(300,300).into(ivChurchProfileImage);
        tvChurchName.setText(church.getChurch_name());
        tvChurchDescription.setText(church.getDescription());
    }

    private void initActions(){
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvFollowChurch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChurchFollowedListener.onChurchFollowed(church);
            }
        });
    }

    public interface OnChurchFollowedListener{
        public void onChurchFollowed(Church church);
    }
}
