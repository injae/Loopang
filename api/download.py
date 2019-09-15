from flask_restful import Resource, reqparse
from flask import Response, stream_with_context
from tools.request_message import request_message
from api.auth import Auth
from model.music import Music
from App import logger
import os


class Download(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('token', type=str)
            parser.add_argument('name', type=str)
            args = parser.parse_args()
            logger().debug('[download] file: %s', args['name'])

            (token, err) = Auth.decord_token(args['token'])
            if token is None:
                return err, 200

            file_name = Music.search(args['name'])
            if file_name is not None:
                if(os.path.exists(file_name.path())):
                    buffer = open(file_name.path(), 'rb')
                    def streaming():
                        for line in buffer:
                            yield line
                    return Response(stream_with_context(streaming()))
                return request_message('error', 'Can\'t find file')
            else:
                return request_message('fail', 'Can\'t find music')
        except Exception as e:
            logger().error(str(e))
            return request_message('error', str(e))

# response = make_response(streaming)
# response.headers['Content-Type'] = "application/octet-stream"
# content_type='application/octet-stream',
