package christian.network.entity;

import android.os.Parcel;
import android.os.Parcelable;

import christian.network.utils.StaticData;

/**
 * Created by user pc on 5/1/2016.
 */
public class Comment implements Parcelable {
    String post_id;
    String poster_id;
    String poster_type;
    String post_image;
    String description;
    String post_created_date;
    String comment_id;
    String user_id;
    String type;
    String created_date;
    String first_name;
    String last_name;
    String image;

    public Comment() {

    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getImage() {
        return StaticData.IMG_BASE_URL + image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public String getPoster_type() {
        return poster_type;
    }

    public void setPoster_type(String poster_type) {
        this.poster_type = poster_type;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost_created_date() {
        return post_created_date;
    }

    public void setPost_created_date(String post_created_date) {
        this.post_created_date = post_created_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public static Creator<Comment> getCREATOR() {
        return CREATOR;
    }

    protected Comment(Parcel in) {
        post_id = in.readString();
        poster_id = in.readString();
        poster_type = in.readString();
        post_image = in.readString();
        description = in.readString();
        post_created_date = in.readString();
        comment_id = in.readString();
        user_id = in.readString();
        type = in.readString();
        created_date = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        image = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(post_id);
        dest.writeString(poster_id);
        dest.writeString(poster_type);
        dest.writeString(post_image);
        dest.writeString(description);
        dest.writeString(post_created_date);
        dest.writeString(comment_id);
        dest.writeString(user_id);
        dest.writeString(type);
        dest.writeString(created_date);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(image);
    }
}
