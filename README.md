# NBAFanFinder
NBAFanFinder is a website that helps you find other NBA fans near you. You can find them and chat with them.

This project is a 3-tier architecture project composed of:
1. A frontend
2. A backend
3. And Databases

This project uses different technologies:
| **Part** | Technologies |
| ----------- | ----------- |
| **Backend** | [Java Spring Boot](https://spring.io/projects/spring-boot), [Maven](https://maven.apache.org/) |
| **Frontend** | [React](https://react.dev), [Vite](https://vitejs.fr), [TypeScript](https://www.typescriptlang.org) |


## Prerequisites

Install all the following resources:
- [JDK](https://www.oracle.com/java/technologies/javase/jdk17-0-13-later-archive-downloads.html)
- [SDKMAN!](https://sdkman.io)
- [Node](https://nodejs.org)
- [NVM](https://www.nvmnode.com)

### IDEs
Note : those are only suggestions, it's a matter of preference
| Backend | Frontend |
| ----------- | ----------- |
| [Eclipse](https://eclipseide.org) | [Visual Studio Code](https://code.visualstudio.com) |

### Database
- Any local database provider such as [XAMPP](https://www.apachefriends.org/fr/index.html)

## Cloner the project
```bash
  git clone https://github.com/AdamLnt/NBAFanFinder.git
  cd NBAFanFinder
```

### Backend

Configure the database in `src/main/resources/application.properties`:
```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/NBAFanFinder
  spring.datasource.username=username
  spring.datasource.password=password
```

Go to the Backend folder:
```bash
  cd Backend
```

You can either launch it with this command :
```bash
  ./mvnw spring-boot:run
```
Or by clicking the Run button on the IDE

### Frontend

Go to the Frontend folder:

And run the following commands
```bash
  cd Frontend
  npm install
  npm run dev
```

To open the web page:
```bash
  o
```
