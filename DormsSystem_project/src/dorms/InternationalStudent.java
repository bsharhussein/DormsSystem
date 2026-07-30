package dorms;

public class InternationalStudent extends Student {
    
    private static final long serialVersionUID = 1L;
    
    private String passportNumber;
    private String countryOfOrigin;
    private String visaExpirationDate;

    public InternationalStudent(String id, String fullName, String phoneNumber, String fieldOfStudy, int studyYear, 
                                String passportNumber, String countryOfOrigin, String visaExpirationDate) {
        
        super(id, fullName, phoneNumber, fieldOfStudy, studyYear);
        
        this.passportNumber = passportNumber;
        this.countryOfOrigin = countryOfOrigin;
        this.visaExpirationDate = visaExpirationDate;
    }

    // --- Getters & Setters ---

    public String getPassportNumber() { return passportNumber; } 
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; } 

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getVisaExpirationDate() { return visaExpirationDate; }
    public void setVisaExpirationDate(String visaExpirationDate) { this.visaExpirationDate = visaExpirationDate; }

    // --- Behavioral Logic ---

    public void checkVisaStatus() {
        System.out.println("SYSTEM ALERT for " + getFullName() + ":");
        System.out.println("Visa (Passport: " + passportNumber + ") expires on " + visaExpirationDate + ".");
        System.out.println("Please ensure documentation is renewed before this date.");
    }

    // --- Overrides ---

    @Override
    public String getRole() {
        return "International Student";
    }

    @Override
    public String toString() {
        // We now include the base Student fields so you don't lose that data when printing
        return "InternationalStudent{" + 
               "id='" + getId() + '\'' +  
               ", fullName='" + getFullName() + '\'' + 
               ", fieldOfStudy='" + getFieldOfStudy() + '\'' + 
               ", studyYear=" + getStudyYear() +
               ", passport='" + passportNumber + '\'' + 
               ", country='" + countryOfOrigin + '\'' + 
               ", visa='" + visaExpirationDate + '\'' + 
               '}';
    }
}