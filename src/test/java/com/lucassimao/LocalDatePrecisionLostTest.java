package com.lucassimao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Month;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalDatePrecisionLostTest{


    static EntityManager em;
    private static EntityManagerFactory factory;

    
    @BeforeClass
    public static void name() {
        
        factory = Persistence.createEntityManagerFactory("java8date-UTC");
        em = factory.createEntityManager();
    }


    @Test
    public void shouldLocalDateLosePrecision(){
            TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));

            LocalDate originalDate= LocalDate.of(1990, Month.JUNE, 17);
            BookEntry entry = new BookEntry();
            entry.setValue(100);
            entry.setDate(originalDate);

            em.getTransaction().begin();

            em.createQuery("delete from BookEntry").executeUpdate();
            em.persist(entry);
            em.flush();
            entry = null;


            Query q = em.createNativeQuery("select id from BookEntry where CAST(date as char)=?");
            q.setParameter(1, "1990-06-17");
            List result = q.getResultList();

            assertEquals(1, result.size());
            assertTrue(result.get(0) instanceof BigInteger);

            BigInteger id = (BigInteger) result.get(0);
            em.clear();
            assertEquals(16, em.find(BookEntry.class, id.longValue()).getDate().getDayOfMonth() );

            em.getTransaction().commit();
            em.clear();

            Session hSession =  em.unwrap(Session.class);
            hSession.clear();

            hSession.doWork( (connection)->{
                PreparedStatement ps = connection.prepareStatement("select date from BookEntry where id=?");
                ps.setLong(1, id.longValue());
                ResultSet rs =  ps.executeQuery();
                assertTrue(rs.next());
                assertTrue( rs.getDate(1) instanceof java.sql.Date);
                java.sql.Date dt = rs.getDate(1);

                assertEquals(16, dt.getDate());
                
                rs.close();
                ps.close();
            });
            hSession.close();

    }

    /**
     * that is exactly what com.mysql.cj.result.SqlDateValueFactory does when converting a SQL DATE type to java.sql.Date
     */
    @Test
    public void shouldReturnBuggyDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setLenient(false);

        cal.clear();
        cal.set(1990, Calendar.JUNE, 17);
        long ms = cal.getTimeInMillis();
        Date dt = new Date(ms);

        assertEquals(16, dt.getDate());
    }


    @AfterClass
    public static void shutdown() {
        em.close();
        factory.close();
    }

}