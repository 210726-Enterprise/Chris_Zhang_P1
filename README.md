# CUSTOM Basic ORM

## Description
A custom object relational mapping (ORM) framework, written in Java, which allows for a simplified and SQL-free interaction with a relational data source. Low-level JDBC is completely abstracted away from the developer, allowing them to easily query and persist data to a data source. Makes heavy use of the Java Reflection API in order to support any entity objects as defined by the developer.


## Tech Stack
- Java 8
- Apache Maven
- JUnit
- Java EE Servlet API (v4.0+)
- PostgreSQL
- AWS RDS
- DBeaver
- Git SCM (on Github)

## Features
- Allows CRUD functionality for any Object within a database
- Contains a sample 'Cat' Object along with a Servlet to demonstrate functionality

## Getting Started
1. Download the most recent .war file in the /target/ folder and extract into your own project
2. Create and assign system environment variables "db_url", "db_username", and "db_password" to your respective database url, name, and password.

## Usage
- For an Object to be viable inside my ORM, the class name must be annotated with an @Entity tag, the primary key field must be annotated with @Primary,
and column fields with @Column
- Objects must have a NoArgs constructor
- All getters and setter methods must follow best coding practice naming conventions
