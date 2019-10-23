from flask_restful import Resource, reqparse
from model.User import User
from model.likes import Like
from model.music import Music
from api.auth import Auth
from tools.request_message import request_message
from App import logger
import json


class UserInfo(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])
            if token is None: return err, 200
            user = User.search(token.get('sub'))
            logger().debug('[upload] user_name: %s', user.name)

            return {
                'status': 'success',
                'message': 'user-info',
                'nickName': user.name,
                'trackList': json.dumps(Music.track_list(user.public_id)),
                'likedList': json.dumps(Like.music_list(user.public_id))
            }, 200

        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
