# TaskTeam3

## Database setup (PostgreSQL)

This project uses PostgreSQL.

Start a local PostgreSQL instance with the startup script:

```bash
./startup.sh
```

Default connection values used by the app:

- `DB_URL=jdbc:postgresql://localhost:5432/device_db`
- `DB_USERNAME=postgres`
- `DB_PASSWORD=postgres`

You can override these with environment variables before running the app.

## Start the app

After PostgreSQL is running, start the Spring Boot application.

```powershell
$env:JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"
./gradlew bootRun
```

