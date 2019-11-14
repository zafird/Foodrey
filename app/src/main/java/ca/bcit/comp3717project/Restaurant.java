package ca.bcit.comp3717project;

import java.io.Serializable;

public class Restaurant  implements Serializable {
    private String FACTYPE;
    private String HazardRating;
    private String InspType;
    private String InspectionDate;
    private String LATITUDE;
    private String LONGITUDE;
    private String NAME;
    private int NumCritical;
    private int NumNonCritical;
    private String PHYSICALADDRESS;
    private String PHYSICALCITY;
    private String TrackingNumber;
    private String ViolLump;
    private boolean _favorite;

    public Restaurant() {
    }

/*    public Restaurant(String FACTYPE, String hazardRating, String inspType, String inspectionDate, String LATITUDE, String LONGITUDE, String NAME, int numCritical, int numNonCritical, String PHYSICALADDRESS, String PHYSICALCITY, String trackingNumber, String violLump) {
        this.FACTYPE = FACTYPE;
        HazardRating = hazardRating;
        InspType = inspType;
        InspectionDate = inspectionDate;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
        this.NAME = NAME;
        NumCritical = numCritical;
        NumNonCritical = numNonCritical;
        this.PHYSICALADDRESS = PHYSICALADDRESS;
        this.PHYSICALCITY = PHYSICALCITY;
        TrackingNumber = trackingNumber;
        ViolLump = violLump;
    }*/

// Getter Methods

    public String getFACTYPE() {
        return FACTYPE;
    }

    public String getHazardRating() {
        return HazardRating;
    }

    public String getInspType() {
        return InspType;
    }

    public String getInspectionDate() {
        return InspectionDate;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public String getNAME() {
        return NAME;
    }

    public int getNumCritical() {
        return NumCritical;
    }

    public int getNumNonCritical() {
        return NumNonCritical;
    }

    public String getPHYSICALADDRESS() {
        return PHYSICALADDRESS;
    }

    public String getPHYSICALCITY() {
        return PHYSICALCITY;
    }

    public String getTrackingNumber() {
        return TrackingNumber;
    }

    public String getViolLump() {
        return ViolLump;
    }

    // Setter Methods

    public void setFACTYPE(String FACTYPE) {
        this.FACTYPE = FACTYPE;
    }

    public void setHazardRating(String HazardRating) {
        this.HazardRating = HazardRating;
    }

    public void setInspType(String InspType) {
        this.InspType = InspType;
    }

    public void setInspectionDate(String InspectionDate) {
        this.InspectionDate = InspectionDate;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setNumCritical(int NumCritical) {
        this.NumCritical = NumCritical;
    }

    public void setNumNonCritical(int NumNonCritical) {
        this.NumNonCritical = NumNonCritical;
    }

    public void setPHYSICALADDRESS(String PHYSICALADDRESS) {
        this.PHYSICALADDRESS = PHYSICALADDRESS;
    }

    public void setPHYSICALCITY(String PHYSICALCITY) {
        this.PHYSICALCITY = PHYSICALCITY;
    }

    public void setTrackingNumber(String TrackingNumber) {
        this.TrackingNumber = TrackingNumber;
    }

    public void setViolLump(String ViolLump) {
        this.ViolLump = ViolLump;
    }

    public boolean is_favorite() { return _favorite; }
}