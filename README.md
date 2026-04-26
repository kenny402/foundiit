# FoundIt+ (FoundIIt)
### Smart Lost & Found and Claim Verification System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

FoundIt+ is a comprehensive, secure, and user-friendly web application designed to bridge the gap between people who have lost items and those who have found them. Built with a robust Spring Boot backend and a responsive Thymeleaf frontend, it provides a reliable platform for reporting, searching, and claiming items.

---

## 🌟 Key Features

### 🔍 Search & Discovery
*   **Intuitive Browsing**: View all lost and found items in a clean, paginated list.
*   **Smart Filtering**: Filter items by status (LOST/FOUND/CLAIMED), category, or location.
*   **Keyword Search**: Quickly find items using search terms.
*   **Detailed Views**: Access comprehensive item details, including high-resolution images, descriptions, and discovery dates.

### 📝 Reporting & Management
*   **Streamlined Reporting**: Simplified multi-field forms for reporting lost or found items.
*   **Photo Uploads**: Secure image uploading with automatic UUID-based file storage.
*   **Personal Dashboard**: Track your own reports and manage their status from a single location.
*   **Case Resolution**: Close resolved cases or update item status as they are claimed.

### 🛡️ Security & Reliability
*   **Secure Authentication**: Role-based access control powered by Spring Security.
*   **Privacy Protection**: Reporter-only access for managing specific items.
*   **Reliable Verification**: Built-in verification mechanisms for claims.
*   **Input Validation**: Robust server-side validation to ensure data integrity.

---

## 💻 Technology Stack

| Component | Technology |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.2.0, Spring Data JPA |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript |
| **Security** | Spring Security 6 (Auth, CSRF, Authorization) |
| **Database** | MySQL 8.0+ |
| **Documentation** | Apache POI (Excel), OpenPDF (PDF generation) |
| **Build Tool** | Maven 3.9+ |

---

## 📂 Project Structure

```text
foundiit-master/
├── src/main/java/com/student/foundiit/
│   ├── controller/      # Web controllers handling routes
│   ├── service/        # Business logic & file handling
│   ├── repository/     # Data access layer (JPA)
│   ├── model/         # Database entities
│   └── dto/           # Data Transfer Objects & Validation
├── src/main/resources/
│   ├── templates/     # UI HTML pages (Thymeleaf)
│   ├── static/        # CSS, JS, and static assets
│   └── application.properties # Core configuration
├── docs/              # Detailed technical documentation
├── sql/               # SQL scripts for database setup
├── logs/              # Application and build logs
├── uploads/           # User-uploaded item images
└── pom.xml            # Project dependencies & build config
```

---

## 🚀 Getting Started

### 📋 Prerequisites
*   **Java 17** or higher
*   **MySQL Server** (Running locally or remotely)
*   **Maven** 3.6+

### 🛠️ Installation

1.  **Clone & Navigate**:
    ```bash
    cd foundiit-master
    ```

2.  **Database Setup**:
    ```sql
    CREATE DATABASE foundiit;
    ```
    *Apply schema from `setup_tables.sql` if manual setup is preferred.*

3.  **Create Upload Folders**:
    ```bash
    mkdir uploads/items uploads/claims uploads/profiles
    ```

4.  **Build Project**:
    ```bash
    mvn clean compile
    ```

### 🏃 Running the App

Execute the following command in the project root:
```bash
mvn spring-boot:run
```
Once started, access the application at: **`http://localhost:8080/`**

---

## 📖 Available Documentation

For deeper technical details, please refer to our specialized guides:

*   [**Quick Start Guide**](docs/QUICK_START_GUIDE.md) - Get up and running in 5 minutes.
*   [**Technical Implementation Summary**](docs/IMPLEMENTATION_SUMMARY.md) - Architecture & metrics.
*   [**Complete Feature Guide**](docs/ITEM_REPORTING_COMPLETE_GUIDE.md) - Detailed component docs.
*   [**Deployment Checklist**](docs/DEPLOYMENT_CHECKLIST.md) - Preparation for production.
*   [**Documentation Index**](docs/DOCUMENTATION_INDEX.md) - A full list of all available project documentation.

---

## 📝 Configuration

Key settings in `src/main/resources/application.properties`:
*   `spring.datasource.url`: Database connection URL.
*   `file.upload-dir`: Location for storing images (default: `./uploads/`).
*   `server.port`: Application port (default: 8080).

---

## 🤝 Credits
**Developed by:** Imena Kenny  
**Copyright:** © 2026 Imena Kenny
