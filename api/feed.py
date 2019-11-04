from flask_restful import Resource, reqparse
from model.music import Music
from api.auth import Auth
from tools.request_message import request_message, make_data
from App import logger


class Feed(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            args = parser.parse_args()
            (token, err) = Auth.decord_token(args['token'])
            if token is None: return err, 200
            user_id = token.get('sub')
            logger().debug('[upload] user_name: %s', user_id)
            return {
                "status": "success",
                "message": "feed data",
                "recent_musics": make_data(Music.query.order_by(Music.updated_date).limit(5)),
                "likes_top": make_data(),
                "likes_top": make_data(Music.query.order_by(Music.num_likes).limit(5)),
                "download_top": make_data(Music.query.order_by(Music.downloads).limit(5))                
            }, 200
        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
