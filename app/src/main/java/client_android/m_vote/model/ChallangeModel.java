package client_android.m_vote.model;

/**
 * Created by wahyuade on 21/07/17.
 */

public class ChallangeModel {
    private boolean success;
    private String message;
    private Integer c;

    public ChallangeModel(boolean success, String message, Integer c) {
        this.success = success;
        this.message = message;
        this.c = c;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getC() {
        return c;
    }
}