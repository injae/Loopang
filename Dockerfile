FROM python:latest
MAINTAINER injae <8687lee@gmail.com>

RUN apt-get update
COPY ./ /app/
WORKDIR /app
ENV PYTHONPATH /app
RUN /bin/bash -c "source venv/bin/activate"
RUN pip install -r requirements.txt
RUN chmod +x startup.sh

ENTRYPOINT ["sh", "./startup.sh"]
EXPOSE 5000
