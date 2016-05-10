package christian.network.responses;

import java.util.ArrayList;

import christian.network.entity.Comment;

/**
 * Created by user pc on 5/1/2016.
 */
public class CommentResponse extends CommonResponse {
    ArrayList<Comment> comments;

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
