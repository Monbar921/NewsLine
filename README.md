# NewsLine

## Hello, I am Denis Kornilovich and it is my test task.

### Firstly, you have to unpack zip to any folder. 
### Then, to run this application, you have 2 choices:

### 1. Use Docker (simple)
Just put following code to you terminal inside of root folder project (Newsline) and wait around 3 minutes while docker container and application inside its will be deployed. Instead of POSTGRES_USER and POSTGRES_PASSWORD use your own examples (or you can keep it as default newsline and newsline):
``` 
docker build --tag 'newsline' .
docker run -p 8585:8080 -it --detach 'newsline'
```

If you have UNIX-like system, you can run bash script, which runs 2 commands above, but previously change POSTGRES_USER and POSTGRES_PASSWORD in Docker/docker_start.sh:
``` 
./Docker/docker_start.sh
```

Open your web browser and go to address (if your 8585 port is busy, then go to Dockerfile and change mapped ports to another one)
``` 
http://localhost:8585/
```

If you want to delete docker container and image and you have UNIX-like system, you can run bash script, which will do this:
``` 
./Docker/docker_remove.sh
```
### 2. Manually deploying (hard)
#### 2.1 Create database 'newsline' in postgresql database:
``` 
CREATE DATABASE newsline;
```
#### 2.2 Create table 'news':
``` 
CREATE TABLE IF NOT EXISTS news(
    id serial NOT NULL,
    title varchar NOT NULL,
    date date NOT NULL,
    text varchar NOT NULL,
    image BYTEA
);
```

First 2 step present in sql file which is located in:
``` 
src/main/database/init_database.sql
```

#### 2.3 Create a new user in your database and give him all permissions:
``` 
    CREATE USER $POSTGRES_USER WITH PASSWORD $PASSWORD;
    GRANT CONNECT ON DATABASE "newsline" to $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE "newsline" to $POSTGRES_USER;
    GRANT pg_read_all_data to $POSTGRES_USER;
    GRANT pg_write_all_data to $POSTGRES_USER;
```

#### 2.4 Write password and username from previous step in 'spring.datasource.password' and 'spring.datasource.username' fields. It is located in:
``` 
src/main/resources/application.properties
```

#### 2.5 Run application
``` 
mvn spring-boot:run
```

### How to use. Guide

In the main page you can see 2 hyperlinks. If you want to add news, please, click to "Добавить новость". If you want to see news, please, click to "Смотреть новости".
Also, you can always return to the main page by clicking "На главную", which is located below. 

You can choose the maximum number of news displayed on page (10, 20 or 50). This drop-down list is also located below. After selecting the desired number, press "Выбрать" button.

Add news page consist of 3 text required and 1 optional fields. You have to write some text to "Заголовок" and "Содержимое" fields. The program checks these for emptiness and only spaces and gives error message if it exists.
The date must be written in the 'yyyy-MM-dd' format only. An error message is also sent if you make a mistake.

Image is optional therefore you might not click "Выберите файл" button. It will be mean that image will not be shown on the newsline page.
When you will click on "Добавить" button, you can see message "Вы успешно добавили новость!". It means that news was added successfully. If you see the message "Новость с таким заголовком и датой уже существует!" you need to change date or title or body of your news because it is already exists. 

### Something about project structure
**Docker** folder contains Dockerfile which runs this application in container and bash scripts to run and remove Docker container/image from your system.

**main** folder contains following folders:
- **database** folder contains .sql file which create database and table for this app;
- **dockerbuild** folder contains bash script for setting database user login and password to this app. These data comes from command line when user run container and set environment variables POSTGRES_USER and POSTGRES_PASSWORD. This script also runs postgres server and create database and table for app;
- **java** folder contains source java files for this project;
- **resources** folder contains **templates** folder with html pages used in this project and property file for Spring.