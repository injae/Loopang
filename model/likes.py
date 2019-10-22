from model.database import db
from model.User import User
from model.music import Music


class Likes(db.Model):
    __table_name__ = 'likes'
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.String(36), db.ForeignKey('user.public_id'))
    owner = db.relationship("User", backref=db.backref("user", lazy='dynamic'))
    music_id = db.Column(db.String(36), db.ForeignKey('music.music_id'))
    music = db.relationship("Music", backref=db.backref("music", lazy='dynamic'))

    def __init__(self, user_id, music_id):
        self.user_id = user_id
        self.music_id = music_id

    def can_likes(self):
        is_find = Likes.query.filter_by(user_id=self.user_id, music_id=self.music_id).first()
        return True if is_find is not None else False

    def off(self):
        is_find = Likes.query.filter_by(user_id=self.user_id, music_id=self.music_id).first()
        if is_find is not None:
            db.session.delete(self)
            db.session.commit()

    def on(self):
        if self.can_likes():
            db.session.add(self)
            db.session.commit()
            return True
        else:
            return False


    @staticmethod 
    def music_list(user_id):
        return list(map(lambda l: l.music.public_data(), Likes.query.filter_by(user_id=user_id)))

    @staticmethod 
    def user_list(music_id):
        return list(map(lambda l: l.owner.public_data(), Likes.query.filter_by(music_id=music_id)))
