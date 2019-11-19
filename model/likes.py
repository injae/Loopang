from model.database import db, insert, update, remove
from model.music import Music


class Like(db.Model):
    __table_name__ = 'like'
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.String(36), db.ForeignKey('user.public_id'), nullable=False)
    owner = db.relationship("User", backref=db.backref("user_likes", lazy='dynamic', foreign_keys=[user_id]))
    music_id = db.Column(db.String(36), db.ForeignKey('music.music_id'), nullable=False)
    #music = db.relationship("Music", backref=db.backref("music_likes", lazy='dynamic', foreign_keys=[music_id]))
    music = db.relationship("Music", foreign_keys=[music_id])

    def __init__(self, user_id, music_id):
        self.user_id = user_id
        self.music_id = music_id

    def can_likes(self):
        is_find = Like.query.filter_by(user_id=self.user_id, music_id=self.music_id).first()
        return False if is_find is not None else True

    def off(self):
        is_find = Like.query.filter_by(user_id=self.user_id, music_id=self.music_id).first()
        if is_find is not None:
            is_find.music.likes -= 1
            remove(is_find)

    def on(self):
        is_find = Like.query.filter_by(user_id=self.user_id, music_id=self.music_id).first()
        if is_find is None:
            self.music.likes += 1
            insert(self)
            return True
        else:
            return False

    def public_data(self):
        return self.music.public_data()

    @staticmethod 
    def music_list(user_id):
        return list(map(lambda l: l.music.public_data(), Like.query.filter_by(user_id=user_id)))

    @staticmethod 
    def user_list(music_id):
        return list(map(lambda l: l.owner.public_data(), Like.query.filter_by(music_id=music_id)))
