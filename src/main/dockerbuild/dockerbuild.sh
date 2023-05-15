#!/bin/bash

service postgresql start
sudo -u postgres psql -a -f src/main/database/init_database.sql
sudo -u postgres psql -U postgres -d newsline -c "alter user postgres with password 'postgres';"
mvn spring-boot:run
