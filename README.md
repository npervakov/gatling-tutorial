# Gatling tutorial project

This is tutorial project for education purposes to help team members get familiar with basic performance tasks using Gatling

## Setup Project
**Clone the Project**
   - Use the following command to clone the project from Bitbucket:
     ```bash
     git clone <repository_url>
     ```

## Prerequisites
Before starting any work, ensure the following:
- **Git** is installed on all team members' machines.
- **Java** is installed and configured properly.
- The project can be built using **Gradle**.

## How to run simulations
Use gatling tasks `gradlew gatlingRun` with parameters --simulation=ClassName
For example, if we want to run simulation TestSimulation.java  
`gradlew gatlingRun --simulation TestSimulation`
### Task 1: Simple scenario
**Open the simulation example and understand basic syntax**  
   - Http protocol
   - ScenarioBuilder
   - SetUp method

### Task 2: Modify simple scenario
**Develop a scenario with the following blocks:**  
- `pace` to define the time interval between requests.  
- `GET` request.  
- `Check` to validate the response.  
- `Pause` to add a delay between requests.  
- `POST` request with a string body.

### Task 3: Real Scenario
**Create a Scenario for CoinGecko API**
   - go to main page
   - get all coins
   - get coin details for this coin
   - go to coin website
   - get Historical data
### Task 4: Load Profiles
**Create Load Profiles**
   - Create different load profiles using the same scenario:
      - 10 users
      - Ramp users
      - Maximum (30 users)

### Task 5: Test results
**Create Tables in Confluence**
   - **Table 1:** Test Scenario with all steps.
   - **Table 2:** Scenario Profiles with descriptions.
   - Add the Gatling report to the Confluence page.


