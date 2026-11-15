# MatchIQ ⚽

MatchIQ is a full-stack soccer analytics app I built to explore Premier League data and practice building applications with Java, Spring Boot, and React.

The app lets users compare teams, view match statistics, and get AI-generated match analysis based on recent performance and head-to-head results.

## Features

- View statistics and results from 380 Premier League matches.
- Compare teams based on recent form and head-to-head records.
- Generate match analysis using the OpenAI API.
- User signup and login with JWT authentication.

## Tech Stack

- **Backend:** Java, Spring Boot, Spring Data JPA
- **Frontend:** React, JavaScript, CSS
- **Database:** PostgreSQL
- **APIs:** API-Football, OpenAI
- **Deployment:** AWS EC2, Amazon RDS, Nginx, HTTPS

## Deployment

The React frontend and Spring Boot backend are hosted on AWS EC2, with PostgreSQL running on Amazon RDS. I used Nginx to serve the frontend and route API requests to Spring Boot, and Let's Encrypt for HTTPS.

## Live Demo

[Try MatchIQ](https://matchiq.duckdns.org)