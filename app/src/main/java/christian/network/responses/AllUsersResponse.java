package christian.network.responses;

import java.util.ArrayList;

import christian.network.entity.User;

/**
 * Created by User on 25-Apr-16.
 */
public class AllUsersResponse extends CommonResponse {

    private ArrayList<User> all_pastor_not_follow;
    private ArrayList<User> all_member_not_follow;

    public ArrayList<User> getAll_pastor_not_follow() {
        return all_pastor_not_follow;
    }

    public void setAll_pastor_not_follow(ArrayList<User> all_pastor_not_follow) {
        this.all_pastor_not_follow = all_pastor_not_follow;
    }

    public ArrayList<User> getAll_member_not_follow() {
        return all_member_not_follow;
    }

    public void setAll_member_not_follow(ArrayList<User> all_member_not_follow) {
        this.all_member_not_follow = all_member_not_follow;
    }
}
