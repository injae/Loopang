from flask_restful import Resource, reqparse
from model.User import User
from model.database import db
import uuid

secret_key = ""


class SignUp(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('email',    type=str)
            parser.add_argument('name',     type=str)
            parser.add_argument('password', type=str)
            args = parser.parse_args()

            public_id = str(uuid.uuid4())
            email = args['email']
            name = args['name']
            password = args['password']
            user = User(public_id=public_id, email=email, name=name, password=password)
            if User.query.filter_by(email=email).first() is not None:
                return {'status': 'fail', 'message': "duplicate id"}, 200
            else:
                db.session.add(user)
                db.session.commit()

            return {'status': 'success', 'message': 'sign up'}, 200

        except Exception as e:
            return {'status': 'error', 'message': str(e)}, 400
