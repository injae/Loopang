from model.database import db
import flask_bcrypt
import json


class User(db.Model):
    __table_name__ = 'user'
    public_id = db.Column(db.String(36), primary_key=True)
    email = db.Column(db.String(100), unique=True, nullable=False)
    name = db.Column(db.String(100), unique=True, nullable=False)
    password = db.Column(db.String(100), unique=True, nullable=False)

    def __init__(self, public_id, name, email, password):
        self.public_id = public_id
        self.name = name
        self.email = email
        self.password = flask_bcrypt.generate_password_hash(password).decode('utf-8')

    def check_password(self, password):
        return flask_bcrypt.check_password_hash(self.password, password)

    def is_duplicate_email(self) -> bool:
        if User.query.filter_by(email=self.email).first() is None:
            return False
        else:
            return True

    def is_duplicate_name(self) -> bool:
        if User.query.filter_by(name=self.name).first() is None:
            return False
        else:
            return True

    def public_data(self):
        return {"name": self.name}

    @staticmethod
    def search(public_id):
        return User.query.filter_by(public_id=public_id).first()
