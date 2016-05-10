package christian.network.entity;

import christian.network.utils.StaticData;

/**
 * Created by User on 07-Apr-16.
 */
public class Church {

    private String church_id;
    private String church_name;
    private String description;
    private String image_pp;
    private String image_cover;
    private boolean isFollowed;

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
}
