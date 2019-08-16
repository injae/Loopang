#!/bin/bash

python manage.py db init
python manage.py db migrate
python manage.py db upgrade
python App.py
echo "app run"
