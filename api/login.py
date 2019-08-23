from flask_restful import Resource, reqparse
from model.User import User
from api.auth import Auth


class Login(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('email',    type=str)
            parser.add_argument('password', type=str)
            args = parser.parse_args()

            email = args['email']
            password = args['password']

            db_user = User.query.filter_by(email=email).first()
            if db_user is None:
                return {'status': 'fail', 'message': 'unregistered id or wrong password'}, 200

            if db_user.check_password(password):
                return {
                    'status': 'success',
                    'message': 'login',
                    'refresh_token': Auth.encord_refresh_token(db_user.public_id).decode(),
                    'access_token': Auth.encord_access_token(db_user.public_id).decode()
                }, 200
            else:
                return {'status': 'fail', 'message': 'unregisted id or wrong password'}, 202

        except Exception as e:
            return {'status': 'error', 'message': str(e)}, 404
