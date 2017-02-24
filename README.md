Run mysql in Docker locally
docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag

WORKLOG:

28.1.2017 4h
# Backend work
    - Database modeling
    - API stubs
# Frontend
    - Added Auth0 lock to drawing app

4.2.2107 6h
    - Studying and implementing Groups to Auth0.
    - Improving drawing app, clearing logic and polishing visuals.

11.2.2015 5h
    - Drawing app finalizing
    - Dashboard app support for new data format
    - Backend api for updating smiley
    - DevOps
        - Started building infrastructure to AWS
13.3.2015 3h
    - Setting up cloud infrastructure in AWS
# Commands
- Connect to local MySQL 
mysql -h 127.0.0.1 -P 3306 -u root -p