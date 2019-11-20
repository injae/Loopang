from flask import Flask
from flask_restful import Api
from config import DATABASE_CONNECTION_URI
from api.auth import secret_key
from flask.logging import default_handler
import logging


app = None


def App():
    global app
    if app is not None: return app
    app = Flask('Loopang')
    logging.basicConfig(level=logging.INFO)
    app.config['SECRET_KEY'] = secret_key
    app.config['DEBUG'] = True
    app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_CONNECTION_URI
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
    from model.database import db
    db.init_app(app)

    api = Api(app)
    from api.signup import SignUp
    api.add_resource(SignUp, '/auth/sign-up')
    from api.login import Login
    api.add_resource(Login, '/auth/login')
    from api.auth import Auth
    api.add_resource(Auth, '/auth/refresh')
    from api.upload import Upload
    api.add_resource(Upload, '/file/upload')
    from api.download import Download
    api.add_resource(Download, '/file/download')
    from api.user_info import UserInfo
    api.add_resource(UserInfo, '/info/user')
    from api.search_music import MusicSearch
    api.add_resource(MusicSearch, '/music/search')
    from api.like import LikeMusic
    api.add_resource(LikeMusic, '/like/request')
    from api.feed import Feed
    api.add_resource(Feed, '/info/feed')

    return app


def logger():
    return App().logger


if __name__ == '__main__':
    app = App()
    app.run(host='0.0.0.0', debug=True, ssl_context=('cert.pem', "key.pem"))
