package kz.ncanode.pki;

import java.util.Date;

public class OCSPStatus {
    enum OCSPResult {
        ACTIVE,
        REVOKED,
        UNKNOWN
    }

    private final OCSPResult status;
    private final Date revokationTime;
    private final int revokationReason;

    public OCSPStatus(OCSPResult status, Date revokationTime, int revokationReason) {
        this.status           = status;
        this.revokationTime   = revokationTime;
        this.revokationReason = revokationReason;
    }

    public Date getRevokationTime() {
        return revokationTime;
    }

    public OCSPResult getStatus() {
        return status;
    }

    public int getRevokationReason() {
        return revokationReason;
    }
}
