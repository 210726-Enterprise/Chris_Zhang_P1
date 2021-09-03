# CUSTOM Basic ORM
A custom object relational mapping (ORM) framework, written in Java, which allows for a simplified and SQL-free interaction with a relational data source. Low-level JDBC is completely abstracted away from the developer, allowing them to easily query and persist data to a data source. Makes heavy use of the Java Reflection API in order to support any entity objects as defined by the developer.


#Usage
For an Object to be viable inside my ORM, the class name must be annotated with an @Entity tag, the primary key field must be annotated with @Primary,
and column fields with @Column.  All getters and setter methods must follow best coding practice naming conventions.
