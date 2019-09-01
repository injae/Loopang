import enum




def request_message(status, message):
    code = 404
    if type == 'success':
        code = 200
    elif type == 'fail':
        code = 202
    else:
        status = 'error'
        code = 404
    return {'status:': status, 'message:': message}, code
