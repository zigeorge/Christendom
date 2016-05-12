package christian.network.entity;

import android.os.Parcel;
import android.os.Parcelable;

import christian.network.utils.StaticData;

/**
 * Created by User on 07-Apr-16.
 */
public class Church implements Parcelable {

    private String church_id;
    private String church_name;
    private String description;
    private String image_pp;
    private String image_cover;
    private boolean isFollowed;

    protected Church(Parcel in) {
        church_id = in.readString();
        church_name = in.readString();
        description = in.readString();
        image_pp = in.readString();
        image_cover = in.readString();
        isFollowed = in.readByte() != 0;
    }

    public static final Creator<Church> CREATOR = new Creator<Church>() {
        @Override
        public Church createFromParcel(Parcel in) {
            return new Church(in);
        }

        @Override
        public Church[] newArray(int size) {
            return new Church[size];
        }
    };

    public String getImage_pp() {
        return StaticData.IMG_BASE_URL + image_pp;
    }

    public void setImage_pp(String image_pp) {
        this.image_pp = image_pp;
    }

    public String getImage_cover() {
        return StaticData.IMG_BASE_URL + image_cover;
    }

    public void setImage_cover(String image_cover) {
        this.image_cover = image_cover;
    }

    public String getChurch_id() {
        return church_id;
    }

    public void setChurch_id(String church_id) {
        this.church_id = church_id;
    }

    public String getChurch_name() {
        return church_name;
    }

    public void setChurch_name(String church_name) {
        this.church_name = church_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(church_id);
        dest.writeString(church_name);
        dest.writeString(description);
        dest.writeString(image_pp);
        dest.writeString(image_cover);
        dest.writeByte((byte) (isFollowed ? 1 : 0));
    }
}
