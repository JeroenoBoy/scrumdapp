# scrumdapp

Welcome to Scrumdapp! A tool made for squads within Open-ICT to manage their daily check-ins and share them with their coaches. 

The full tool is build using the latest version of [ktor](https://ktor.io/). 

Fun fact! One of the challenges and design requirments of this tool was to not use any Javascript on the both the front- and backend. This required some creative htmlcss solutions to create all the functionality required for Scrumdapp to function properly. 

## Running and hosting scrumdapp

### Tools

Gradle and Java v24 are required to run this app. We specifically recommend Amazon Corretto V24 for running in 
development & production.

### Enviroment Variables

```dotenv
#
# Database credentials
#

# The database driver to use, postgres recomended
DATABASE_DRIVER="org.postgresql.Driver"

# Other database data...
DATABASE_URL="jdbc:postgresql://localhost:5432/scrumdapp-example"
DATABASE_USER="root"
DATABASE_PASSWORD="one-very-scrum-worthy-password"

#
# Discord OAuth data, make an application at https://discord.com/developers/applications
#

DISCORD_OAUTH_ID=
DISCORD_OAUTH_SECRET=
DISCORD_OAUTH_CALLBACK= # Must be http[s]://(domain)[:(port)]/auth/callback

# The server id the discord user needs to be in to authenticate in the app.
AUTHORIZATION_SERVER_ID=730401118470930432

#
# Encryption variables. We only reccomend changing AES_SECRET_KEY.
#

GCM_IV_SIZE=12
GCM_SPEC_SIZE=128
# Must be a value of 16 bytes, can be any character. Rest will be ignored.
AES_SECRET_KEY=
```

### Running in development

For development, we recommend using Amazon Corretto V24 along with Intellij Community Edition 2025.2 and up. Then 
use their gradle tools to run, debug and build the tool.

### Running in production

Make sure you have a database setup. We recommend postgres, see
[working-with-databases](https://www.jetbrains.com/help/exposed/working-with-database.html) for a list of databases 
available.

properly set the environment variables to reflect your production needs.

Then for the app itself, we recommend building it with the docker image and deploying it either via docker, or some 
other containerization software. Otherwise, make sure you have Java V24 installed on your system, build it via 
gradle and then run the app using `java -jar (output.jar)` If it doesn't start, you likely don't have the correct JDK.

## Contributions

- [Jeroeno](https://github.com/JeroenoBoy)
- [TheLonelyKiwi](https://github.com/TheLonelyKiwi)
