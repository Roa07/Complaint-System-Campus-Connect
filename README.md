# Campus Connect System

A full-stack Java Spring Boot web application designed for managing, tracking, and resolving user complaints. 

## 🚀 Technologies Used

*   **Backend:** Java 21, Spring Boot 3.4.0
*   **Web Framework:** Spring Web MVC
*   **Templating Engine:** Thymeleaf (with Spring Security extras)
*   **Security:** Spring Security
*   **Database Access:** Spring Data JPA, Hibernate
*   **Database:** MySQL
*   **Utilities:** Lombok, Spring Boot Mail (for OTP/Email verification)

## 📋 Key Features

*   **User Authentication & Authorization:** Secure registration, login, and role-based access control (Admin, User).
*   **Password Recovery:** OTP-based forgot password mechanism using email verification.
*   **Complaint Management:** Users can submit, view, and track the status of their complaints.
*   **Interaction:** Users and admins can comment on complaints, and complaints can receive "likes".
*   **Dashboards:** Dedicated dashboards for users and administrators to manage their respective activities.
*   **User Profiles:** Profile management capabilities for registered users.
*   **File Uploads:** Support for attaching screenshots or files to complaints.

## 🛠️ Project Structure Overview

*   **Controllers:** Handling routing for Authentication, Complaints, Dashboard, Admin tasks, and Profile management.
*   **Entities:** Core data models including `User`, `Complaint`, `Comment`, `ComplaintLike`, and `OtpToken`.
*   **Repositories:** Spring Data JPA interfaces for database operations.
*   **Templates:** HTML views rendered via Thymeleaf.

## ⚙️ Setup and Installation

### Prerequisites
*   JDK 21 or higher
*   Maven 3.x
*   MySQL Server

### Configuration
1.  Clone the repository.
2.  Open the `src/main/resources/application.properties` file.
3.  Configure your MySQL database connection settings:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
    spring.datasource.username=your_db_username
    spring.datasource.password=your_db_password
    ```
4.  Configure your Mail settings for OTP functionality if required.

### Running the Application

You can run the application using Maven:

```bash
./mvnw spring-boot:run
```
Alternatively, build the jar and execute it:
```bash
./mvnw clean package
java -jar target/cmplaint-system-0.0.1-SNAPSHOT.jar
```

## 📄 License
This project is open-source.
