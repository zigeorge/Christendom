package christian.network.responses;

import java.util.ArrayList;

import christian.network.entity.Church;

/**
 * Created by User on 07-Apr-16.
 */
public class AllChurchResponse extends CommonResponse {

    private ArrayList<Church> church;

    public ArrayList<Church> getChurch() {
        return church;
    }

    public void setChurch(ArrayList<Church> church) {
        this.church = church;
    }
}
