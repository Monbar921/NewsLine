# NewsLine

## Hello, I am Denis Kornilovich and it is my test task.

### Firstly, you have to unpack zip to any folder. 
### Then, to run this application, you have 2 choices:

### 1. Use Docker (simple)
Just put following code to you terminal inside of root folder project (Newsline) and wait around 3 minutes while docker container and application inside its will be deployed.
``` 
docker build --tag 'newsline'
docker run -p 8585:8080 -it --detach 'newsline'
```
Open your web browser and go to address (if your 8585 port is busy, then go to Dockerfile and change mapped ports to another one)
``` 
http://localhost:8585/
```
### 2. Manually deploying (hard)
#### 2.1 Create database 'newsline' in postgresql server:
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

First 2 step you can execute as follows in one command (you are in project root folder):
``` 
sudo -u postgres psql -a -f src/main/database/init_database.sql
```

#### 2.3 Change password of 'postgres' user:
``` 
psql -U postgres -d newsline -c "alter user postgres with password 'postgres';"
```

#### 2.4 Write password from previous step in 'spring.datasource.password' field. It is located in:
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