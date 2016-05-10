package christian.network.responses;

import christian.network.entity.User;

/**
 * Created by George on 06-Apr-16.
 */
public class SignUpResponse extends CommonResponse{

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
