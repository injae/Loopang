from model.database import db, gen_id
from pathlib import Path
import os

MUSIC_FOLDER = "../music"


class Music(db.Model):
    __table_name__ = 'music'
    id = db.Column(db.String(36), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    owner = db.Column(db.String(36), db.ForeignKey('user.public_id'))

    def __init__(self, name, owner):
        self.id = gen_id()
        self.name = name
        self.owner = owner

    def file_name(self):
        return self.owner+self.id

    def path(self):
        return os.path.join(MUSIC_FOLDER, self.file_name())

    def save_music(self, file):
        path = self.path()
        if Path(path).exists():
            return False
        else:
            file.save(path())
            return True
