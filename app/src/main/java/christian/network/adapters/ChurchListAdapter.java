package christian.network.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import christian.network.R;
import christian.network.entity.Church;
import christian.network.fragment.ChurchDialogFragment;

/**
 * Created by User on 10-Apr-16.
 */
public class ChurchListAdapter extends ArrayAdapter<Church> {

    private Context context;
    private int resId;
    private ArrayList<Church> churches, origChurches;
    private String user_id;
    ChurchFilter churchFilter;

    OnFollowChangedListener onFollowChangedListener;
    OnChurchDetailsPressedListener onChurchDetailsPressedListener;

    public ChurchListAdapter(Context context, int resource, ArrayList<Church> churches, String user_id) {
        super(context, resource, churches);
        this.context = context;
        this.resId = resource;
        this.churches = churches;
        this.user_id = user_id;
        this.origChurches = churches;

        try {
            onFollowChangedListener = (OnFollowChangedListener) context;
            onChurchDetailsPressedListener = (OnChurchDetailsPressedListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFollowChangedListener");
        }

    }

    private class ViewHolder {
        ImageView ivChurchPp;
        TextView tvChurchName;
        TextView tvChurchDesc;
        ImageView ivFollow;
        LinearLayout llChurchDetails;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.ivChurchPp = (ImageView) convertView.findViewById(R.id.ivChurchPp);
            holder.tvChurchDesc = (TextView) convertView.findViewById(R.id.tvChurchDesc);
            holder.tvChurchName = (TextView) convertView.findViewById(R.id.tvChurchName);
            holder.ivFollow = (ImageView) convertView.findViewById(R.id.ivEditComment);
            holder.llChurchDetails = (LinearLayout) convertView.findViewById(R.id.llChurchDetails);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Sizes",churches.size()+"");
                Church church = churches.get(position);
                if(church.isFollowed()){
                    churches.get(position).setIsFollowed(false);
                    onFollowChangedListener.onFollowChanged(churches.get(position), false);
                }else {
                    for (Church aChurch :
                            churches) {
                        if (aChurch.isFollowed()) {
                            aChurch.setIsFollowed(false);
                        }
                    }
                    churches.get(position).setIsFollowed(true);
                    Log.e("Name",church.getChurch_name());
                    onFollowChangedListener.onFollowChanged(churches.get(position), true);
                }
                notifyDataSetChanged();
            }
        });

        holder.llChurchDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Church Details on dialogue
                Church aChurch=churches.get(position);
                onChurchDetailsPressedListener.onChurchDetailsPressed(aChurch);
            }
        });

        if (position < churches.size()) {
            setDataInRow(position, holder);
        }

        return convertView;
    }

    private void setDataInRow(int position, ViewHolder holder) {
        Church church = churches.get(position);
        holder.tvChurchName.setText(church.getChurch_name());
        holder.tvChurchDesc.setText(church.getDescription());
        if (!church.isFollowed()) {
            holder.ivFollow.setImageResource(R.drawable.church_unfollow);
        } else {
            holder.ivFollow.setImageResource(R.drawable.church_follow);
        }
        Log.e("Profile Picture",church.getImage_pp());
        Picasso.with(context).load(church.getImage_pp()).placeholder(R.drawable.profile_thumb).into(holder.ivChurchPp);
    }

    public void resetData() {
        churches = origChurches;
    }

    @Override
    public Filter getFilter() {
        if (churchFilter == null)
            churchFilter = new ChurchFilter();

        return churchFilter;
    }

    public interface OnFollowChangedListener {
        public void onFollowChanged(Church church, boolean hasFollowed);
    }

    public interface OnChurchDetailsPressedListener{
        public void onChurchDetailsPressed(Church church);
    }

    private class ChurchFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = churches;
                results.count = churches.size();
            }
            else {
                // We perform filtering operation
                List<Church> nChurchList = new ArrayList<Church>();

                for (Church church : churches) {
                    if (church.getChurch_name().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nChurchList.add(church);
                }

                results.values = nChurchList;
                results.count = nChurchList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                churches = (ArrayList<Church>) results.values;
                notifyDataSetChanged();
            }
        }
    }

}
