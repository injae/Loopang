from flask import Flask
from flask_restful import Api
from config import DATABASE_CONNECTION_URI
from api.auth import secret_key


def create_app():
    app = Flask('Loopang')
    app.config['SECRET_KEY'] = secret_key
    app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE_CONNECTION_URI
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
    from model.database import db
    db.init_app(app)

    api = Api(app)
    from api.signup import SignUp
    api.add_resource(SignUp, '/sign-up')
    from api.login import Login
    api.add_resource(Login, '/login')
    from api.auth import Auth
    api.add_resource(Auth, '/auth')
    from api.upload import Upload
    api.add_resource(Upload, '/upload')
    from api.download import Download
    api.add_resource(Download, '/download')
    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', debug=True, ssl_context=('cert.pem', "key.pem"))
