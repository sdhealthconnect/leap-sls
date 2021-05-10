
# LEAP Security Labeling Service (LEAP-SLS)
This repository contains the artifacts for services that process the structured portion of clinical record to identify 
items that may be sensitive in nature.
  It sources the VSAC valueset jointly created by Dept. of Veteran Affairs (VA) and Substance Abuse and Mental 
  Health Services Administration (SAMHSA) to make these determinations. 
  These services are utilized by LEAP demonstrations for HL7 V2 messaging and HL7 CCDA eHealth Exchange. Supports FHIR based exchanges, and are also accessed by the
   the LEAP Consent UI where a patient may analyze their clinical record to determine if privacy sensitivities exist and act on them when 
   creating a FHIR consent.
   
   **Note:** This implementation is based on the HL7 Healthcare Classification System (HCS) specification.   It leverages the foundational work done over a 10 year period at Dept. of Veteran Affairs former Emerging Health Technology Advancement Center (EHTAC).

## Prerequisites
- OpenJDK 11.0.6_10
- Maven 3.6.x
- Docker 19.03.5 and Docker Compose 1.25.2 


## Build Instructions
-  Clone this repository and change to the repository directory:
```
> git clone https://github.com/sdhealthconnect/leap-sls.git
> cd leap-sls
```

- Build the project using `maven`:
```
> mvn clean install -DskipTests
```
- Build the Docker leap-sls-service container:
Note: Docker must be running
```
> cd leap-sls-service
> docker build .
```

## Run Instructions
From within leap-sls-service directory
```
> cd leap-sls-service
```
- Make necessary changes to docker-compose.yml to best suit your environment.
```
version: '3.6'

leap-mysql:
  image: mysql:latest
  container_name: leap-mysql
  restart: always
  environment:
    MYSQL_DATABASE: 'leap'
    MYSQL_USER: 'admin'
    MYSQL_PASSWORD: 'admin'
    MYSQL_ROOT_PASSWORD: 'admin'
  ports:
    - 3308:3306
  volumes:
    - leap-mysql:/var/lib/mysql
services:
  leap-sls-service:
    image: leap-sls-service
    container_name: leap-sls-service
    build:
      context: .
      dockerfile: Dockerfile
    environment:
      LEAP_DB_PASS: ${LEAP_DB_PASS}
      LEAP_DB_USER: ${LEAP_DB_USER}
      LEAP_DB_URL: ${LEAP_DB_URL}
    ports:
      - 9091:9091
volumes:
  leap-mysql:
```

- Following environment variables must be set:
```
export LEAP_DB_URL=jdbc:mysql://leap-mysql:3306/leap?createDatabaseIfNotExist=true&autoReconnect=true&allowPublicKeyRetrie
val=true&useSSL=false
export LEAP_DB_USER=admin
export LEAP_DB_PASS=admin
```
- Launch Docker Containers
```
> docker-compose up
```
If you are running Docker locally you can visually validate that all have started by launching the Dashboard.  

You can also check this via the command line:
```
> docker stats
```
2 containers should be running leap-mysql and leap-sls-service

You can test the SLS by accessing its openapi interface @ http://localhost:9091/swagger-ui.html.  

