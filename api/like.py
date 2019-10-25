from flask_restful import Resource, reqparse
from model.music import Music
from model.likes import Like
from api.auth import Auth
from tools.request_message import request_message
from App import logger


class LikeMusic(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            parser.add_argument('music_id', type=str)
            parser.add_argument('like', type=bool)
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])
            if token is None: return err, 200
            user_id = token.get('sub')
            logger().debug('[upload] user_name: %s', user_id)
            music = Music.query().filter_by(music_id=args['music_id']).first()
            if music is None: return request_message("error", "can't find music")
            like = Like(user_id, music.music_id)
            if args['like']:
                like.on()
                return request_message("success", "likes on")
            else:
                like.off()
                return request_message("success", "likes off")
        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
