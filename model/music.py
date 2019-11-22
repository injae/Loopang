from model.database import db, gen_id, insert
from model.tag import Tags
from sqlalchemy.ext.hybrid import hybrid_property
from sqlalchemy import func
from datetime import datetime
from pathlib import Path
import os

MUSIC_FOLDER = os.path.expandvars('$HOME/music')


class Music(db.Model):
    __table_name__ = 'music'
    music_id = db.Column(db.String(36), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(100), nullable=False)
    user_id = db.Column(db.String(36), db.ForeignKey('user.public_id'), nullable=False)
    owner = db.relationship("User", backref=db.backref("musics", lazy='dynamic', foreign_keys=[user_id]))
    updated_date = db.Column(db.DateTime(), default=datetime.utcnow())
    downloads = db.Column(db.Integer, default=0, nullable=False)
    likes = db.Column(db.Integer, default=0, nullable=False)

    @hybrid_property
    def num_likes(self):
        return func.count(self.music_likes)

    def __init__(self, name, user_id, description):
        self.music_id = gen_id()
        self.name = name
        self.description = description
        self.user_id = user_id

    def set_tags(self, tags):
        for tag in tags:
            insert(Tags(tag, self.music_id))

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

    def tag_list(self):
        return list(map(lambda l: "{}".format(l.tag.name), self.tags))

    def public_data(self):
        return {'id': self.music_id, 'name': self.name, 'owner': self.owner.name 
               ,'updated_date': str(self.updated_date), 'downloads': self.downloads
               ,'likes': self.likes, 'explanation': self.description
               ,'tags': self.tag_list()}
    @staticmethod
    def track_list(user_id):
        return list(map(lambda l: l.public_data(), Music.query.filter_by(user_id=user_id)))

    @staticmethod
    def search(name):
        return list(map(lambda l: l.public_data(), Music.query.filter_by(name=name)))
