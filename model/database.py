from flask_sqlalchemy import SQLAlchemy
import uuid


db = SQLAlchemy()


def gen_id():
    return str(uuid.uuid4())


def insert(data):
    db.session.add(data)
    db.session.commit()


def remove(data):
    db.session.delete(data)
    db.session.commit()


def update():
    db.session.commit()


def paging(query, page=0, page_size=None):
    if page_size:
        query = query.limit(page_size)
    if page:
        query = query.offset(page*page_size)
    return query
