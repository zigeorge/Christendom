package christian.network.entity;

import christian.network.utils.StaticData;

/**
 * Created by ppobd_six on 6/8/2016.
 */
public class Friend {
    private String id;
    private String follower_id;
    private String followee_id;
    private String created_date;
    private String status;
    private String church_id;
    private String first_name;
    private String last_name;
    private String email;
    private String contact_no;
    private String image;
    private String address;
    private String link;
    private String type;
    private String registered_with;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

    public String getFollowee_id() {
        return followee_id;
    }

    public void setFollowee_id(String followee_id) {
        this.followee_id = followee_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfileImage(String userId){
        return StaticData.FACEBOOK_IMAGE_URL+userId+StaticData.FACEBOOK_IMAGE_SIZE;
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

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
