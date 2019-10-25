from flask_sqlalchemy import SQLAlchemy
import uuid


db = SQLAlchemy()


def gen_id():
    return str(uuid.uuid4())

def insert(data):
    db.session.add(data)
    db.session.commit()

def remove(data):
    db.session.remove(data)
    db.session.commit()

def update():
    db.session.commit()
