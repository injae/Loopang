from flask_sqlalchemy import SQLAlchemy
import uuid


db = SQLAlchemy()


def gen_id():
    return str(uuid.uuid4())
