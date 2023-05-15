# NewsLine

## Hello, I am Denis Kornilovich and it is my test task.

### Firstly, you have to unpack zip to any folder. 
### Then, to run this application, you have 2 choice:

### 1. Use Docker. 
Just put following code to you terminal inside of root folder project (Newsline) and wait around 3 minutes while docker container and application inside its will be deployed.
``` 
docker build --tag 'newsline'
docker run -p 8585:8080 -it --detach 'newsline'
```
Open your web browser and go to address (if your 8585 port is busy, then go to Dockerfile and change mapped ports to another one)
``` 
http://localhost:8585/
```