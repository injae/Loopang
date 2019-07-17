from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand
from App import create_app
from model.database import db

if __name__ == '__main__':
    app = create_app()
    migrate = Migrate(app, db)
    manager = Manager(app)
    manager.add_command('db', MigrateCommand)
    manager.run()
