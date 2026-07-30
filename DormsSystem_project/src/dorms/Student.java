package dorms;
import java.util.Stack;

public class Student extends Person {

    private static final long serialVersionUID = 1L;
    
    private String fieldOfStudy;   
    private int studyYear;         
    private Room assignedRoom;     
    private Stack<Payment> paymentHistory;

    public Student(String id, String fullName, String phoneNumber, String fieldOfStudy, int studyYear) {
        // Pass the core identity details up to the Person parent class
        super(id, fullName, phoneNumber); 

        this.fieldOfStudy = fieldOfStudy; 
        this.studyYear = studyYear;
        this.assignedRoom = null;          
        this.paymentHistory = new Stack<>(); 
    }

    // --- Getters & Setters ---

    public String getFieldOfStudy() { return fieldOfStudy; } 
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; } 

    public int getStudyYear() { return studyYear; } 
    public void setStudyYear(int studyYear) { this.studyYear = studyYear; } 

    public Room getAssignedRoom() { return assignedRoom; } 
    public void setAssignedRoom(Room assignedRoom) { this.assignedRoom = assignedRoom; } 

    public Stack<Payment> getPaymentHistory() { return this.paymentHistory; }
    
    // --- Behavioral Logic ---

    public void addPayment(Payment payment) {
        paymentHistory.push(payment);
        System.out.println("Payment added to history for " + getFullName());
    }

    public void viewPaymentHistory() {
        System.out.println("Payment History for " + getFullName());

        if (paymentHistory.isEmpty()) {
            System.out.println("No payments found.");
        } else {
            for (Payment p : paymentHistory) {
                System.out.println(p.toString());
            }
        }
        System.out.println("-----------------------------------");
    }

    public boolean requestRoomChange(Room newRoom) {
        if (newRoom == null) {
            System.out.println("Invalid room.");
            return false; 
        }

        if (!newRoom.isAvailable()) {
            System.out.println("The requested room is full, cannot change.");
            return false; 
        }
        
        // Link the student to the new room, and the new room to the student
        this.assignedRoom = newRoom;   
        newRoom.assignStudent(this);   
        
        System.out.println("Student " + getFullName() + " successfully moved to room " + newRoom.getRoomNumber());
        return true; 
    }

    // --- Overrides ---

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        // Prevents a crash if the student is on the waitlist (room is null)
        String roomInfo = (assignedRoom != null) ? "Room " + assignedRoom.getRoomNumber() : "Unassigned";
        
        return "Student{" +
                "id='" + getId() + '\'' +                  
                ", fullName='" + getFullName() + '\'' +   
                ", fieldOfStudy='" + fieldOfStudy + '\'' + 
                ", studyYear=" + studyYear +               
                ", assignedRoom=" + roomInfo +             
                ", paymentCount=" + paymentHistory.size() + 
                '}';
    }
}