from flask_restful import Resource, reqparse
from flask import Response, stream_with_context
from tools.request_message import request_message
from api.auth import Auth
from model.music import Music


class Download(Resource):
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('token', type=str)
        parser.add_argument('name', type=str)
        args = parser.parse_args()

        (token, err) = Auth.decord_token(args['token'])
        if token is None:
            return err, 200

        file_name = Music.search(args['name'])
        if file_name is not None:
            with open(file_name.path(), 'rb') as buffer:
                def streaming():
                    for line in buffer:
                        yield line
                return Response(stream_with_context(streaming()))
            return request_message('error', 'Can\'t find file')
        else:
            return request_message('fail', 'Can\'t find music')

# response = make_response(streaming)
# response.headers['Content-Type'] = "application/octet-stream"
# content_type='application/octet-stream',
