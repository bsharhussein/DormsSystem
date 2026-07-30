/*
 * Final Project: DormsSystem
 * Course: Advanced Topics in Object-Oriented Programming and JAVA
 * Submitted by:
 * 1. Rayan Suan - ID: 215733601
 * 2. Bashar Hussein - ID: 324220326
 */

package dorms;

import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Scanner; 
import java.util.TreeMap; 

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;

/**
 * Main execution class for the Dorms System.
 * Acts as the central hub connecting all models, managing the UI loop, 
 * and handling data persistence (Save/Load).
 */
public class Main {

	// TreeMap is used here to automatically keep rooms sorted numerically (101, 102, 201...)
	static TreeMap<Integer, Room> dormRooms = new TreeMap<>();
	
	// LinkedLists used for dynamic lists where elements are constantly added/removed
	static LinkedList<Student> studentWaitlist = new LinkedList<>();
	static LinkedList<MaintenanceRequest> activeTickets = new LinkedList<>();
	static LinkedList<StaffMember> staffList = new LinkedList<>();
	
	// Queue enforces FIFO (First-In-First-Out) for fairness in maintenance dispatch
	static Queue<MaintenanceRequest> maintenanceQueue = new LinkedList<>();

	public static void main(String[] args) {

		// Restore saved data from disk, or build starter data if first run
		loadSystemData();

		Scanner scanner = new Scanner(System.in); 
		boolean running = true; 
		int requestCounter = 500; // Starting point for unique maintenance ticket IDs

		System.out.println("=========================================");
		System.out.println("       Welcome to the Dorms System       ");
		System.out.println("=========================================");

		// Main UI Loop
		while (running) {
			System.out.println("\n--- MAIN MENU ---");
			System.out.println("=========================================");
			System.out.println("1. Register New Student & Assign Room");
			System.out.println("2. Room Inspection");
			System.out.println("3. Manage Waitlist (View & Assign)");
			System.out.println("4. Maintenance Dispatch (Queue)");
			System.out.println("5. Manage Staff Members");
			System.out.println("6. System Reports (Financials & Maps)");
			System.out.println("7. Advance to Next Month (Automated Billing & Late Fees)");
			System.out.println("=========================================");
			System.out.println("---STUDENT ACTIONS---");
			System.out.println("8. Smart Student Search");
			System.out.println("9. Transfer Student to New Room");
			System.out.println("10. Checkout / Evict Student");
			System.out.println("11. Process Student Payment");
			System.out.println("=========================================");
			System.out.println("---SYSTEM ADMINISTRATION---");
			System.out.println("12. Add New Dorm Room");
			System.out.println("13. Update Room Pricing");
			System.out.println("14. Exit System");
			System.out.println("=========================================");
			System.out.print("Choose an option (1-14): ");

			String choice = scanner.nextLine().trim(); 

			switch (choice) {

			// CASE 1: Register New Student & Assign Room
			case "1":
				System.out.println("\n[ 1. STUDENT REGISTRATION ]");
				String id = "";
				while (true) {
					id = InputValidator.getValidId(scanner);
					if (isIdTaken(id)) {
						System.out.println("System Alert: The ID " + id + " is already registered! Please try again.");
					} else {
						break;
					}
				}
				String name = InputValidator.getValidName(scanner, "Enter Full Name: ");

				String phone = "";
				while (true) {
					phone = InputValidator.getValidPhoneNumber(scanner, "Enter Phone Number");
					if (isPhoneTaken(phone)) {
						System.out.println("System Alert: The phone number " + phone + " is already in use! Please try again.");
					} else {
						break;
					}
				}

				String fieldOfStudy = InputValidator.getValidName(scanner, "Enter Field of Study (e.g., Computer Science, Engineering): ");
				int studyYear = InputValidator.getValidNumberInRange(scanner, "Enter Study Year (1-4)", 1, 4);
				String isInt = InputValidator.getValidYesNo(scanner, "Is this an International Student?");

				// Polymorphism: Declare as Student, instantiate as Student OR InternationalStudent
				Student newStudent;
				if (isInt.equals("yes")) {
					String passport = InputValidator.getValidPassport(scanner, "Enter Passport Number");
					String country = InputValidator.getValidLettersOnly(scanner, "Enter Country of Origin");
					String visa = InputValidator.getValidFutureDate(scanner, "Enter Visa Expiration Date");
					newStudent = new InternationalStudent(id, name, phone, fieldOfStudy, studyYear, passport, country, visa);
				} else {
					newStudent = new Student(id, name, phone, fieldOfStudy, studyYear);
				}

				boolean validRoomEntered = false;
				while (!validRoomEntered) {
					int requestedRoomNum = InputValidator.getValidNumber(scanner, "Enter Room Number to assign (or type '0' for waitlist): ");

					if (requestedRoomNum == 0) {
						System.out.println("System: Placing " + name + " on the waitlist.");
						studentWaitlist.add(newStudent);
						validRoomEntered = true;
					} else {
						Room requestedRoom = dormRooms.get(requestedRoomNum);
						if (requestedRoom != null) {
							if (requestedRoom.isAvailable()) { 

								// Bi-directional link between room and student
								requestedRoom.assignStudent(newStudent);
								newStudent.setAssignedRoom(requestedRoom);

								System.out.println("System: Successfully assigned to Room " + requestedRoomNum);

								String payNow = InputValidator.getValidYesNo(scanner, "Would the student like to pay their first month's rent right now?");
								String paymentStatus;
								if (payNow.equals("yes")) {
									paymentStatus = "Paid";
									System.out.println("System: Initial payment of $" + requestedRoom.getMonthlyPrice() + " processed successfully.");
								} else {
									paymentStatus = "Pending";
									System.out.println("System: First month's rent added to account as Pending.");
								}

								int firstPaymentId = 8000 + (int) (Math.random() * 1000);
								Payment initialRent = new Payment(firstPaymentId, requestedRoom.getMonthlyPrice(), "01/06/2026", paymentStatus, newStudent);
								newStudent.addPayment(initialRent);

								validRoomEntered = true; 
							} else {
								System.out.println("System: Room " + requestedRoomNum + " is full! Choose another room or type 0.");
							}
						} else {
							System.out.println("Error: Room " + requestedRoomNum + " does not exist. Try again.");
						}
					}
				}
				break;

			// CASE 2: Room Inspection
			case "2":
				boolean inspecting = true;
				while (inspecting) {
					System.out.println("\n[ 2. ROOM INSPECTION ]");
					int searchRoom = InputValidator.getValidNumber(scanner, "Enter Room Number to inspect (or '0' to return to menu): ");

					if (searchRoom == 0) { 
						inspecting = false;
						break;
					}

					Room foundRoom = dormRooms.get(searchRoom);
					if (foundRoom != null) {
						System.out.println("\n--- Room " + searchRoom + " Details ---");
						System.out.println(foundRoom.toString()); 
						foundRoom.printResidents(); 
					} else {
						System.out.println("System Error: Room " + searchRoom + " was not found in the database.");
					}
				}
				break;

			// CASE 3: Manage Waitlist (View & Assign)
			case "3":
				boolean managingWaitlist = true;
				while (managingWaitlist) {
					System.out.println("\n[ 3. WAITLIST MANAGEMENT ]");
					if (studentWaitlist.isEmpty()) {
						System.out.println("System: The waitlist is currently empty.");
						managingWaitlist = false;
						break;
					}

					System.out.println("Total students waiting: " + studentWaitlist.size());
					for (int i = 0; i < studentWaitlist.size(); i++) {
						Student ws = studentWaitlist.get(i);
						System.out.println((i + 1) + ". " + ws.getFullName() + " | ID: " + ws.getId() + " | Phone: " + ws.getFormattedPhone());
					}

					String waitlistAction = InputValidator.getValidYesNo(scanner, "\nDo you want to assign a waitlisted student to a room?");

					if (waitlistAction.equals("yes")) {
						int studentIndex = InputValidator.getValidNumberInRange(scanner, "Enter the list number of the student (1-" + studentWaitlist.size() + ")", 1, studentWaitlist.size());
						Student selectedStudent = studentWaitlist.get(studentIndex - 1);

						int assignRoomNum = InputValidator.getValidNumber(scanner, "Enter Room Number to assign " + selectedStudent.getFullName() + " to (or type '0' to cancel): ");

						if (assignRoomNum == 0) {
							continue; 
						}

						Room assignRoom = dormRooms.get(assignRoomNum);
						if (assignRoom != null) {
							if (assignRoom.isAvailable()) {

								assignRoom.assignStudent(selectedStudent);
								selectedStudent.setAssignedRoom(assignRoom);

								// Remove from waitlist since they now have a bed
								studentWaitlist.remove(selectedStudent);

								System.out.println("System: Success! " + selectedStudent.getFullName() + " has been moved from the waitlist to Room " + assignRoomNum);

								String payNow = InputValidator.getValidYesNo(scanner, "Would the student like to pay their first month's rent right now?");
								String paymentStatus = payNow.equals("yes") ? "Paid" : "Pending";

								int firstPaymentId = 8000 + (int) (Math.random() * 1000);
								Payment initialRent = new Payment(firstPaymentId, assignRoom.getMonthlyPrice(), "01/06/2026", paymentStatus, selectedStudent);
								selectedStudent.addPayment(initialRent);

								if (payNow.equals("yes")) {
									System.out.println("System: Initial payment of $" + assignRoom.getMonthlyPrice() + " processed successfully.");
								} else {
									System.out.println("System: First month's rent added to account as Pending.");
								}

							} else {
								System.out.println("System Alert: Room " + assignRoomNum + " is currently full!");
							}
						} else {
							System.out.println("System Error: Room " + assignRoomNum + " does not exist.");
						}
					} else {
						System.out.println("System: Exiting waitlist manager.");
						managingWaitlist = false;
					}
				}
				break;

			// CASE 4: Maintenance Dispatch Board
			case "4":
				boolean managingMaintenance = true;

				while (managingMaintenance) {
					System.out.println("\n[ 4. MAINTENANCE DISPATCH BOARD ]");
					System.out.println("Issues waiting in Queue: " + maintenanceQueue.size() + " | Active Jobs: " + activeTickets.size());

					System.out.println("\nOptions:");
					System.out.println("A. Report a NEW issue");
					System.out.println("B. Dispatch worker to next waiting issue");
					System.out.println("C. Mark an active job as FIXED");
					System.out.println("D. Return to Main Menu");

					String[] maintOptions = { "A", "B", "C", "D" };
					String maintChoice = InputValidator.getValidChoice(scanner, "Choose an option", maintOptions);

					if (maintChoice.equals("A")) {
						// --- CREATE NEW TICKET ---
						int roomIssue = InputValidator.getValidNumber(scanner, "Enter Room Number with the issue: ");
						Room issueRoom = dormRooms.get(roomIssue);

						if (issueRoom != null) {
							Student reportingStudent = null;
							boolean hasResidents = false;
							Student[] residents = issueRoom.getAssignedStudents();

							for (Student s : residents) {
								if (s != null) hasResidents = true;
							}

							if (hasResidents) {
								String linkChoice = InputValidator.getValidYesNo(scanner, "Link a specific student in this room to the ticket?");
								if (linkChoice.equals("yes")) {
									System.out.println("Select the student who reported this:");
									for (int i = 0; i < residents.length; i++) {
										if (residents[i] != null) {
											System.out.println((i + 1) + ". " + residents[i].getFullName());
										}
									}
									while (reportingStudent == null) {
										int bedChoice = InputValidator.getValidNumberInRange(scanner, "Choose student by bed number:", 1, residents.length);
										reportingStudent = residents[bedChoice - 1];
										if (reportingStudent == null) {
											System.out.println("Error: That bed is currently empty. Please choose a valid student.");
										}
									}
								}
							} else {
								System.out.println("System Note: Room is empty. Ticket will be submitted by Management.");
							}

							System.out.println("\nSelect Issue Category:");
							System.out.println("1. Plumbing (Leaks, Clogs)");
							System.out.println("2. Electrical (Outlets, Lights)");
							System.out.println("3. Other (Furniture, General Repairs)");

							int issueType = InputValidator.getValidNumberInRange(scanner, "Choose (1-3):", 1, 3);
							String description = (issueType == 1) ? "[Plumber Required] Plumbing Issue" : 
												 (issueType == 2) ? "[Electrician Required] Electrical Issue" : 
												 "[Handyman Required] " + InputValidator.getValidName(scanner, "Briefly describe the issue: ");

							System.out.println("\nSelect Priority Level:\n1. Low\n2. Medium\n3. Urgent");
							int prioChoice = InputValidator.getValidNumberInRange(scanner, "Choose (1-3):", 1, 3);
							String priority = (prioChoice == 1) ? "Low" : (prioChoice == 2) ? "Medium" : "Urgent";

							requestCounter++;
							MaintenanceRequest newReq = new MaintenanceRequest(requestCounter, description, priority, "Open", reportingStudent, issueRoom);
							maintenanceQueue.add(newReq);
							System.out.println("System: Issue added to the Maintenance Queue with " + priority + " priority.");
						} else {
							System.out.println("System: Room not found.");
						}

					} else if (maintChoice.equals("B")) {
						// --- DISPATCH WORKER & POLL QUEUE ---
						if (maintenanceQueue.isEmpty()) {
							System.out.println("System: The queue is empty! No issues to dispatch.");
						} else {
							MaintenanceRequest nextIssue = maintenanceQueue.peek(); 
							
							System.out.println("\n[ NEXT TICKET IN LINE ]");
							System.out.println(nextIssue.getDescription() + " | Room: " + nextIssue.getRelatedRoom().getRoomNumber());

							String requiredSkill = "handyman";
							if (nextIssue.getDescription().contains("[Plumber Required]")) requiredSkill = "plumber";
							if (nextIssue.getDescription().contains("[Electrician Required]")) requiredSkill = "electrician";

							StaffMember assignedWorker = null;
							for (StaffMember staff : staffList) {
								if (staff.getJobTitle().toLowerCase().contains(requiredSkill) && staff.isAvailable()) {
									assignedWorker = staff;
									break; 
								}
							}

							if (assignedWorker != null) {
								System.out.println("System: " + assignedWorker.getFullName() + " (" + assignedWorker.getJobTitle() + ") has been dispatched!");
								
								assignedWorker.setAvailable(false); 
								nextIssue.updateStatus("In Progress"); 
								nextIssue.setAssignedWorker(assignedWorker);
								nextIssue.pay(); 

								// Poll removes it from the waiting line and pushes it to active work
								maintenanceQueue.poll();
								activeTickets.add(nextIssue);
							} else {
								System.out.println("System Error: You don't have a free '" + requiredSkill + "' to take this job. Hire one or wait until they finish their active job!");
							}
						}

					} else if (maintChoice.equals("C")) {
						// --- MARK JOB FIXED ---
						if (activeTickets.isEmpty()) {
							System.out.println("System: There are no jobs currently in progress.");
						} else {
							System.out.println("\n--- ACTIVE JOBS ---");
							for (int i = 0; i < activeTickets.size(); i++) {
								MaintenanceRequest active = activeTickets.get(i);
								System.out.println((i + 1) + ". " + active.getDescription() + " | Room " + active.getRelatedRoom().getRoomNumber() + " | Worker: " + active.getAssignedWorker().getFullName());
							}
							
							int fixIndex = InputValidator.getValidNumberInRange(scanner, "Select the job that is finished (or 0 to cancel)", 0, activeTickets.size());
							
							if (fixIndex != 0) {
								MaintenanceRequest finishedJob = activeTickets.remove(fixIndex - 1);
								finishedJob.getAssignedWorker().setAvailable(true); // Free the worker
								finishedJob.updateStatus("Closed");
								System.out.println("System: Success! Ticket marked as FIXED and " + finishedJob.getAssignedWorker().getFullName() + " is now free for a new job.");
							}
						}

					} else if (maintChoice.equals("D")) {
						System.out.println("System: Exiting dispatch board.");
						managingMaintenance = false;
					}
				}
				break;

			// CASE 5: Manage Staff Members
			case "5":
				boolean managingStaff = true;

				while (managingStaff) {
					System.out.println("\n[ 5. STAFF MANAGEMENT ]");
					if (staffList.isEmpty()) {
						System.out.println("System: There are currently no staff members on the payroll.");
					} else {
						for (StaffMember staff : staffList) {
							staff.printWorkerStatus();
						}
					}

					System.out.println("\nStaff Options:");
					System.out.println("A. Hire a new worker");
					System.out.println("B. Fire an existing worker");
					System.out.println("C. Return to Main Menu");

					String[] staffOptions = { "A", "B", "C" };
					String staffChoice = InputValidator.getValidChoice(scanner, "Choose an option", staffOptions);

					if (staffChoice.equals("A")) {
						String staffId = "";
						while (true) {
							staffId = InputValidator.getValidId(scanner);
							if (isIdTaken(staffId)) {
								System.out.println("System Alert: The ID " + staffId + " is already registered! Cannot hire duplicate.");
							} else {
								break;
							}
						}

						String staffName = InputValidator.getValidName(scanner, "Enter Full Name: ");

						String staffPhone = "";
						while (true) {
							staffPhone = InputValidator.getValidPhoneNumber(scanner, "Enter Phone Number");
							if (isPhoneTaken(staffPhone)) {
								System.out.println("System Alert: The phone number " + staffPhone + " is already in use! Try again.");
							} else {
								break;
							}
						}

						String jobTitle = InputValidator.getValidName(scanner, "Enter Job Title (e.g., Plumber, Handyman): ");

						staffList.add(new StaffMember(staffId, staffName, staffPhone, jobTitle));
						System.out.println("System: Success! " + staffName + " has been hired as a " + jobTitle + ".");

					} else if (staffChoice.equals("B")) {
						if (staffList.isEmpty()) {
							System.out.println("System Error: No staff members available to terminate.");
						} else {
							System.out.println("\n[ TERMINATION PROTOCOL ]");
							String fireId = InputValidator.getValidId(scanner);
							boolean workerFound = false;

							for (int i = 0; i < staffList.size(); i++) {
								if (staffList.get(i).getId().equals(fireId)) {
									if (!staffList.get(i).isAvailable()) {
										System.out.println("System Error: You cannot fire this worker, they are currently fixing a ticket!");
										workerFound = true;
										break;
									}
									StaffMember firedWorker = staffList.remove(i);
									System.out.println("System: " + firedWorker.getFullName() + " (" + firedWorker.getJobTitle() + ") has been terminated.");
									workerFound = true;
									break;
								}
							}

							if (!workerFound) {
								System.out.println("System Error: No staff member found with ID " + fireId);
							}
						}
					} else if (staffChoice.equals("C")) {
						System.out.println("System: Returning to Main Menu.");
						managingStaff = false;
					}
				}
				break;

			// CASE 6: System Reports (Financials & Maps)
			case "6":
				System.out.println("\n[ 6. SYSTEM REPORTS ]");
				System.out.println("A. View Building Map (Dynamic Floorplan)");
				System.out.println("B. Financial Report (Student Payments)");

				String[] finalReportOptions = { "A", "B" };
				String finalReportChoice = InputValidator.getValidChoice(scanner, "Choose a report", finalReportOptions);

				if (finalReportChoice.equals("A")) {
					System.out.println("\n--- DORMS FLOOR MAP ---");
					System.out.println("Legend: [ Room Number (Current Students / Maximum Beds) ]\n");

					// 2D Array implementation for visual grid reporting
					String[][] visualGrid = new String[5][4];

					for (int row = 0; row < visualGrid.length; row++) {
						for (int col = 0; col < visualGrid[row].length; col++) {
							visualGrid[row][col] = "[   Empty   ]";
						}
					}

					for (Room r : dormRooms.values()) {
						int floorIndex = (r.getRoomNumber() / 100) - 1; 
						int roomIndex = (r.getRoomNumber() % 100) - 1;  

						if (floorIndex >= 0 && floorIndex < 5 && roomIndex >= 0 && roomIndex < 4) {
							visualGrid[floorIndex][roomIndex] = "[ Room " + r.getRoomNumber() + " (" + r.getCurrentOccupancy() + "/" + r.getCapacity() + ") ]";
						}
					}

					for (int row = 0; row < visualGrid.length; row++) {
						System.out.print("Floor " + (row + 1) + ": ");
						for (int col = 0; col < visualGrid[row].length; col++) {
							if (!visualGrid[row][col].equals("[   Empty   ]")) {
								System.out.print(visualGrid[row][col] + "  ");
							}
						}
						System.out.println();
					}
				} else if (finalReportChoice.equals("B")) {
					System.out.println("\n--- FINANCIAL REPORT (PAYMENTS) ---");
					boolean paymentsFound = false;

					for (Room r : dormRooms.values()) {
						for (int i = 0; i < r.getAssignedStudents().length; i++) {
							Student s = r.getAssignedStudents()[i];
							if (s != null) {
								System.out.println("\nChecking Account for: " + s.getFullName() + " (Room " + r.getRoomNumber() + ")");
								s.viewPaymentHistory();
								paymentsFound = true;
							}
						}
					}
					if (!paymentsFound) {
						System.out.println("No students currently assigned to rooms to generate a report.");
					}
				}
				break;

			// CASE 7: Advance to Next Month (Automated Billing & Late Fees)
			case "7":
				advanceMonth(); 
				break;

			// CASE 8: Smart Student Search
			case "8":
				System.out.println("\n[ 8. SMART STUDENT SEARCH ]");
				System.out.print("Enter the exact first name (or first few letters) of the student: ");
				String query = scanner.nextLine().trim();

				if (query.length() == 0) {
					System.out.println("Error: Search query cannot be empty.");
				} else {
					System.out.println("System: Scanning all dorm rooms for names starting with '" + query + "'...");
					boolean found = false;

					for (Room r : dormRooms.values()) {
						for (int i = 0; i < r.getAssignedStudents().length; i++) {
							Student s = r.getAssignedStudents()[i];

							if (s != null) {
								String sName = s.getFullName();
								if (sName.length() >= query.length()) {
									String namePrefix = sName.substring(0, query.length());
									if (namePrefix.equalsIgnoreCase(query)) {
										System.out.println("\n--- MATCH FOUND ---");
										System.out.println("Student: " + sName + " | Room: " + r.getRoomNumber());
										System.out.println(s.toString());
										found = true;
									}
								}
							}
						}
					}
					if (!found) {
						System.out.println("No students found starting with: '" + query + "'");
					}
				}
				break;

			// CASE 9: Transfer Student to New Room
			case "9":
				boolean managingTransfer = true;

				while (managingTransfer) {
					System.out.println("\n[ 9. TRANSFER STUDENT ROOM ]");

					Student transferringStudent = null;
					Room oldRoom = null;

					while (transferringStudent == null) {
						System.out.print("Enter the ID of the student requesting a transfer (or 'cancel' to exit): ");
						String transferId = scanner.nextLine().trim();

						if (transferId.equalsIgnoreCase("cancel")) {
							managingTransfer = false;
							break;
						}

						for (Room r : dormRooms.values()) {
							for (int i = 0; i < r.getAssignedStudents().length; i++) {
								Student s = r.getAssignedStudents()[i];
								if (s != null && s.getId().equals(transferId)) {
									transferringStudent = s;
									oldRoom = r;
									break;
								}
							}
						}

						if (transferringStudent == null) {
							System.out.println("System Error: No assigned student found with ID '" + transferId + "'. Try again.");
						}
					}

					if (!managingTransfer)
						break; 

					System.out.println("Student Found: " + transferringStudent.getFullName() + " (Currently in Room " + oldRoom.getRoomNumber() + ")");

					boolean validNewRoom = false;
					while (!validNewRoom) {
						int newRoomNum = InputValidator.getValidNumber(scanner, "Enter the new Room Number to transfer to (or '0' to cancel)");

						if (newRoomNum == 0) {
							System.out.println("System: Transfer cancelled.");
							managingTransfer = false;
							break;
						}

						Room newRoom = dormRooms.get(newRoomNum);

						if (newRoom != null) {
							if (newRoom.getRoomNumber() == oldRoom.getRoomNumber()) {
								System.out.println("System Alert: Student is already in Room " + newRoomNum + ". Pick a different room.");
							} else {
								oldRoom.checkoutStudent(transferringStudent.getId());
								boolean success = transferringStudent.requestRoomChange(newRoom);

								// Rollback logic if the transfer fails
								if (!success) {
									System.out.println("System Alert: Transfer failed (Room is likely full). Returning student to Room " + oldRoom.getRoomNumber());
									oldRoom.assignStudent(transferringStudent);
									transferringStudent.setAssignedRoom(oldRoom);
									validNewRoom = true;
								} else {
									validNewRoom = true;
								}
								managingTransfer = false;
							}
						} else {
							System.out.println("System Error: Room " + newRoomNum + " does not exist. Please try again.");
						}
					}
				}
				break;

			// CASE 10: Checkout / Evict Student
			case "10":
				boolean managingCheckout = true;

				while (managingCheckout) {
					System.out.println("\n[ 10. STUDENT CHECKOUT & EVICTION ]");
					int checkoutRoomNum = InputValidator.getValidNumber(scanner, "Enter Current Room Number of the student (or type '0' to cancel): ");

					if (checkoutRoomNum == 0) {
						System.out.println("System: Returning to Main Menu.");
						managingCheckout = false;
						break;
					}

					Room checkoutRoom = dormRooms.get(checkoutRoomNum);

					if (checkoutRoom != null) {
						boolean hasResidents = false;
						for (Student s : checkoutRoom.getAssignedStudents()) {
							if (s != null)
								hasResidents = true;
						}

						if (!hasResidents) {
							System.out.println("System Alert: Room " + checkoutRoomNum + " is completely empty! Nobody to checkout.");
							continue;
						}

						System.out.println("\nCurrent Residents:");
						checkoutRoom.printResidents();

						Student targetStudent = null;

						while (targetStudent == null) {
							System.out.print("\nEnter the exact ID of the student to evict/checkout (or type 'cancel' to go back): ");
							String studentId = scanner.nextLine().trim();

							if (studentId.equalsIgnoreCase("cancel")) {
								break;
							}

							for (int i = 0; i < checkoutRoom.getAssignedStudents().length; i++) {
								Student s = checkoutRoom.getAssignedStudents()[i];
								if (s != null && s.getId().equals(studentId)) {
									targetStudent = s;
									break;
								}
							}

							if (targetStudent == null) {
								System.out.println("System Error: No student with ID '" + studentId + "' found in Room " + checkoutRoomNum + ". Please try again.");
							}
						}

						if (targetStudent != null) {
							System.out.println("\n--- FINAL BILLING FOR " + targetStudent.getFullName() + " ---");
							targetStudent.viewPaymentHistory();
							String confirm = InputValidator.getValidYesNo(scanner, "\nConfirm checkout and remove student from system?");

							if (confirm.equals("yes")) {
								checkoutRoom.checkoutStudent(targetStudent.getId());
								managingCheckout = false;
							} else {
								System.out.println("System: Checkout cancelled. Returning to menu.");
								managingCheckout = false;
							}
						}
					} else {
						System.out.println("System Error: Room " + checkoutRoomNum + " does not exist. Please try again.");
					}
				}
				break;

			// CASE 11: Process Student Payment
			case "11":
				System.out.println("\n[ 11. PROCESS STUDENT PAYMENT ]");
				System.out.print("Enter the ID of the student making a payment: ");
				String payId = scanner.nextLine().trim();

				boolean studentFoundForPay = false;
				for (Room r : dormRooms.values()) {
					for (int i = 0; i < r.getAssignedStudents().length; i++) {
						Student s = r.getAssignedStudents()[i];
						if (s != null && s.getId().equals(payId)) {
							studentFoundForPay = true;
							System.out.println("Found Student: " + s.getFullName());
							s.viewPaymentHistory();

							System.out.println("\nProcessing all pending and late balances...");
							int paidCount = 0;
							for (Payment p : s.getPaymentHistory()) {
								if (!p.getStatus().equals("Paid")) {
									p.pay();
									paidCount++;
								}
							}
							if (paidCount == 0) {
								System.out.println("System: Account is fully paid up. No payment needed.");
							}
							break;
						}
					}
				}
				if (!studentFoundForPay) {
					System.out.println("System Error: No student found in rooms with ID " + payId);
				}
				break;

			// CASE 12: Add New Dorm Room
			case "12":
				boolean addingRoom = true;

				while (addingRoom) {
					System.out.println("\n[ 12. ADD NEW DORM ROOM ]");

					int newRoomNum = InputValidator.getValidNumber(scanner, "Enter the new Room Number (or type '0' to cancel): ");

					if (newRoomNum == 0) {
						System.out.println("System: Returning to Main Menu.");
						addingRoom = false;
						break;
					}

					if (dormRooms.containsKey(newRoomNum)) {
						System.out.println("System Error: Room " + newRoomNum + " already exists in " + dormRooms.get(newRoomNum).getBuilding() + "! Please try a different number.");
					} else {
						String newBuilding = InputValidator.getValidName(scanner, "Enter the Building Name (e.g., Building A, Building C): ");
						int newCapacity = InputValidator.getValidNumberInRange(scanner, "Enter Room Capacity (1-4 beds)", 1, 4);
						double newPrice = InputValidator.getValidDouble(scanner, "Enter the Monthly Price for this room");

						Room brandNewRoom = new Room(newRoomNum, newBuilding, newCapacity, newPrice);
						dormRooms.put(newRoomNum, brandNewRoom);

						System.out.println("System: Success! Room " + newRoomNum + " has been added to " + newBuilding + " with " + newCapacity + " beds.");

						String addAnother = InputValidator.getValidYesNo(scanner, "\nDo you want to add another new room?");
						if (addAnother.equals("no")) {
							addingRoom = false;
						}
					}
				}
				break;

			// CASE 13: Update Room Pricing
			case "13":
				boolean managingPricing = true;

				while (managingPricing) {
					System.out.println("\n[ 13. ROOM PRICING MANAGEMENT ]");
					int priceRoomNum = InputValidator.getValidNumber(scanner, "Enter Room Number to update (or type '0' to exit): ");

					if (priceRoomNum == 0) {
						System.out.println("System: Returning to Main Menu.");
						managingPricing = false;
						break;
					}

					Room priceRoom = dormRooms.get(priceRoomNum);

					if (priceRoom != null) {
						System.out.println("Current price for Room " + priceRoomNum + ": $" + priceRoom.getMonthlyPrice());

						double newPrice = InputValidator.getValidDouble(scanner, "Enter the new monthly price for this room");

						priceRoom.setMonthlyPrice(newPrice);
						System.out.println("System: Success! Room " + priceRoomNum + " will now charge $" + newPrice + " per month.");
					} else {
						System.out.println("System Error: Room " + priceRoomNum + " does not exist. Please try again.");
					}
				}
				break;

			// CASE 14: Exit System
			case "14":
				System.out.println("\nSaving data...");
				saveSystemData(); 
				System.out.println("Exiting system. Goodbye!");
				running = false; 
				break;

			default:
				System.out.println("\nError: Invalid choice. Please type 1-14.");
			}
		}
		scanner.close(); 
	}

	/**
	 * Saves all system collections to disk using ObjectOutputStream.
	 */
	public static void saveSystemData() {
		try {
			FileOutputStream fileOut = new FileOutputStream("DormsData.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeObject(dormRooms);
			out.writeObject(studentWaitlist);
			out.writeObject(maintenanceQueue);
			out.writeObject(activeTickets);
			out.writeObject(staffList);

			out.close(); 
			fileOut.close();
			System.out.println("System: Data successfully saved to 'DormsData.ser'.");
		} catch (Exception e) {
			System.out.println("System Error: Could not save data. " + e.getMessage());
		}
	}

	/**
	 * Restores saved data on startup. Generates default data if no save file exists.
	 */
	@SuppressWarnings("unchecked")
	public static void loadSystemData() {
		File f = new File("DormsData.ser");

		if (f.exists()) {
			try {
				FileInputStream fileIn = new FileInputStream("DormsData.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);

				dormRooms = (TreeMap<Integer, Room>) in.readObject();
				studentWaitlist = (LinkedList<Student>) in.readObject();
				maintenanceQueue = (Queue<MaintenanceRequest>) in.readObject();
				activeTickets = (LinkedList<MaintenanceRequest>) in.readObject(); 
				staffList = (LinkedList<StaffMember>) in.readObject();

				// Reset worker availability in case the system was shut down during a job
				for (int i = 0; i < staffList.size(); i++) {
					staffList.get(i).setAvailable(true);
				}

				in.close();
				fileIn.close();
				System.out.println("System: Previous data successfully loaded!");
			} catch (Exception e) {
				System.out.println("System Error: Could not load data.");
			}
		} else {
			System.out.println("System: No previous data found. Starting fresh.");

			dormRooms.put(101, new Room(101, "Building A", 2, 1500.0));
			dormRooms.put(102, new Room(102, "Building A", 1, 2000.0));
			dormRooms.put(201, new Room(201, "Building A", 3, 1200.0));
			dormRooms.put(202, new Room(202, "Building A", 2, 1500.0));
			dormRooms.put(301, new Room(301, "Building A", 1, 2200.0));

			dormRooms.put(401, new Room(401, "Building B", 2, 1600.0));
			dormRooms.put(402, new Room(402, "Building B", 2, 1600.0));
			dormRooms.put(501, new Room(501, "Building B", 1, 2300.0));

			staffList.add(new StaffMember("111111111", "David", "0501112222", "Plumber"));
			staffList.add(new StaffMember("222222222", "Sarah", "0503334444", "Electrician"));
			staffList.add(new StaffMember("333333333", "Mike", "0505556666", "Handyman"));
		}
	}

	/**
	 * Automates the monthly billing process. Applies late fees and issues new rent charges.
	 */
	public static void advanceMonth() {
		System.out.println("\n[ TIME SKIP: ADVANCING TO NEXT MONTH ]");
		int chargedCount = 0;

		for (Room r : dormRooms.values()) {
			for (int i = 0; i < r.getAssignedStudents().length; i++) {
				Student s = r.getAssignedStudents()[i];
				if (s != null) {

					for (Payment p : s.getPaymentHistory()) {
						if (p.getStatus().equals("Pending")) {
							p.setStatus("Late");
							p.calculateLateFee();
						}
					}

					int paymentId = 9000 + (int) (Math.random() * 1000);
					Payment newRent = new Payment(paymentId, r.getMonthlyPrice(), "01/10/2026", "Pending", s);
					s.addPayment(newRent);
					chargedCount++;
				}
			}
		}
		System.out.println("System: " + chargedCount + " students have been billed. Late fees applied to unpaid balances!");

		System.out.println("\n[ RUNNING SYSTEM MAINTENANCE AUDIT... ]");
		int escalatedTickets = 0;
		for (MaintenanceRequest req : maintenanceQueue) {
			if (req.getStatus().equals("Open") && !req.getPriority().equals("Urgent")) {
				req.escalate();
				escalatedTickets++;
			}
		}
		if (escalatedTickets > 0) {
			System.out.println("System Alert: " + escalatedTickets + " forgotten maintenance tickets were automatically escalated!");
		}
	}

	// Helper to ensure global uniqueness of IDs across all entities
	public static boolean isIdTaken(String searchId) {
		for (Room r : dormRooms.values()) {
			for (int i = 0; i < r.getAssignedStudents().length; i++) {
				Student s = r.getAssignedStudents()[i];
				if (s != null && s.getId().equals(searchId))
					return true;
			}
		}
		for (Student s : studentWaitlist) {
			if (s.getId().equals(searchId))
				return true;
		}
		for (StaffMember staff : staffList) {
			if (staff.getId().equals(searchId))
				return true;
		}
		return false; 
	}

	// Helper to ensure global uniqueness of phone numbers
	public static boolean isPhoneTaken(String searchPhone) {
		for (Room r : dormRooms.values()) {
			for (int i = 0; i < r.getAssignedStudents().length; i++) {
				Student s = r.getAssignedStudents()[i];
				if (s != null && s.getPhoneNumber().equals(searchPhone))
					return true;
			}
		}
		for (Student s : studentWaitlist) {
			if (s.getPhoneNumber().equals(searchPhone))
				return true;
		}
		for (StaffMember staff : staffList) {
			if (staff.getPhoneNumber().equals(searchPhone))
				return true;
		}
		return false;
	}
}