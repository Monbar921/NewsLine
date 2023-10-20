FROM logstash:8.8.1

COPY ./config/ /usr/share/logstash/config/
COPY ./pipelines.yml /usr/share/logstash/config/

RUN curl https://jdbc.postgresql.org/download/postgresql-42.6.0.jar --output /usr/share/logstash/logstash-core/lib/jars/postgresql-42.6.0.jar
