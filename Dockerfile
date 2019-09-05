FROM python:latest
MAINTAINER injae <8687lee@gmail.com>

RUN apt-get update
COPY ./ /usr/src/app/
WORKDIR /usr/src/app
ENV PYTHONPATH /usr/src/app
RUN pip install -r requirements.txt
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.6.0/wait /wait
RUN chmod +x /wait
RUN chmod +x startup.sh

ENTRYPOINT ["sh", "/wait && ./startup.sh"]
EXPOSE 5000
