package christian.network.responses;

import java.util.ArrayList;

import christian.network.entity.Feed;

/**
 * Created by User on 27-Apr-16.
 */
public class AllFeedsResponse extends CommonResponse {

    private ArrayList<Feed> post;

    public ArrayList<Feed> getPost() {
        return post;
    }

    public void setPost(ArrayList<Feed> post) {
        this.post = post;
    }
}
