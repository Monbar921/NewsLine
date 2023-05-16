#!/bin/bash

service postgresql start
sudo -u postgres psql -a -f src/main/database/init_database.sql

PASSWORD="'"$POSTGRES_PASSWORD"'"
if [ "$POSTGRES_USER" = "postgres" ]; then
  sudo -u postgres psql -U postgres -d newsline -c "alter user postgres with password $PASSWORD;"
else
  sudo -u postgres psql -c "CREATE USER $POSTGRES_USER WITH PASSWORD $PASSWORD;"
  sudo -u postgres psql -c "GRANT CONNECT ON DATABASE "newsline" to $POSTGRES_USER;"
  sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE "newsline" to $POSTGRES_USER;"
  sudo -u postgres psql -c "GRANT pg_read_all_data to $POSTGRES_USER;"
  sudo -u postgres psql -c "GRANT pg_write_all_data to $POSTGRES_USER;"
fi

sed -i -e "s/POSTGRES_USER/$POSTGRES_USER/g" src/main/resources/application.properties
sed -i -e "s/POSTGRES_PASSWORD/$POSTGRES_PASSWORD/g" src/main/resources/application.properties

mvn spring-boot:run