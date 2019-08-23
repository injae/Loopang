from flask_sqlalchemy import SQLAlchemy
from App import create_app
import uuid


db = SQLAlchemy()


def gen_id():
    return str(uuid.uuid4())
