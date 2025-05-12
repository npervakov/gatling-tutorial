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
**Create a Scenario for ComputerBase Api**
- go to main page
- get all computers 
- POST create a new computer
- PUT changes an existing computer
- 
### Task 4: Load Profiles
**Create Load Profiles**
   - Create different load profiles using the same scenario:
      - Regular load 
      - Find maximum 
      - Stress test 

### Task 5: Test results
**Create Tables in Confluence**
   - **Table 1:** Test Scenario with all steps.
   - **Table 2:** Scenario Profiles with descriptions.
   - Add the Gatling report to the Confluence page.

### Task 6: Home-task
**Modify computerBase scenario**
- POST create a new computer
- PUT changes an existing computer
- Add feeder to scenario  
- Add a new load profile , ramp up with 4 steps, load during 10 min and shut down 

