# World Cup 2026 Backend

A production-grade Spring Boot 3 backend for the World Cup 2026 application, serving as the single source of truth for match data, team information, and group standings.

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.0**
- **Gradle**
- **PostgreSQL 16**
- **Spring Data JPA**
- **Spring RestClient**
- **Caffeine Cache**
- **Docker**

## Features

- RESTful API for matches, teams, and group standings
- Intelligent caching strategy to minimize Football-Data.org API calls
- Clean architecture with separation of concerns
- Comprehensive exception handling and validation
- Docker support for easy deployment
- Health checks and monitoring via Spring Actuator

## Project Structure

```
com.worldcup2026
├── controller      # REST API endpoints
├── service         # Business logic with caching
├── repository      # Spring Data JPA repositories
├── entity          # JPA entities
├── dto             # Data Transfer Objects
├── client          # External API clients (Football-Data.org)
├── config          # Configuration classes
└── exception       # Custom exceptions and global handler
```

## API Endpoints

### Matches
- `GET /api/matches` - Get all matches
- `GET /api/matches/{id}` - Get match by ID

### Teams
- `GET /api/teams` - Get all teams
- `GET /api/teams/{id}` - Get team by ID

### Groups
- `GET /api/groups` - Get all group standings
- `GET /api/groups/{groupName}` - Get standings for a specific group

## Caching Strategy

The application uses a multi-layer caching strategy:

1. **Database First**: Check PostgreSQL for cached data
2. **Freshness Check**: Determine if data is stale based on TTL
3. **API Refresh**: If stale, fetch from Football-Data.org and update database
4. **Return Data**: Serve fresh data from database

Cache TTLs:
- Matches: 15 minutes
- Standings: 1 hour
- Teams: 24 hours

## Environment Variables

Required environment variables:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/worldcup
DATABASE_USERNAME=worldcup
DATABASE_PASSWORD=worldcup
FOOTBALL_DATA_API_KEY=your_api_key_here
SERVER_PORT=8080
```

## Running with Docker

### Using Docker Compose

1. Set your Football-Data.org API key:
```bash
export FOOTBALL_DATA_API_KEY=your_api_key_here
```

2. Start the services:
```bash
docker-compose up -d
```

3. Check health:
```bash
curl http://localhost:8080/actuator/health
```

### Building the Docker Image

```bash
docker build -t world-cup-backend .
```

## Running Locally

### Prerequisites
- Java 21
- PostgreSQL 16
- Gradle 8.x

### Steps

1. Configure PostgreSQL:
```sql
CREATE DATABASE worldcup;
CREATE USER worldcup WITH PASSWORD 'worldcup';
GRANT ALL PRIVILEGES ON DATABASE worldcup TO worldcup;
```

2. Set environment variables:
```bash
export FOOTBALL_DATA_API_KEY=your_api_key_here
```

3. Run the application:
```bash
./gradlew bootRun
```

## Database Schema

### Teams
- `id` (Primary Key)
- `external_api_id` (Unique)
- `name`
- `short_name`
- `tla`
- `crest_url`
- `created_at`
- `updated_at`

### Matches
- `id` (Primary Key)
- `external_api_id` (Unique)
- `home_team_id` (Foreign Key)
- `away_team_id` (Foreign Key)
- `utc_date`
- `status`
- `home_score`
- `away_score`
- `last_updated`
- `created_at`

### Group Standings
- `id` (Primary Key)
- `group_name`
- `team_id` (Foreign Key)
- `position`
- `played_games`
- `wins`
- `draws`
- `losses`
- `goals_for`
- `goals_against`
- `goal_difference`
- `points`
- `last_updated`
- `created_at`

## Health Checks

The application exposes health endpoints:

- `GET /actuator/health` - Application health
- `GET /actuator/info` - Application info
- `GET /actuator/metrics` - Application metrics

## Development

### Running Tests

```bash
./gradlew test
```

### Building the JAR

```bash
./gradlew bootJar
```

The JAR will be created at `build/libs/world-cup-backend.jar`.

## Production Deployment

For production deployment:

1. Use environment-specific configuration files
2. Enable the `prod` Spring profile
3. Configure proper database connection pooling
4. Set up monitoring and alerting
5. Use HTTPS for all API endpoints
6. Implement rate limiting
7. Set up proper logging aggregation

## License

This project is for educational purposes.
