FROM locustio/locust
ADD locustfile.py /mnt/locust/locustfile.py
WORKDIR /mnt/locust
CMD ["locust", "-f", "locustfile.py"]
