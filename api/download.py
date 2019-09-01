from flask_restful import Resource, reqparse
from api.auth import Auth


class Download(Resource):
    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument('token', type=str)
        args = parser.parse_args()

        (token, err) = Auth.decord_token(args['token'])
        if token is None:
            return err, 200
