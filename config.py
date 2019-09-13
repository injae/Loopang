import os


user = os.getenv('POSTGRES_USER', 'loopang')
password = os.getenv('MYSQL_ROOT_PASSWORD', '1234')
host = os.getenv('DB_HOST', 'localhost')
port = '5432'
database = os.getenv('POSTGRES_DB','test_db')


DATABASE_CONNECTION_URI = f'postgresql://{user}:{password}@{host}/{database}'
#DATABASE_CONNECTION_URI = f'mysql://{user}:{password}@{host}:{port}/{database}?charset=utf8'

#user = 'loopang'
#password = '1234'
#host = 'db'
#host = 'localhost'
#port = '5432'
#database = 'test_db'
