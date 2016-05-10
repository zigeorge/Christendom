package christian.network.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by User on 08-Mar-16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private boolean isCompanion;

    public PagerAdapter(FragmentManager fm,
                        List<Fragment> fragments, boolean isCompanion) {
        super(fm);
        this.fragments = fragments;
        this.isCompanion = isCompanion;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                if (isCompanion) {
                    return "Members";
                } else {
                    return "Pastors";
                }
            case 1:
                if (isCompanion) {
                    return "Pastors";
                } else {
                    return "Members";
                }
            default:
                return super.getPageTitle(position);
        }
    }
}
