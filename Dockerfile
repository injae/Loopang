FROM python:latest
MAINTAINER injae <8687lee@gmail.com>

RUN apt-get update
ADD ./* /usr/src/app/
RUN pip install -r /usr/src/app/requirements.txt
RUN chmod +x ./usr/src/app/startup.sh
ENTRYPOINT ["./usr/src/app/startup.sh"]
EXPOSE 5000
