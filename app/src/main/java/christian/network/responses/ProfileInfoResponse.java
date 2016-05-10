package christian.network.responses;

import java.util.ArrayList;

import christian.network.entity.User;
import christian.network.entity.Feed;

/**
 * Created by ppobd_six on 5/5/2016.
 */
public class ProfileInfoResponse extends CommonResponse {
    ArrayList<Feed> post;
    User user;

    public ArrayList<Feed> getPost() {
        return post;
    }

    public void setPost(ArrayList<Feed> post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
