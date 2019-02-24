# JPA, Timezones, Java 8 Date and Time APIs 
Testing how JPA, hibernate and the java 8 date and time APIs interact with mysql database timezone

## Setup
- Setup the following mysql instances through docker
> docker run --name java8-guayaquil -p 3307:3306 -e MYSQL_ROOT_PASSWORD=123 -e TZ='America/Guayaquil' -d mysql:latest
> docker run --rm -it --link java8-guayaquil mysql:latest mysql -u root -h java8-guayaquil -p --execute="create database IF NOT EXISTS java8_tests"
> docker run --name java8-utc -p 3308:3306 -e MYSQL_ROOT_PASSWORD=123 -e TZ='UTC' -d mysql:latest
> docker run --rm -it --link java8-utc mysql:latest mysql -u root -h java8-utc -p --execute="create database IF NOT EXISTS java8_tests"
- Run
> mvn test

## Conclusions
<pre>
Java app TZ == DB TZ
    without hibernate.jdbc.time_zone : stores ZonedDateTime instances with database TZ
    with hibernate.jdbc.time_zone : stores ZonedDateTime instances with the timezone value of hibernate.jdbc.time_zone property 
App TZ != db TZ
    without hibernate.jdbc.time_zone : stores ZonedDateTime instances with database TZ
    with hibernate.jdbc.time_zone : stores ZonedDateTime instances with the timezone value of hibernate.jdbc.time_zone property
</pre>



