from model.database import db, gen_id
from datetime import datetime
from pathlib import Path
import os

MUSIC_FOLDER = os.path.expandvars('$HOME/music')


class Music(db.Model):
    __table_name__ = 'music'
    music_id = db.Column(db.String(36), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    owner = db.Column(db.String(36), db.ForeignKey('user.public_id'))
    updated_date = db.Column(db.DateTime(), default=datetime.utcnow())

    def __init__(self, name, owner):
        self.music_id = gen_id()
        self.name = name
        self.owner = owner

    def file_name(self):
        return self.owner+self.id

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
        return Music.query.all()

    @staticmethod
    def search(self, name):
        return Music.query.filter_by(name=name).first()
        
