package client_android.m_vote.model;

/**
 * Created by wahyuade on 26/07/17.
 */
public class DeviceModel {
    private String uuid;
    private String status;
    private Double latitude;
    private Double longitude;

    public DeviceModel(String uuid, String status, Double latitude, Double longitude) {
        this.uuid = uuid;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public DeviceModel() {
    }

}
