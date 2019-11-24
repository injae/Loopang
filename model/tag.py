from model.database import db, gen_id, insert
from sqlalchemy.orm import joinedload


class Tag(db.Model):
    __table_name__ = "tag"
    name = db.Column(db.String(36), primary_key=True)

    def __init__(self, name):
        self.name = name

    def music_list(self):
        list(map(lambda l: l.music, self.tags_list))

    @staticmethod
    def generate(name):
        is_existed = Tag.query.filter_by(name=name).first()
        if is_existed is not None:
            return is_existed
        else:
            new_tag = Tag(name)
            insert(new_tag)
            return new_tag


class Tags(db.Model):
    __table_name__ = "tags"
    id = db.Column(db.Integer, primary_key=True)
    tag_id = db.Column(db.String(36), db.ForeignKey('tag.name'), nullable=False)
    tag = db.relationship("Tag", backref=db.backref("tags_list", lazy='joined', foreign_keys=[tag_id]))
    music_id = db.Column(db.String(36), db.ForeignKey('music.music_id'), nullable=False)
    music = db.relationship("Music", backref=db.backref("tags", lazy='joined', foreign_keys=[music_id]))

    def __init__(self, tag_id, music_id):
        Tag.generate(tag_id)
        self.tag_id = tag_id
        self.music_id = music_id
