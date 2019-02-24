package com.lucassimao;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testing how a specfic ZonedDateTime instance is persisted in a mysql database
 * with America/Guayaquil (-5 hours) timezone from a client with the America/Fortaleza (-3 hours) timezone 
 * 
 */
public class GuayaquilTZTest {

    static EntityManager em;
    private static EntityManagerFactory factory;
    private final DateTimeFormatter mysqlFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private final ZoneId GUAYAQUIL_ZONE_ID = ZoneId.of("America/Guayaquil");
    private final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    @BeforeClass
    public static void setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));
        factory = Persistence.createEntityManagerFactory("java8date-Guayaquil");
        em = factory.createEntityManager();
    }   

    @Before
    public void beforeTests() {
        em.getTransaction().begin();
        em.createQuery("delete from Person").executeUpdate();
        em.getTransaction().commit();
    }

    /**
     * scenario local tz: America/Fortaleza 
     * db tz : America/Guayaquil
     * hibernate.jdbc.time_zone: none
     */
    @Test
    public void shouldSaveZonedDateTimeAsDataBaseTZ() {
        Person me = new Person();
        ZonedDateTime myBirthDate = LocalDate.of(1990, Month.JUNE, 17).atStartOfDay(ZoneId.systemDefault());
        me.birthdate = myBirthDate;
        me.setNome("lucas simao");

        em.getTransaction().begin();
        em.persist(me);

        Query q = em.createNativeQuery("select * from Person where CAST(birthdate as char)=?");
        String format = mysqlFormatter.withZone(GUAYAQUIL_ZONE_ID).format(myBirthDate);
        q.setParameter(1, format);
        assertEquals(1, q.getResultList().size());
        em.getTransaction().commit();
    }

      /**
     * scenario local tz: America/Fortaleza 
     * db tz : America/Guayaquil
     * hibernate.jdbc.time_zone: UTC
     */
    @Test
    public void shouldSaveZonedDateTimeAsUTC() {

        Session hSession =  em.unwrap(Session.class)
                                    .sessionWithOptions().jdbcTimeZone(TimeZone.getTimeZone("UTC"))
                                    .flushMode(FlushMode.ALWAYS)
                                    .autoClear(true)
                                    .openSession();

        hSession.beginTransaction();
        Person me = new Person();
        ZonedDateTime myBirthDate = LocalDate.of(1990, Month.JUNE, 17).atStartOfDay(ZoneId.systemDefault());
        me.birthdate = myBirthDate;
        me.setNome("lucas simao");
        hSession.save(me);

        Query q = hSession.createNativeQuery("select * from Person where CAST(birthdate as char)=?");
        String format = mysqlFormatter.withZone(UTC_ZONE_ID).format(myBirthDate);
        q.setParameter(1, format);
        List resultList = q.getResultList();
        assertEquals(1, resultList.size());
        
        hSession.getTransaction().commit();
        hSession.close();
    }    

    @AfterClass
    public static void teardown() {
        em.close();
        factory.close();
    }
}
