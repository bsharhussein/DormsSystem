package dorms;
import java.io.Serializable;

public class Payment implements Payable, Serializable {

    private static final long serialVersionUID = 1L;
    
    private int paymentId;      
    private double amount;      
    private String paymentDate; 
    private String status;      
    private Student payingStudent;

    public Payment(int paymentId, double amount, String paymentDate, String status, Student payingStudent) {
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.payingStudent = payingStudent;
        
        // Enforce the rules right when the object is created
        setAmount(amount);
        setStatus(status);
    }

    // --- Getters & Setters ---

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public double getAmount() { return amount; }
    
    public void setAmount(double amount) {
        // Make sure we never accidentally charge a negative amount
        if (amount < 0) {
            throw new IllegalArgumentException("Error: Payment amount cannot be negative.");
        }
        this.amount = amount;
    }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public Student getPayingStudent() { return payingStudent; }
    public void setPayingStudent(Student payingStudent) { this.payingStudent = payingStudent; }

    public String getStatus() { return status; }
    
    public void setStatus(String status) {
        // Only allow valid words so the automated billing logic doesn't break
        if (status.equals("Pending") || status.equals("Paid") || status.equals("Late")) {
            this.status = status;
        } else {
            System.out.println("System Error: Invalid payment status. Defaulting to 'Pending'.");
            this.status = "Pending";
        }
    }

    // --- Behavioral Logic ---

    public void calculateLateFee() {
        // Add a flat 50-shekel penalty if they missed the deadline
        if (this.status.equals("Late")) {
            this.amount += 50.0; 
            System.out.println("Late fee applied. New amount due: $" + this.amount);
        }
    }

    // --- Payable Interface Methods ---

    @Override
    public void pay() {
        setStatus("Paid");
        System.out.println("Payment ID " + paymentId + " has successfully been marked as Paid.");
    }

    @Override
    public double calculateAmount() {
        return this.amount;
    }

    @Override
    public boolean isOverdue() {
        return this.status.equals("Late");
    }

    // --- Overrides ---

    @Override
    public String toString() {
        String studentName = (payingStudent != null) ? payingStudent.getFullName() : "Unknown Student";
        
        return "Payment{" + 
               "paymentId=" + paymentId + 
               ", amount=" + amount + 
               ", paymentDate='" + paymentDate + '\'' + 
               ", status='" + status + '\'' + 
               ", payingStudent=" + studentName + 
               '}';
    }
}
