from flask_restful import Resource, reqparse
from model.music import Music
from api.auth import Auth
from tools.request_message import request_message
from App import logger


class MusicSearch(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            parser.add_argument('parameter', type=str)
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])
            if token is None: return err, 200
            user = token.get('sub')
            logger().debug('[upload] user_name: %s', user)
            logger().debug('[upload] parameter: %s', args['parameter'])
            return {
                'status': 'success',
                'message': 'user-info',
                'searchResult': list(map(lambda music: music.public_data(), Music.search(args['parameter'])))
            }, 200

        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
