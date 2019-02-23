#JPA, Timezones, Java 8 Date and Time APIs 
Testing how JPA, hibernate and the java 8 date and time APIs interact with mysql database timezone

##Setup
- Setup a mysql instance through docker
> docker run --name java8 -p 3307:3306 -e MYSQL_ROOT_PASSWORD=123 -e TZ='America/Guayaquil' -d mysql:latest
- Run
> mvn exec:java

##Conclusions
Java app TZ == DB TZ
    without hibernate.jdbc.time_zone : stores ZonedDateTime instances with database TZ
    with hibernate.jdbc.time_zone : stores ZonedDateTime instances with database TZ
App TZ != db TZ
    without hibernate.jdbc.time_zone : stores ZonedDateTime instances with database TZ
    with hibernate.jdbc.time_zone : stores ZonedDateTime instances with database TZ




