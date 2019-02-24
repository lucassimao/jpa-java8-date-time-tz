package com.lucassimao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

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

    }


    @AfterClass
    public static void shutdown() {
        em.close();
        factory.close();
    }

}