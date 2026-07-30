package dorms;

public class StaffMember extends Person {

    private static final long serialVersionUID = 1L;

    private String jobTitle; 
    
    // Tracks if the worker is free or currently assigned to a maintenance ticket
    private boolean isAvailable = true;

    public StaffMember(String id, String fullName, String phoneNumber, String jobTitle) {
        // Pass identity details up to the Person parent class
        super(id, fullName, phoneNumber);
        this.jobTitle = jobTitle;
    }

    // --- Getters & Setters ---

    public String getJobTitle() { return jobTitle; } 
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; } 

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    // --- Behavioral Logic ---

    public void printWorkerStatus() {
        System.out.println("STAFF STATUS REPORT");
        System.out.println("Worker: " + getFullName() + " (" + this.jobTitle + ")");
        
        if (this.isAvailable) {
            System.out.println("Status: Waiting for dispatch.");
        } else {
            System.out.println("Status: Currently on a job!");
        }
        System.out.println("---------------------------");
    }

    // --- Overrides ---

    @Override
    public String getRole() {
        return "Staff: " + jobTitle;
    }

    @Override
    public String toString() {
        return "StaffMember{" + 
               "id='" + getId() + '\'' + 
               ", fullName='" + getFullName() + '\'' + 
               ", jobTitle='" + jobTitle + '\'' + 
               ", isAvailable=" + isAvailable + 
               '}';
    }
}