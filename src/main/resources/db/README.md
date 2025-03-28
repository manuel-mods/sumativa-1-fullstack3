# Database Migrations

This directory contains database migration scripts that are automatically executed by Flyway when the application starts.

## Migration Files

- `V1__Create_Initial_Schema.sql`: Creates the initial database tables for users, roles, topics, and comments.
- `V2__Add_Indexes.sql`: Adds indexes to improve query performance.

## How It Works

1. When the application starts, Flyway scans the `db/migration` directory for SQL files.
2. Files are sorted by version number (the `V1`, `V2` prefix).
3. Flyway compares the versions against its schema history table to determine which migrations need to be applied.
4. Each script is executed in order.

## Adding New Migrations

To add a new migration:

1. Create a new SQL file in the `db/migration` directory.
2. Name it following the pattern `V{n}__{description}.sql`, where `{n}` is the next version number and `{description}` is a brief description with words separated by underscores.
3. Write your SQL statements in the file.

## Configuration

The Flyway configuration is set in the `application.properties` file:

```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
```