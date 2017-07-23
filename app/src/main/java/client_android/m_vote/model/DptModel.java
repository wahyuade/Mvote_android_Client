package client_android.m_vote.model;

/**
 * Created by wahyuade on 20/07/17.
 */

public class DptModel{
    private String nrp;
    private String token;
    private String privat;
    private String n;

    public DptModel(String nrp, String token, String privat, String n) {
        this.nrp = nrp;
        this.token = token;
        this.privat = privat;
        this.n = n;
    }

    public String getNrp() {
        return nrp;
    }

    public String getToken() {
        return token;
    }

    public String getPrivat() {
        return privat;
    }

    public String getN() {
        return n;
    }

    public void setNrp(String nrp) {
        this.nrp = nrp;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPrivat(String privat) {
        this.privat = privat;
    }

    public void setN(String n) {
        this.n = n;
    }
}