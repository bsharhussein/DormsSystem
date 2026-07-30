package dorms;

public interface Payable {
    void pay();
    double calculateAmount();
    boolean isOverdue();
}