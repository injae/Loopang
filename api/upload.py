from flask_restful import Resource, reqparse
from api.auth import Auth
from model.music import Music
from tools.request_message import request_message
from App import logger
from model.database import db, insert
import werkzeug


class Upload(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            parser.add_argument('name', type=str)
            parser.add_argument('explanation', type=str)
            parser.add_argument('tags', type=str, action='append')
            parser.add_argument('file', type=werkzeug.datastructures.FileStorage, location='files')
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])

            if token is None:
                return err, 200

            name = args['name']
            file = args['file']
            desc = args['explanation']
            tags = args['tags']
            user_id = token.get('sub')
            logger().debug('[upload] owner: %s', user_id)
            logger().debug('[upload] file: %s', name)
            logger().debug('[upload] desc: %s', name)
            logger().debug('[upload] tags: %s', name)

            if file == "":
                return request_message('fail', 'No File Found')
            else:
                music = Music(name=name, user_id=user_id, description=desc)
                if music.save_music(file):
                    insert(music)
                    music.set_tags(tags)
                    return request_message('success', 'Uploaded ' + music.music_name)
                else:
                    return request_message('fail', 'Is Existed file: ' + music.music_name)
        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
