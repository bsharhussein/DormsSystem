package dorms;

import java.io.Serializable;

public class Room implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int roomNumber;        
    private String building;      
    private int capacity;          // Total number of beds
    private int currentOccupancy;  // How many beds are currently filled
    private double monthlyPrice;   // Rent per month

    // Array representing the specific "beds" in the room
    private Student[] assignedStudents; 

    public Room(int roomNumber, String building, int capacity, double monthlyPrice) {
        this.roomNumber = roomNumber;
        this.building = building;
        this.capacity = capacity;
        
        // Defensive check: rent cannot be negative
        if (monthlyPrice < 0) {
            System.out.println("System Error: Price cannot be negative. Defaulting to 0.");
            this.monthlyPrice = 0;
        } else {
            this.monthlyPrice = monthlyPrice;
        }

        this.currentOccupancy = 0;
        this.assignedStudents = new Student[capacity]; // Instantiate the empty beds
    }

    // --- Getters & Setters ---

    public int getRoomNumber() { return roomNumber; } 
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; } 

    public String getBuilding() { return building; } 
    public void setBuilding(String building) { this.building = building; } 

    public int getCapacity() { return capacity; } 
    public void setCapacity(int capacity) { this.capacity = capacity; } 

    public int getCurrentOccupancy() { return currentOccupancy; } 
    
    public double getMonthlyPrice() { return monthlyPrice; } 
    
    public void setMonthlyPrice(double monthlyPrice) { 
        if (monthlyPrice >= 0) {
            this.monthlyPrice = monthlyPrice; 
        } else {
            System.out.println("System Error: Room price cannot be negative.");
        }
    } 

    public Student[] getAssignedStudents() { return this.assignedStudents; }

    // --- Behavioral Logic ---

    public boolean isAvailable() {
        // True if there is at least one empty bed
        return currentOccupancy < capacity;   
    }

    public void assignStudent(Student newStudent) {
        // Walk through every bed slot to find an empty one
        for (int i = 0; i < assignedStudents.length; i++) {
            if (assignedStudents[i] == null) {
                assignedStudents[i] = newStudent; 
                currentOccupancy++;               
                return; // Stop immediately so we don't fill multiple beds with the same person
            }
        }
        System.out.println("System Error: Room is at maximum capacity.");
    }
    
    public boolean checkoutStudent(String studentId) {
        for (int i = 0; i < assignedStudents.length; i++) {
            // Find the specific student by ID
            if (assignedStudents[i] != null && assignedStudents[i].getId().equals(studentId)) { 
                Student leavingStudent = assignedStudents[i];
                assignedStudents[i] = null; // Empty the bed 
                this.currentOccupancy--;    // Reduce occupancy counter
                
                System.out.println("System: " + leavingStudent.getFullName() + " has been officially checked out of Room " + this.roomNumber);
                return true; 
            }
        }
        return false; // Student ID was not found in this room
    }

    public void printResidents() {
        System.out.println("Residents in Room " + roomNumber + ":");
        boolean isEmpty = true;
        
        for (int i = 0; i < assignedStudents.length; i++) {
            if (assignedStudents[i] != null) {
                Student s = assignedStudents[i]; 
                System.out.println(" Bed " + (i + 1) + ": " + s.getFullName() + " | ID: " + s.getId() + " | Phone: " + s.getFormattedPhone());
                isEmpty = false; 
            }
        }
        if (isEmpty) {
            System.out.println("Room is currently empty.");
        }
    }

    // --- Overrides ---
   
    @Override 
    public String toString() {
        return "Room{" + 
               "roomNumber=" + roomNumber + 
               ", building='" + building + '\'' + 
               ", capacity=" + capacity + 
               ", currentOccupancy=" + currentOccupancy + 
               ", monthlyPrice=" + monthlyPrice + 
               '}';
    }
}