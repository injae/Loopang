from flask_restful import Resource, reqparse
from model.User import User
from model.database import db, gen_id

secret_key = ""


class SignUp(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('email',    type=str)
            parser.add_argument('name',     type=str)
            parser.add_argument('password', type=str)
            args = parser.parse_args()

            public_id = gen_id()
            email = args['email']
            name = args['name']
            password = args['password']
            user = User(public_id=public_id, email=email, name=name, password=password)
            if(user.is_duplicate()):
                return {'status': 'fail', 'message': "duplicate id"}, 202
            else:
                db.session.add(user)
                db.session.commit()
                return {'status': 'success', 'message': 'sign up'}, 200
        except Exception as e:
            return {'status': 'error', 'message': str(e)}, 400
