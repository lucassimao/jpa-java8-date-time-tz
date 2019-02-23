package com.lucassimao;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class App {
    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("java8date");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery("from Person");
        // Person p = (Person) query.getSingleResult();
        Person p = (Person) query.getResultStream().findFirst().orElseGet(() -> {
            Person p2 = new Person();
            p2.birthdate = LocalDate.of(1990, Month.JUNE, 17).atStartOfDay(ZoneOffset.ofHours(-1));
            p2.setNome("lucas simao");
            em.persist(p2);
            return p2;
        });

        System.out.println(p.birthdate);
        System.out.println(p.birthdate.withZoneSameInstant(ZoneOffset.ofHours(-1)));


        em.getTransaction().commit();
        em.close();
        factory.close();
    }
}
