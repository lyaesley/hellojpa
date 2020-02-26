package hellojpa;

import hellojpa.entity.Member;
import hellojpa.entity.MemberType;
import hellojpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;

public class Main {

    public static void main(String[] args) { // 아래는 JPA 정석 로직이다!
        /*  note:
        *   EntityManagerFactory 는 서버 띄울때 딱 1번만 띄운다. JPA 매니저라고 생각하면 된다.
        *   EntityManagerFactory 는 하나만 생성해서 애플리케이션 전체에서 공유
        *   EntityManager 쓰레드간에 공유하면 안된다.(사용하고 버려야한다) (DB 커넥션 당 묶이기 때문이다) (스프링에서는 알아서 해준다)
        *   JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
        * */
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("hello"); // note: 파라미터 hello 는 persistence.xml 에 <persistence-unit name="hello"> 에 설정한 정보를 매핑한다.

        // note: EMF 에서 EntityManager 를 꺼낸다. EntityManager 자체가 jpa 라고 생각해도 된다.
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx =  em.getTransaction();  // note: 트랜잭션을 먼저 얻어야 한다. JPA 에서 모든 활동은 트랜잭션 안에서 이루어져야 한다.
        tx.begin(); // note: 트랜잭션 시작

        try {

            //팀 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            //새로운 팀B 저장
            Team teamB = new Team();
            teamB.setName("TeamB");
            em.persist(teamB);

            //회원 저장
            Member member = new Member();
//            member.setId(101L);

            // 객체를 테이블에 맞추어 데이터 중심으로 모델링
            member.setTeamId(team.getId());

            // 객체 지향 모델링 (객체의 참조와 테이블의 외래 키를 매핑)
            member.setTeam(team);

            member.setName("안녕하세요");
            member.setMemberType(MemberType.USER);
            member.setRegDate(new Date());
            member.setTransientTest("이것은 저장되지 않음");

            em.persist(member); // 영구 저장하다 라는 표현

            member.setTeam(teamB);
            //
            em.flush();
            em.clear();

            ////////////////////////////////////////////////

            Member findMember = em.find(Member.class, member.getId());

            // 객체를 테이블에 맞추어 데이터 중심으로 모델링. 식별자로 다시 조회, 객체 지향적인 방법은 아니다.)
            Long teamId = findMember.getTeamId();

            Team findTeam = em.find(Team.class, teamId);
            System.out.println(findTeam.getName());

            // 객체 지향 모델링 (객체의 참조와 테이블의 외래 키를 매핑)
            Team findTeam2 = findMember.getTeam();
            System.out.println(findTeam2.getName());

            ////////////////////////////////////////////////

            tx.commit(); // 커밋
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close(); // em 닫기
        }

        emf.close(); // emf 닫기
    }
}
