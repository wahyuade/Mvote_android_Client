package client_android.m_vote.model;

/**
 * Created by wahyuade on 20/07/17.
 */

public class VerifyModel {
    private boolean success;
    private String message;
    private DptModel data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DptModel getData() {
        return data;
    }


    public void setData(DptModel data) {
        this.data = data;
    }

    public VerifyModel(boolean success, String message, DptModel data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }


    public VerifyModel() {
    }
}
