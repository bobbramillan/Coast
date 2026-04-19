# Coast

A terminal-based Java application backed by Supabase, built to explore core software engineering concepts.

## What We Built

A full user account management system with the following features:

- Create Account (with unique ID generation and email uniqueness check)
- Sign In / Sign Out (with timestamps persisted to Supabase)
- Display Profile
- Change Name, Email, Password, About
- Delete Account (with email confirmation)
- SHA-256 password hashing with user_id as salt

## Tech Stack

- Java 17
- Maven
- OkHttp (HTTP client)
- Gson (JSON parsing)
- Supabase (PostgreSQL REST API)

## What We Learned

### Object-Oriented Design
- **4 Pillars of OOP** applied throughout:
  - **Encapsulation** — private fields, controlled access via getters, SupabaseClient constructor private
  - **Abstraction** — Action and AuthenticatedAction hide implementation details from Menu
  - **Inheritance** — AuthenticatedAction extends Action, all actions inherit scanner and execute()
  - **Polymorphism** — Menu calls execute() on any Action without knowing the concrete type

### Design Principles
- **Single Responsibility Principle (SRP)** — each class has one reason to change
- **UML relationships** — Menu has-a list of Action objects, actions is-a Action
- **Access modifiers** — public, protected, private applied deliberately
- final on execute() in AuthenticatedAction to prevent subclasses overriding the auth guard

### Package Structure
- Actions scoped by domain (actions/users/, scalable to actions/posts/ etc.)
- utils/ scoped to the domain that owns them
- Shared infrastructure (SupabaseClient, Session, User) at the top level

### Security
- Passwords stored as SHA-256 hashes — never plain text
- user_id used as a unique salt per user
- Same password produces different hashes for different users
- Protects against DB breaches and rainbow table attacks

### Database
- Supabase PostgreSQL via REST API
- Altered column types across foreign key constraints
- Added created_at, last_sign_in, last_sign_out timestamp columns
- Email uniqueness enforced at both DB and application level

## Project Structure

Coast/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── coast/
        │           ├── Main.java
        │           ├── Menu.java
        │           ├── Session.java
        │           ├── SupabaseClient.java
        │           ├── User.java
        │           └── actions/
        │               ├── Action.java
        │               ├── AuthenticatedAction.java
        │               ├── ChangeAbout.java
        │               ├── ChangeEmail.java
        │               ├── ChangeName.java
        │               ├── ChangePassword.java
        │               ├── CreateAccount.java
        │               ├── DeleteAccount.java
        │               ├── DisplayProfile.java
        │               ├── SignIn.java
        │               ├── SignOut.java
        │               └── utils/
        │                   ├── PasswordHasher.java
        │                   ├── UserGenerator.java
        │                   └── Validator.java
        └── resources/
            └── config.properties

## Notes

- config.properties is not committed — add your own Supabase URL and API key
- Delete existing users in Supabase before using the app if they have plain text passwords
