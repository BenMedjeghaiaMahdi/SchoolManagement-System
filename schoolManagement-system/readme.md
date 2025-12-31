# School Management System - Backend Architecture

## Architecture Overview
This is a multi-module Mavenapplication consisting of:
- core` Module: Handles business logic and data persistence.
- Service Layer(`SchoolService.java`): A Singleton entry point managing transactions, security/permissions, and business rules.
- DAO Layer: Uses **JDBI** to map SQL queries directly to Java Interfaces (`TeacherDao`, `StudentDao`).
- Database: **SQLite** configured in **WAL (Write-Ahead Logging)** mode for high concurrency.
- `app` Module: The JavaFX GUI presentation layer (depends on `core`).

## Tech Stack & Documentation
- Build Tool: [Apache Maven](https://maven.apache.org/guides/)
- Database: [SQLite](https://www.sqlite.org/wal.html) via [SQLite JDBC](https://github.com/xerial/sqlite-jdbc)
- ORM/SQL Mapper: [JDBI v3](https://jdbi.org/) (SqlObject Plugin)
- UI Framework: [JavaFX](https://openjfx.io/)

## How to Run
**1. Build & Install Dependencies** (Run this after any code change in `core`)
```bash
mvn clean install
```

**2. Run the Application**
```bash
cd app
mvn exec:java -Dexec.mainClass="com.school.ui.App"
```

## Notes:

- The **App.java** file includes a test case for running the application flow via console.
- If you are using the existing stack, the gui build goes inside **app module**. The **SchoolService.java** file is the  main entry that manages the backend as hole, and is the source of trueth when communicating with the database. Any public methods ment for data crud operationss, or direct backend gui interaction, should be implemented in that class.
- The resources above should be enough to get you started. If there are any missing features, you can add them the same way the rest of the  backend was implemented.