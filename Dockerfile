FROM python:latest
MAINTAINER injae <8687lee@gmail.com>

RUN apt-get update
COPY ./ /usr/src/app/
WORKDIR /usr/src/app
ENV PYTHONPATH /usr/src/app
RUN pip install -r requirements.txt
RUN chmod +x startup.sh
ENTRYPOINT ["sh", "./startup.sh"]
EXPOSE 5000
