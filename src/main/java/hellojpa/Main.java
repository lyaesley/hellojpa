package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        // EntityManagerFactory 는 서버 띄울때 딱 1번만 띄운다. JPA 매니저라고 생각하면 된다.
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hello"); //파라미터 hello 는 persistence.xml 에 <persistence-unit name="hello"> 에 설정한 정보를 매핑한다.

        // EMF 에서 EntityManager 를 꺼낸다.
        EntityManager em = emf.createEntityManager();
        System.out.println("hello");
        emf.close();
    }
}
