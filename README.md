# Simple Expense Splitter 

A full-stack expense splitting application built with Spring Boot and Vue.js to manage group expenses and calculate settlements efficiently.

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat&logo=vue.js&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat&logo=tailwind-css&logoColor=white)

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [API Documentation](#api-documentation)

## Features

- *User Authentication*: Secure JWT-based authentication with registration and login
- *Group Management*: Create and manage expense groups with multiple members
- *Expense Tracking*: Add, update, and delete expenses with detailed categorization
- *Selective Participants*: Split expenses among selected group members (like Splitwise)
- *Smart Settlement Calculation*: Minimize transactions using an optimized settlement algorithm
- *Real-time Updates*: Instant calculation of who owes whom
- *Responsive UI*: Mobile-friendly interface built with TailwindCSS
- *Form Validation*: Client and server-side validation with helpful error messages

## Tech Stack

### Backend
- *Java 17* - Programming language
- *Spring Boot 3.4* - Application framework
- *Spring Security* - Authentication & authorization
- *JWT (JSON Web Tokens)* - Secure authentication
- *Spring Data JPA* - Database access
- *PostgreSQL* - Relational database
- *Flyway* - Database migration management
- *Maven* - Dependency management
- *JUnit & Mockito* - Unit testing

### Frontend
- *Vue.js 3* - Progressive JavaScript framework
- *Vue Router 4* - Client-side routing
- *Axios* - HTTP client
- *TailwindCSS 3* - Utility-first CSS framework
- *Vite* - Build tool and dev server
- *Vitest* - Unit testing framework
- *JavaScript ES6+* - Programming language


## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/register | Register new user | No |
| POST | /api/auth/login | Login user | No |

### User Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/users | Get all users | Yes |
| GET | /api/users/{id} | Get user by ID | Yes |
| PUT | /api/users/{id} | Update user | Yes |
| DELETE | /api/users/{id} | Delete user | Yes |

### Group Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/groups | Get all groups | Yes |
| GET | /api/groups/{id} | Get group by ID | Yes |
| GET | /api/groups/user/{userId} | Get user's groups | Yes |
| POST | /api/groups | Create new group | Yes |
| PUT | /api/groups/{id} | Update group | Yes |
| DELETE | /api/groups/{id} | Delete group | Yes |

### Expense Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/expenses | Get all expenses | Yes |
| GET | /api/expenses/{id} | Get expense by ID | Yes |
| GET | /api/expenses/group/{groupId} | Get group expenses | Yes |
| POST | /api/expenses | Create expense | Yes |
| PUT | /api/expenses/{id} | Update expense | Yes |
| DELETE | /api/expenses/{id} | Delete expense | Yes |
| GET | /api/expenses/group/{groupId}/settlements | Calculate settlements | Yes |
