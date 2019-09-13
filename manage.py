from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand
from App import App
from model.database import db

if __name__ == '__main__':
    app = App()
    migrate = Migrate(app, db)
    manager = Manager(app)
    manager.add_command('db', MigrateCommand)
    manager.run()
