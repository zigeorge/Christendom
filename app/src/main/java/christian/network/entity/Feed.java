package christian.network.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import christian.network.utils.StaticData;

/**
 * Created by User on 27-Apr-16.
 */
public class Feed implements Parcelable {

    private String post_id;
    private String poster_id;
    private String poster_type;
    private String post_image;
    private String description;
    private String post_created_date;
    private String id;
    private String cp_church_id;
    private String pastor_id;
    private String cp_status;
    private String created_date;
    private String user_id;
    private String church_id;
    private String church_name;
    private String first_name;
    private String last_name;
    private String email;
    private String contact_no;
    private String image;
    private String address;
    private String link;
    private String status;
    private String type;
    private String registered_with;
    private String join_date;
    private int no_of_comments;
    private int no_of_likes;
    private boolean is_like;

    protected Feed(Parcel in) {
        post_id = in.readString();
        poster_id = in.readString();
        poster_type = in.readString();
        post_image = in.readString();
        description = in.readString();
        post_created_date = in.readString();
        id = in.readString();
        cp_church_id = in.readString();
        pastor_id = in.readString();
        cp_status = in.readString();
        created_date = in.readString();
        user_id = in.readString();
        church_id = in.readString();
        church_name = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        email = in.readString();
        contact_no = in.readString();
        image = in.readString();
        address = in.readString();
        link = in.readString();
        status = in.readString();
        type = in.readString();
        registered_with = in.readString();
        join_date = in.readString();
        no_of_comments = in.readInt();
        no_of_likes = in.readInt();
        is_like = in.readByte() != 0;
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public String getChurch_name() {
        return church_name;
    }

    public void setChurch_name(String church_name) {
        this.church_name = church_name;
    }

    public int getNo_of_comments() {
        return no_of_comments;
    }

    public void setNo_of_comments(int no_of_comments) {
        this.no_of_comments = no_of_comments;
    }

    public int getNo_of_likes() {
        return no_of_likes;
    }

    public void setNo_of_likes(int no_of_likes) {
        this.no_of_likes = no_of_likes;
    }

    public boolean is_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
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
        if(TextUtils.isEmpty(post_image)){
            return "";
        }else {
            return StaticData.IMG_BASE_URL + post_image;
        }
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCp_church_id() {
        return cp_church_id;
    }

    public void setCp_church_id(String cp_church_id) {
        this.cp_church_id = cp_church_id;
    }

    public String getPastor_id() {
        return pastor_id;
    }

    public void setPastor_id(String pastor_id) {
        this.pastor_id = pastor_id;
    }

    public String getCp_status() {
        return cp_status;
    }

    public void setCp_status(String cp_status) {
        this.cp_status = cp_status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChurch_id() {
        return church_id;
    }

    public void setChurch_id(String church_id) {
        this.church_id = church_id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return StaticData.IMG_BASE_URL+image;
    }

    public String getProfileImage(String id){
        return StaticData.FACEBOOK_IMAGE_URL+id+StaticData.FACEBOOK_IMAGE_SIZE;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistered_with() {
        return registered_with;
    }

    public void setRegistered_with(String registered_with) {
        this.registered_with = registered_with;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

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
        dest.writeString(id);
        dest.writeString(cp_church_id);
        dest.writeString(pastor_id);
        dest.writeString(cp_status);
        dest.writeString(created_date);
        dest.writeString(user_id);
        dest.writeString(church_id);
        dest.writeString(church_name);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(email);
        dest.writeString(contact_no);
        dest.writeString(image);
        dest.writeString(address);
        dest.writeString(link);
        dest.writeString(status);
        dest.writeString(type);
        dest.writeString(registered_with);
        dest.writeString(join_date);
        dest.writeInt(no_of_comments);
        dest.writeInt(no_of_likes);
        dest.writeByte((byte) (is_like ? 1 : 0));
    }
}
