# Technikon - Project Overview

## Installation Requirements
- **Java SE 21**
- **MySQL Server**
- **WildFly 33.0.1**
-  **Postman (for API testing)**


## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/armandok0/technicon-web.git
```

### 2. Create the MySQL database

Once MySQL is running, open a MySQL client and create the required database by executing the following command:

```sql
CREATE DATABASE Technikon;
```

You will also need to configure the connection settings (username, password) in the application server's configuration or `persistence.xml` file.

### 3. Persistence Configuration

Locate the `persistence.xml` file in the project at `src/main/resources/META-INF/persistence.xml`. Ensure the following line is set to allow automatic schema updates:

```xml
<property name="hibernate.hbm2ddl.auto" value="update" />
```


### 4. Deploying the Application

1. Build the project.

2. Deploy the generated `.war` file to your WildFly application server.
   

### 5. Postman Collection for API Testing

A Postman collection is included in the project for easy API testing. The collection is located at:

```
src/main/resources/Postman/Technikon.postman_collection.json
```

This collection contains pre-configured requests for testing the API endpoints provided by the application.
