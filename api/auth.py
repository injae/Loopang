from flask_jwt import jwt
from flask_restful import Resource, reqparse
import datetime


secret_key = 'loopang'


class Auth(Resource):
    def get(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token',    type=str)
            args = parser.parse_args()

            pub_id = Auth.decord_token(args['token']).get('sub')
            token = Auth.encord_access_token(pub_id)
            return {'status': 'success', 'message': 'refreshed token', 'token': token.decode()}, 200
        except jwt.ExpiredSignatureError:
            return {'status': 'fail', 'message': 'expired signature'}, 200
        except jwt.InvalidTokenError:
            return {'status': 'fail', 'message': 'invalid token'}, 200
        except Exception as e:
            return {'status': 'error', 'message': str(e)}, 404

    @staticmethod
    def encord_token(pub_id, exp: datetime):
        payload = {
            'exp': datetime.datetime.utcnow() + exp,
            'iat': datetime.datetime.utcnow(),
            'sub': pub_id
        }
        return jwt.encode(payload, secret_key, algorithm='HS256')

    @staticmethod
    def encord_refresh_token(pub_id):
        return Auth.encord_token(pub_id, datetime.timedelta(hours=2))

    @staticmethod
    def encord_access_token(pub_id):
        return Auth.encord_token(pub_id, datetime.timedelta(minutes=10))

    @staticmethod
    def decord_token(token):
        try:
            return jwt.decode(token, secret_key)
        except jwt.ExpiredSignatureError:
            raise jwt.ExpiredSignatureError
        except jwt.InvalidTokenError:
            raise jwt.InvalidTokenError
