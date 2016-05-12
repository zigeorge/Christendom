package christian.network.entity;

import android.os.Parcel;
import android.os.Parcelable;

import christian.network.utils.StaticData;

/**
 * Created by User on 06-Apr-16.
 */
public class User implements Parcelable {

    private String id;
    private String user_id;
    private String church_id;
    private String full_name;
    private String first_name;
    private String last_name;
    private String email;
    private String contact_no;
    private String dateOfBirth;
    private String gender;
    private String image;
    private String address;
    private String type;
    private boolean isFollowed;
    private String image_cover;
    private String church_name;

    protected User(Parcel in) {
        id = in.readString();
        user_id = in.readString();
        church_id = in.readString();
        full_name = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        email = in.readString();
        contact_no = in.readString();
        dateOfBirth = in.readString();
        gender = in.readString();
        image = in.readString();
        address = in.readString();
        type = in.readString();
        isFollowed = in.readByte() != 0;
        image_cover = in.readString();
        church_name = in.readString();
    }

    public User(){

    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFull_name() {
        return first_name+" "+last_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return StaticData.IMG_BASE_URL + image;
    }

    public String getProfileImage(String userId){
        return StaticData.FACEBOOK_IMAGE_URL+userId+StaticData.FACEBOOK_IMAGE_SIZE;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getImage_cover() {
        return StaticData.IMG_BASE_URL+image_cover;
    }

    public void setImage_cover(String image_cover) {
        this.image_cover = image_cover;
    }

    public String getChurch_name() {
        return church_name;
    }

    public void setChurch_name(String church_name) {
        this.church_name = church_name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_id);
        dest.writeString(church_id);
        dest.writeString(full_name);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(email);
        dest.writeString(contact_no);
        dest.writeString(dateOfBirth);
        dest.writeString(gender);
        dest.writeString(image);
        dest.writeString(address);
        dest.writeString(type);
        dest.writeString(image_cover);
        dest.writeString(church_name);
        dest.writeByte((byte) (isFollowed ? 1 : 0));
    }
}
