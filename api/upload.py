from flask_restful import Resource, reqparse
from api.auth import Auth
from model.music import Music
from tools.request_message import request_message
import werkzeug


class Upload(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            parser.add_argument('name', type=str)
            parser.add_argument('file', type=werkzeug.datastructures.FileStorage, location='files')
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])
            file = args['file']
            if token is None:
                return err, 200
            if file == "":
                return request_message('fail', 'No File Found')
            else:
                music = Music(name=args['name'], owner=token.get('sub'))
                if music.save_music(file):
                    return request_message('success', 'Uploaded ' + music.name)
                else:
                    return request_message('fail', 'Is Existed file: ' + music.name)
        except Exception as e:
            return request_message('error', str(e))
