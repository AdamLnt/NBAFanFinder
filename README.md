# NBAFanFinder

NBAFanFinder is a website that helps you find other NBA fans near you. You can find them and chat with them.

This project is a 3-tier architecture project composed of:

1. A frontend
2. A backend
3. And Databases

This project uses different technologies:

| **Part**     | Technologies                                                                                                |
| ------------ | ----------------------------------------------------------------------------------------------------------- |
| **Backend**  | [Java Spring Boot](https://spring.io/projects/spring-boot), [Maven](https://maven.apache.org/)              |
| **Frontend** | [React](https://react.dev), [Vite](https://vitejs.fr), [TypeScript](https://www.typescriptlang.org)         |

## Prerequisites

Install the following resources:

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-0-13-later-archive-downloads.html)
- [Node 20](https://nodejs.org)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (for the full stack and SonarQube)
- [pre-commit](https://pre-commit.com/) (for the git hooks)

## Quick start with Docker

The simplest way to run the entire stack (MySQL + Backend + Frontend):

```bash
docker compose up --build
```

- Frontend → http://localhost
- Backend  → http://localhost:8080

To stop:

```bash
docker compose down       # keeps the data
docker compose down -v    # wipes the MySQL volume too
```

## Local development

### Backend

```bash
cd Backend
./mvnw spring-boot:run
```

Configure the database in `Backend/src/main/resources/application.properties` if not using docker compose.

### Frontend

```bash
cd Frontend
npm install
npm run dev
```

## Tests & coverage

### Backend (JUnit + JaCoCo)

```bash
cd Backend
./mvnw verify                      # runs tests + generates JaCoCo report
```

Coverage report → `Backend/target/site/jacoco/index.html`

### Frontend (Vitest)

```bash
cd Frontend
npm run test                    # one-shot
npm run test:watch              # watch mode
npm run test:coverage           # with coverage
```

Coverage report → `Frontend/coverage/index.html`

## Lint & format

### Backend (Spotless)

```bash
cd Backend
./mvnw spotless:check           # checks formatting
./mvnw spotless:apply           # auto-fixes
```

### Frontend (ESLint + Prettier)

```bash
cd Frontend
npm run lint                    # ESLint
npm run format:check            # Prettier check
npm run format                  # Prettier auto-fix
```

## Pre-commit hooks

Install the hooks once:

```bash
pre-commit install
```

The hooks will then run on every `git commit`:

- trim trailing whitespace, fix end-of-files, validate YAML
- gitleaks (secret detection)
- ESLint + Prettier on the Frontend
- Spotless on the Backend

To run them manually on every file:

```bash
pre-commit run --all-files
```

## SonarQube (local)

Start a local SonarQube instance:

```bash
docker compose -f docker-compose.sonar.yml up -d
```

Wait ~1 minute, then open http://localhost:9000 (default login: `admin` / `admin` — you'll be prompted to change it).

### First scan

1. In the SonarQube UI, create a project with key `NBAFanFinder` and generate a token.
2. Run the tests so coverage reports exist:

   ```bash
   cd Backend && ./mvnw verify && cd ..
   cd Frontend && npm run test:coverage && cd ..
   ```

3. Run the scan from the project root.

   > ℹ️ For SonarQube **9.x** (`lts-community`) use `sonar.login=<token>`. For SonarQube **10+** use `sonar.token=<token>`.

   Linux / macOS / Git Bash:

   ```bash
   docker run --rm \
     --network host \
     -v "$(pwd):/usr/src" \
     sonarsource/sonar-scanner-cli \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.login=<YOUR_TOKEN>
   ```

   Windows PowerShell (note: quotes around each `-D` flag — PowerShell otherwise splits on `.` — and `host.docker.internal` instead of `localhost` since `--network host` is unreliable on Docker Desktop):

   ```powershell
   docker run --rm -v "${PWD}:/usr/src" sonarsource/sonar-scanner-cli `
     "-Dsonar.host.url=http://host.docker.internal:9000" `
     "-Dsonar.login=<YOUR_TOKEN>"
   ```

4. Open the project in SonarQube to read the report and fix code smells.

To stop SonarQube:

```bash
docker compose -f docker-compose.sonar.yml down
```

## Contributing — Pull Request flow

1. Branch off `main` (e.g. `feature/my-feature`).
2. Make your changes — pre-commit will keep formatting clean.
3. Make sure tests pass:
   - `cd Backend && ./mvnw verify`
   - `cd Frontend && npm run lint && npm run test`
4. Push and open a PR against `main`.
5. CI will run automatically: tests, coverage, Docker build, Trivy scan, SonarQube scan.
6. The PR can only be merged once CI is green and the SonarQube Quality Gate passes.

## Project structure

```
NBAFanFinder/
├── Backend/                    # Spring Boot app (Java 17, Maven)
├── Frontend/                   # React + Vite + TypeScript
├── docker-compose.yml          # full stack (mysql + backend + frontend)
├── docker-compose.sonar.yml    # local SonarQube
├── sonar-project.properties    # Sonar scanner config
├── .pre-commit-config.yaml     # git hooks
└── .github/
    ├── workflows/ci.yaml       # CI/CD pipeline
    └── dependabot.yml          # automated dependency updates
```
