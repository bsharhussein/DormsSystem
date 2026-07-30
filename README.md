# DormsSystem — Student Dormitory Management System

A console-based dormitory management system written in Java, developed as the final
project for the Introduction to Computer Engineering course at Ruppin Academic Center.

## About

Managing student dormitories with spreadsheets leads to students assigned to unsuitable
rooms, lost payment records, delayed maintenance handling, and no clear picture of
occupancy or revenue. DormsSystem centralizes students, rooms, payments, and maintenance
requests into a single application.

**Goals:**
- Keep all student, room, payment, and maintenance data in one place
- Automate room assignment based on availability
- Track payments and outstanding debts
- Handle maintenance requests by priority
- Give staff up-to-date reports on occupancy and revenue

## Features

- Register new students and assign them to available rooms
- Room availability checking and occupancy tracking
- Monthly payment records with automatic late-fee calculation
- Maintenance request submission, status updates, and priority escalation
- Filtered listings: available rooms, overdue payments, open requests
- Summary reports for occupancy, revenue, and open requests

## Object-Oriented Design

| Type | Class | Purpose |
|---|---|---|
| Abstract class | `Person` | Shared base for anyone in the system (name, ID, phone); declares abstract `getRole()` |
| Class | `Student` | A student living in the dorms; extends `Person` |
| Class | `StaffMember` | Dorm staff and managers; extends `Person` |
| Subclass | `InternationalStudent` | Extends `Student` with passport, country of origin, and visa expiry (`checkVisaStatus()`) |
| Class | `Room` | A dorm room — number, building, capacity, occupancy, monthly price |
| Class | `Payment` | A monthly or one-time payment tied to a student |
| Class | `MaintenanceRequest` | A reported fault, with priority and status |
| Interface | `Payable` | Implemented by `Payment` and `MaintenanceRequest` — `pay()`, `calculateAmount()`, `isOverdue()` |

**Design rationale:**
- `Person` is abstract because a student and a staff member genuinely share data, but a
  bare "person" is not a meaningful entity in this system.
- `InternationalStudent` uses inheritance because the relationship is IS-A — it is a
  student, with additional fields.
- `Payable` is an interface because payments and chargeable maintenance requests are
  unrelated entities that nonetheless share the same behaviour.

**Relationships:**
- A student is assigned to one room; a room holds several students
- A student can have many payments; each payment belongs to one student
- Each maintenance request refers to one specific room

## Usage Scenarios

**Assigning a new student.** The dorm manager selects "Add new student" and enters the
details. The system lists available rooms sorted by building, verifies capacity via
`isAvailable()`, performs the assignment, and automatically creates the first payment
record.

**Making a payment.** A student views their full payment history, selects the current
payment, and the system calculates the final amount including any late fee via
`calculateLateFee()` before marking it paid with `markAsPaid()`.

## How to Run

**Requirements:** Java JDK 17 or later

**From an IDE (IntelliJ IDEA / Eclipse):**
1. Open the project folder
2. Locate `Main.java` under `src`
3. Run it

**From the command line:**
```bash
javac -d out src/*.java
java -cp out Main
```

On startup the program loads sample students and rooms, then displays the main menu.
Choose an option by number; select "Exit" to quit. Any action that modifies data asks
for confirmation first.

## Project Structure

```
src/          Java source files
README.md     This file
```

## Authors

Developed by a team of four students at Ruppin Academic Center.
