# Gatling tutorial project

This is tutorial project for education purposes to help team members get familiar with basic performance tasks using Gatling

## Setup Project

1. **Clone the Project**
    - Use the following command to clone the project from Bitbucket:
      ```bash
      git clone <repository_url>
      ```

## Prerequisites

Before starting any work, ensure the following:
- **Git** is installed on all team members' machines.
- **Java** is installed and configured properly.
- All team members have Java certificates.
- The project can be built using **Gradle**.

## Tasks

### Task 1: Verify Setup

1. **Run Task**
    - Execute the following command:
     `./gradlew gatlingRun`
    - Understand the output and locate the Gatling report.

### Task 2: Basic HTTP Methods

1. **Create Requests**
    - Use `https://httpbin.test.k6.io/` to create simple `GET`, `POST`, `DELETE`, and `PUT` requests.
    - Include checks, custom payloads, and query parameters in the requests.


### Task 3: Complex Scenario Creation

1. **Create a Scenario**
    - Develop a scenario with the following blocks:
        - `GET` request
        - `Check`
        - `Pause`
        - `POST` request
        - Failing check
        - Exit if failed

### Task 4: Real Site Testing

1. **Create a Scenario for BlazeDemo**
    - Start a gatling recorder `./gradlew gradlewRecorder`
    - Navigate to the main page of [BlazeDemo](https://blazedemo.com/).
    - Choose a random destination.
    - Find flights and choose a random flight.
    - Book the flight with random user data.
    - 
### Task 5: Load Profiles

1. **Create Load Profiles**
    - Create different load profiles using the same scenario:
        - 10 users
        - Ramp users
        - Maximum (25 users)


### Task 6: Documentation in Confluence

1. **Create Tables in Confluence**
    - **Table 1:** Test Scenario with all steps.
    - **Table 2:** Scenario Profiles with descriptions.
    - Add the Gatling report to the Confluence page.


