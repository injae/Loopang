#!/bin/bash

python manage.py db migrate
python manage.py db upgrade
echo "db migrated"
