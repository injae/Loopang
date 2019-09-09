import os


#user = os.environ['MYSQL_USER']
#password = os.environ['MYSQL_ROOT_PASSWORD']
#host = os.environ['MYSQL_HOST']
#database = os.environ['MYSQL_DATABASE']
#port = os.environ['MYSQL_PORT']

user = 'loopang'
password = '1234'
host = 'db'
#host = 'localhost'
port = '5432'
database = 'test_db'


DATABASE_CONNECTION_URI = f'postgresql://{user}:{password}@{host}/{database}'
#DATABASE_CONNECTION_URI = f'mysql://{user}:{password}@{host}:{port}/{database}?charset=utf8'
