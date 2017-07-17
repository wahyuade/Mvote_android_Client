package client_android.m_vote.model;

/**
 * Created by wahyuade on 16/07/17.
 */
public class DefaultModel {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DefaultModel(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
