# Coding challenge backend bootstrap
Bootstrap project for Credit Suisse Coding Challenge 

## Technologies
* Spring WebFlux
* Project Reactor
* Gradle
* MongoDB

## Requirements
* JDK 10
* gradle 4.x
* docker 18.x

## Build
Build jar: `gradle clean build`

Build jar + docker image: `gradle clean build docker`

## Launch

Running application and mongo database on docker: `docker-compose up -d`
