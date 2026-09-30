# Core Java Banking System

Console-based banking app in **pure Java + PostgreSQL via JDBC**.
No Spring. No Hibernate. No ORM. Built to master OOP practically before moving to frameworks.

## Stack
- Java 17
- PostgreSQL
- JDBC (plain)
- Maven

## Setup
1. Create database: `psql -U postgres -c "CREATE DATABASE bankdb;"`
2. Apply schema: `psql -U postgres -d bankdb -f sql/schema.sql`
3. Copy `src/main/resources/db.properties.example` → `db.properties`
4. Fill in your PostgreSQL credentials
5. Run: `mvn clean compile exec:java`