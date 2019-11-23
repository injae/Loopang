#!/bin/bash

#python manage.py db init
python manage.py db migrate
python manage.py db upgrade
/bin/bash -c "source venv/bin/activate"
python App.py
echo "app run"
