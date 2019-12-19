package ca.bcit.comp3717project;

import java.util.List;

public class Records
{
    private String NumCritical;

    private String HazardRating;

    private String NumNonCritical;

    private String InspectionDate;

    private String InspType;

    private String ViolLump;

    private String TrackingNumber;

    private int _id;

    public void setNumCritical(String NumCritical){
        this.NumCritical = NumCritical;
    }
    public String getNumCritical(){
        return this.NumCritical;
    }
    public void setHazardRating(String HazardRating){
        this.HazardRating = HazardRating;
    }
    public String getHazardRating(){
        return this.HazardRating;
    }
    public void setNumNonCritical(String NumNonCritical){
        this.NumNonCritical = NumNonCritical;
    }
    public String getNumNonCritical(){
        return this.NumNonCritical;
    }
    public void setInspectionDate(String InspectionDate){
        this.InspectionDate = InspectionDate;
    }
    public String getInspectionDate(){
        return this.InspectionDate;
    }
    public void setInspType(String InspType){
        this.InspType = InspType;
    }
    public String getInspType(){
        return this.InspType;
    }
    public void setViolLump(String ViolLump){
        this.ViolLump = ViolLump;
    }
    public String getViolLump(){
        return this.ViolLump;
    }
    public void setTrackingNumber(String TrackingNumber){
        this.TrackingNumber = TrackingNumber;
    }
    public String getTrackingNumber(){
        return this.TrackingNumber;
    }
    public void set_id(int _id){
        this._id = _id;
    }
    public int get_id(){
        return this._id;
    }
}