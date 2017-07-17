package client_android.m_vote.model;

/**
 * Created by wahyuade on 16/07/17.
 */
public class LoginModel {
    private boolean success;
    private String message;
    private String local;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getLocal() {
        return local;
    }

    public LoginModel(boolean success, String message, String local) {
        this.success = success;
        this.message = message;
        this.local = local;
    }
}
