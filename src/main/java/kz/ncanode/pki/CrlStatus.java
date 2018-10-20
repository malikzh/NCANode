package kz.ncanode.pki;

public class CrlStatus {
    enum CrlResult {
        ACTIVE,
        REVOKED
    }

    private String revokedBy;
    private CrlResult status;

    public CrlStatus(CrlResult status, String revokedBy) {
        this.status    = status;
        this.revokedBy = revokedBy;
    }

    public CrlResult getStatus() {
        return status;
    }

    public String getRevokedBy() {
        return revokedBy;
    }
}
