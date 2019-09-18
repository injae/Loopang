from flask_restful import Resource, reqparse
from model.User import User
from model.database import db, gen_id
from tools.request_message import request_message
from App import logger


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
            logger().debug('[sign-up] email: %s', email)
            logger().debug('[sign-up] name: %s', name)
            user = User(public_id=public_id, email=email, name=name, password=password)
            if(user.is_duplicate_email()):
                return request_message('fail', 'duplicate email')
            elif(user.is_duplicate_name()):
                return request_message('fail', 'duplicate name')
            else:
                db.session.add(user)
                db.session.commit()
                return request_message('success', 'sign up')
        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))
