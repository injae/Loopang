from flask_restful import Resource, reqparse
from api.auth import Auth
from model.music import Music
import werkzeug


class Upload(Resource):
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('token', type=str)
        parser.add_argument('name', type=str)
        parser.add_argument('file', type=werkzeug.datastructures.FileStorage, location='files')
        args = parser.parse_args()
        print(args['name'])
        public_id = Auth.decord_token(args['token'])
        if public_id[0] is None:
            return public_id[1], 200
        if args['file'] == "":
            return {'status': 'fail', 'message': 'No File Found'}, 202
        
        file = args['file']
        if file:
            music = Music(name=args['name'], owner=public_id[0].get('sub'))
            if music.save_music(file):
                return {'status:': 'success', 'message': 'Uploaded' + music.name}, 200
            else:
                return {'status:': 'fail', 'message': 'Is Existed file:' + music.name}, 202
