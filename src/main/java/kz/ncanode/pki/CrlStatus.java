package kz.ncanode.pki;

import java.util.Date;

public class CrlStatus {
    enum CrlResult {
        ACTIVE,
        REVOKED
    }

    private String revokedBy;
    private CrlResult status;
    private Date date;
    private String reason;

    public CrlStatus(CrlResult status, String revokedBy, Date date, String reason) {
        this.status    = status;
        this.revokedBy = revokedBy;
        this.date      = date;
        this.reason    = reason;
    }

    public CrlResult getStatus() {
        return status;
    }

    public String getRevokedBy() {
        return revokedBy;
    }

    public Date getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }
}
