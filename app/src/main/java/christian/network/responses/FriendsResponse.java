package christian.network.responses;

import java.util.ArrayList;

import christian.network.entity.Friend;

/**
 * Created by ppobd_six on 6/8/2016.
 */
public class FriendsResponse extends CommonResponse {
    ArrayList<Friend> user_following;

    public ArrayList<Friend> getUser_following() {
        return user_following;
    }

    public void setUser_following(ArrayList<Friend> user_following) {
        this.user_following = user_following;
    }
}
