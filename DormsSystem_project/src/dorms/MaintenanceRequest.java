package dorms;
import java.io.Serializable;

public class MaintenanceRequest implements Payable, Serializable {

    private static final long serialVersionUID = 1L;

    private int requestId;      
    private String description; 
    private String priority;    // "Low", "Medium", or "Urgent"
    private String status;      // "Open", "In Progress", or "Closed"
    private StaffMember assignedWorker; 
    private Student submittedBy;  
    private Room relatedRoom;     

    public MaintenanceRequest(int requestId, String description, String priority, String status, Student submittedBy, Room relatedRoom) {
        this.requestId = requestId;
        this.description = description;
        this.submittedBy = submittedBy;
        this.relatedRoom = relatedRoom;
        this.assignedWorker = null; 
        
        setPriority(priority);
        setStatus(status);
    }

    // --- Getters & Setters ---

    public int getRequestId() { return requestId; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public Student getSubmittedBy() { return submittedBy; }
    public Room getRelatedRoom() { return relatedRoom; }
    public StaffMember getAssignedWorker() { return this.assignedWorker; }

    public void setRequestId(int requestId) { this.requestId = requestId; }
    public void setDescription(String description) { this.description = description; }
    public void setSubmittedBy(Student submittedBy) { this.submittedBy = submittedBy; }
    public void setRelatedRoom(Room relatedRoom) { this.relatedRoom = relatedRoom; }
    public void setAssignedWorker(StaffMember worker) { this.assignedWorker = worker; }

    // --- Behavioral Logic ---

    public void setPriority(String priority) {
        if (priority.equals("Low") || priority.equals("Medium") || priority.equals("Urgent")) {
            this.priority = priority;
        } else {
            System.out.println("System Error: Invalid priority. Defaulting to 'Low'.");
            this.priority = "Low";
        }
    }

    public void setStatus(String status) {
        // Data gatekeeper to ensure valid status
        if (status.equals("Open") || status.equals("In Progress") || status.equals("Closed")) {
            this.status = status;
        } else {
            System.out.println("System Error: Invalid status.");
        }
    }

    public void updateStatus(String newStatus) {
        setStatus(newStatus); 
        System.out.println("Maintenance Request ID " + requestId + " status updated to: " + this.status);
    }

    public void escalate() {
        if (this.priority.equals("Low")) {
            this.priority = "Medium";
            System.out.println("Request ID " + requestId + " escalated to Medium priority.");
        } else if (this.priority.equals("Medium")) {
            this.priority = "Urgent";
            System.out.println("Request ID " + requestId + " escalated to Urgent priority.");
        } else if (this.priority.equals("Urgent")) {
            System.out.println("Request ID " + requestId + " is already at maximum (Urgent) priority.");
        }
    }

    // --- Payable Interface Methods ---

    @Override
    public void pay() {
        System.out.println("The fee of $" + calculateAmount() + " for Maintenance Request ID " + requestId + " has been paid.");
    }

    @Override
    public double calculateAmount() {
        if (this.priority.equals("Urgent")) {
            return 100.0;
        } else if (this.priority.equals("Medium")) {
            return 50.0;
        } else {
            return 25.0; 
        }
    }

    @Override
    public boolean isOverdue() {
        return this.priority.equals("Urgent") && this.status.equals("Open");
    }

    // --- Overrides ---

    @Override
    public String toString() {
        String studentName = (submittedBy != null) ? submittedBy.getFullName() : "Management";
        String roomNumber = (relatedRoom != null) ? String.valueOf(relatedRoom.getRoomNumber()) : "Campus Grounds";

        return "MaintenanceRequest{" + "requestId=" + requestId + 
               ", description='" + description + '\'' + 
               ", priority='" + priority + '\'' + 
               ", status='" + status + '\'' + 
               ", submittedBy=" + studentName + 
               ", relatedRoom=" + roomNumber + '}';
    }
}