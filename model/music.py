from model.database import db, gen_id
from datetime import datetime
from pathlib import Path
import os
import json

MUSIC_FOLDER = os.path.expandvars('$HOME/music')


class Music(db.Model):
    __table_name__ = 'music'
    music_id = db.Column(db.String(36), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    user_id = db.Column(db.String(36), db.ForeignKey('user.public_id'), nullable=False)
    owner = db.relationship("User", backref=db.backref("musics", lazy='dynamic', foreign_keys=[user_id]))
    updated_date = db.Column(db.DateTime(), default=datetime.utcnow())
    downloads = db.Column(db.Integer)

    def __init__(self, name, user_id):
        self.music_id = gen_id()
        self.name = name
        self.user_id = user_id 
        self.downloads = 0

    def file_name(self):
        return self.user_id+self.music_id

    def path(self):
        return os.path.join(MUSIC_FOLDER, self.file_name())

    def save_music(self, file):
        path = self.path()
        if not os.path.exists(MUSIC_FOLDER):
            os.makedirs(MUSIC_FOLDER)
        if Path(path).exists():
            return False
        else:
            file.save(path)
            return True

    def music_list(self):
        return list(map(lambda l: l.public_data(), Music.query.all()))

    def public_data(self):
        return { 'name': self.name, 'owner': self.owner.name
               , 'updated_date': str(self.updated_date), 'downloads': self.downloads }

    @staticmethod
    def track_list(user_id):
        return list(map(lambda l: l.public_data(), Music.query.filter_by(user_id=user_id)))

    @staticmethod
    def search(name):
        return list(map(lambda l: l.public_data(), Music.query.filter_by(name=name)))
