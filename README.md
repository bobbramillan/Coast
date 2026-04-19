# Coast

A terminal-based Java social media account management system, built from scratch in a single session.
What started as a simple DB diagram turned into a full backend architecture with AWS Lambda, API Gateway,
Supabase, and SHA-256 password hashing.

---

## Where It Started

The project began with a DBML database diagram for a social media app with tables for users, posts,
comments, likes, reposts, saves, and follows. From there, the goal was to build a terminal app in Java
that let users create accounts. By the end of the session, Coast had evolved into a secure, cloud-backed
application that anyone could download and run.

---

## The Journey

### 1. Database Design
Started with a full social media schema in DBML. First changes made:
- Removed the `reposts` table
- Added `email` and `password` columns to `users`
- Later added `created_at`, `last_sign_in`, and `last_sign_out` timestamp columns

All schema changes were made directly in Supabase using raw SQL via the SQL Editor. One early hurdle
was changing `user_id` from `integer` to `varchar` — foreign key constraints across multiple tables
blocked the change, requiring all constraints to be dropped, columns altered, and constraints re-added
in a single script.

### 2. First Java + Maven Setup
The first version of the app was a single `Main.java` that prompted the user to fill in their details
in the terminal. It connected directly to Supabase via the REST API using OkHttp. Early hurdles:
- Maven dependencies not loading — solved by reloading the Maven project in IntelliJ
- Supabase URL was wrong — was pointing at the dashboard URL instead of the project API URL
- `user_id` type mismatch — the DB expected an integer but the app was generating alphanumeric strings

### 3. Object-Oriented Refactor
The app was refactored from a single class into a proper OO architecture applying all four pillars:

**Encapsulation** — all fields in `User` are `private final`, only accessible via getters.
`SupabaseClient` has a private constructor since it should never be instantiated.

**Abstraction** — `Action` and `AuthenticatedAction` are abstract classes that hide implementation
details from `Menu`. The menu just calls `execute()` without knowing what each action does.

**Inheritance** — `AuthenticatedAction extends Action` adds an auth guard. `SignOut`, `DeleteAccount`,
`DisplayProfile`, `ChangeName`, `ChangeEmail`, `ChangePassword`, and `ChangeAbout` all extend
`AuthenticatedAction`. `CreateAccount` and `SignIn` extend `Action` directly since they don't require
authentication.

**Polymorphism** — `Menu` holds arrays of `Action` objects and calls `execute()` on whichever one
the user selects, without knowing the concrete type.

Other design principles applied:
- **SRP** — each class has one reason to change
- **UML relationships** — `Menu` has-a list of `Action` objects, each action is-a `Action`
- **Access modifiers** — `public`, `protected`, `private` applied deliberately throughout
- `execute()` in `AuthenticatedAction` is marked `final` so subclasses can never override the auth guard
- `executeAuthenticated()` is `protected abstract` — only subclasses can implement it

### 4. Package Structure
Actions are scoped by domain. Current structure:

    actions/
    ├── Action.java
    ├── AuthenticatedAction.java
    ├── CreateAccount.java
    ├── SignIn.java
    ├── SignOut.java
    ├── DisplayProfile.java
    ├── ChangeName.java
    ├── ChangeEmail.java
    ├── ChangePassword.java
    ├── ChangeAbout.java
    ├── DeleteAccount.java
    └── utils/
        ├── Validator.java
        ├── UserGenerator.java
        └── PasswordHasher.java

`Validator` is shared across actions (email validation in both `CreateAccount` and `SignIn`).
`UserGenerator` and `PasswordHasher` are scoped to the `actions` layer since only actions use them.

As the app grows this structure scales naturally:

    actions/
    ├── users/
    ├── posts/
    ├── comments/
    └── ...

### 5. Session Management
A simple `Session.java` class holds the currently logged in user in memory. It has static `login()`,
`logout()`, `getCurrentUser()`, and `isLoggedIn()` methods. `AuthenticatedAction` checks
`Session.isLoggedIn()` before allowing any protected action to proceed.

### 6. Password Hashing with SHA-256
Passwords were originally stored as plain text in Supabase — a major security risk. The solution
was to hash passwords using SHA-256 with `user_id` as a salt.

**Why salt?** Two users with the same password produce completely different hashes because the salt
(user_id) is unique per user. This also protects against rainbow table attacks.

**Why SHA-256 and not BCrypt?** SHA-256 is built into Java's `java.security.MessageDigest` with no
extra dependencies. BCrypt is more secure (intentionally slow, harder to brute force) but SHA-256
with a unique salt is a solid step up from plain text for this project.

**How it works:**
1. User signs up → `SHA-256(password + user_id)` → hash stored in Supabase
2. User signs in → types password → `SHA-256(password + user_id)` → compared to stored hash
3. The real password is never stored anywhere

### 7. Email Uniqueness
Email uniqueness is enforced at two levels:
- **Database level** — Supabase rejects duplicate emails
- **Application level** — `emailExists()` checks the DB before inserting or updating, giving the
  user a friendly error message instead of a raw DB error

### 8. AWS Secrets Manager
The first AWS integration. Supabase credentials were moved out of `config.properties` and into
AWS Secrets Manager. The app fetched credentials at startup using the AWS SDK for Java.

**Hurdle:** This only worked on machines with AWS credentials configured via `aws configure`.
Anyone else downloading the JAR would get an authentication error — credentials were tied to the
developer's machine, not the app.

### 9. AWS Lambda + API Gateway
The real solution to credential security. A separate private project (CoastBackend) was created
with Lambda handlers for every DB operation:
- FetchUserByEmailHandler
- FetchExistingUserIdsHandler
- EmailExistsHandler
- InsertUserHandler
- UpdateUserHandler
- DeleteUserHandler

Each handler fetches Supabase credentials from Secrets Manager at runtime inside AWS, calls
Supabase, and returns the result. API Gateway exposes each Lambda as a public HTTPS endpoint.
Coast now calls these endpoints instead of Supabase directly. No credentials exist anywhere in the JAR.

The architecture:

    User's Machine → API Gateway → Lambda → Secrets Manager → Supabase

**Hurdles along the way:**
- `coast-admin` IAM user needed multiple policy attachments: `SecretsManagerReadWrite`,
  `IAMFullAccess`, `AWSLambda_FullAccess`, `AmazonAPIGatewayAdministrator`
- zsh interpreted `*` in ARNs as a wildcard — solved by wrapping ARNs in quotes
- Lambda cold starts cause slight latency on first call — acceptable for a terminal app

**Why Lambda over running the backend locally?**
If credentials lived in the JAR, anyone could decompile it and extract them. Lambda keeps all
sensitive logic and credentials inside AWS, completely separate from the distributed JAR.

---

## What We Learned

### Java
- Maven project setup and dependency management
- OkHttp for HTTP requests
- Gson for JSON parsing
- `java.security.MessageDigest` for SHA-256 hashing
- `java.time.Instant` for UTC timestamps
- Abstract classes, interfaces, access modifiers, and the 4 pillars of OOP

### Database
- Writing and running raw SQL in Supabase
- Altering column types across foreign key constraints
- REST API pattern for database operations (GET, POST, PATCH, DELETE)

### AWS
- IAM users, roles, and policies
- AWS CLI setup and usage
- Secrets Manager for credential storage
- Lambda functions with Java 17 runtime
- API Gateway resources, methods, integrations, and deployments
- Lambda cold starts and how to mitigate them

### Security
- Why plain text passwords are dangerous
- How salting prevents rainbow table attacks
- Why credentials should never live in distributed binaries
- How Lambda acts as a secure intermediary between clients and databases

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application language |
| Maven | Dependency management and build |
| OkHttp | HTTP client |
| Gson | JSON parsing |
| Supabase (PostgreSQL) | Database |
| AWS Secrets Manager | Credential storage |
| AWS Lambda | Serverless backend functions |
| AWS API Gateway | Public HTTPS endpoints for Lambda |
| SHA-256 | Password hashing |

---

## Where To Go Next

- **Spring Boot** — replace the raw Lambda handlers with a proper REST framework
- **Docker** — containerize Coast for even easier distribution
- **CI/CD with GitHub Actions** — auto-deploy CoastBackend to AWS on every push to main
- **Frontend** — build a web or mobile client that connects to the same Lambda backend
- **BCrypt** — upgrade from SHA-256 to BCrypt for stronger password hashing
- **Provisioned Concurrency** — eliminate Lambda cold starts for production use

---

## Notes

- CoastBackend is a private repository — it contains the Lambda handlers and AWS infrastructure
- No credentials exist anywhere in this repository or the distributed JAR
- Existing users created before SHA-256 hashing was added must be deleted and recreated
