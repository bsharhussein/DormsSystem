package dorms;
import java.io.Serializable;

public abstract class Person implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String fullName;
    private String phoneNumber;
    
    public Person(String id, String fullName, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // --- Getters & Setters ---

    public String getId() { return id; } 
    public void setId(String id) { this.id = id; } 

    public String getFullName() { return fullName; } 
    public void setFullName(String fullName) { this.fullName = fullName; } 

    public String getPhoneNumber() { return phoneNumber; } 
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; } 

    // --- Behavioral Logic ---

    public String getFormattedPhone() {
        // Formats an Israeli mobile number: 0541234567 -> 054-1234567
        if (this.phoneNumber != null && this.phoneNumber.length() == 10 && this.phoneNumber.startsWith("05")) {
            return this.phoneNumber.substring(0, 3) + "-" + this.phoneNumber.substring(3);
        }
        // If the number is invalid, return it unmodified
        return this.phoneNumber; 
    }

    // Abstract method: Forces all subclasses (Student, Staff) to define their own role
    public abstract String getRole();

    // --- Overrides ---

    @Override
    public String toString() {
        return "Person{" +
               "id='" + id + '\'' +
               ", fullName='" + fullName + '\'' +
               ", phone='" + getFormattedPhone() + '\'' + 
               '}';
    }
}