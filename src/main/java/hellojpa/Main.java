package hellojpa;

import hellojpa.entity.Member;
import hellojpa.entity.MemberType;
import hellojpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

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
//            member.setTeamId(team.getId());

            // 객체 지향 모델링 (객체의 참조와 테이블의 외래 키를 매핑)
//            member.setTeam(team);

            member.setName("안녕하세요");
            member.setMemberType(MemberType.USER);
            member.setRegDate(new Date());
            member.setTransientTest("이것은 저장되지 않음");

            em.persist(member); // 영구 저장하다 라는 표현
            team.getMembers().add(member);
            // Q : DB 에 member 객체가 Insert 될까?
            // A : 안된다. DB member 테이블에 저장되지 않는다. @OneToMany(mappedBy = "team") 설정으로 무시된다. 자세히 공부 필요!
            member.setTeam(teamB);
            member.setTeamId(teamB.getId());

            //
            em.flush();   // DB 에 쿼리를 다 보냄
            em.clear();   // 캐쉬를 깨끗하게 다 비움
            ////////////////////////////////////////////////

            Member findMember = em.find(Member.class, member.getId());
            System.out.println("출력 findMember = " + findMember);
            // 객체를 테이블에 맞추어 데이터 중심으로 모델링. 식별자로 다시 조회, 객체 지향적인 방법은 아니다.)
//            Long teamId = findMember.getTeamId();
//
//            Team findTeam = em.find(Team.class, teamId);
//            System.out.println("findTeam = " + findTeam);


            // 객체 지향 모델링 (객체의 참조와 테이블의 외래 키를 매핑) S
            Team findTeam2 = findMember.getTeam();  // 검색한 member 객체에서 Team 객체를 꺼낸다.
            System.out.println("출력 size() 111 = " + findTeam2.getMembers().size()); // 결과: 1

            Member member2 = findTeam2.getMembers().get(0); // findMember === member2 ? true
            // 역방향으로 가져온 객체에는 @Transient 로 설정한 필드를 읽어 오지 못한다. (DB 에 저장되지 않으니 당연한 얘기인듯)
            System.out.println("출력 member2 변경전 = " + member2);

            findTeam2.getMembers().add(member2);
            // Q : DB 에 member 객체가 Insert 될까?
            // A : 안된다. DB member 테이블에 저장되지 않는다. @OneToMany(mappedBy = "team") 설정으로 무시된다. 읽기만 되지 쓰기는 되지 않는다. 자세히 공부 필요!
            // findTeam2.getMembers() 의 List<getMembers> 에 member 객체가 add 된다.  (List<memberList>.size() + 1)
            System.out.println("출력 size() 222 = " + findTeam2.getMembers().size()); // 결과: 2
            List<Member> members = findTeam2.getMembers(); // 2개의 member 은 같은 객체다


            member2.setName("첫번째 이름바꿈");    // findMember === member2 ? true 때문에 findMember 값도 변경
            member2.setAge(10);

            System.out.println("출력 member2 변경후 = " + member2);

            em.persist(member2);
            // Q : em.persist(member2); 객체가 insert 될까?
            // A : 안된다. 아무 작업 하지 않는다.

            for (Member member3 : findTeam2.getMembers()) {
                member3.setName("두번째 이름바꿈");    // findMember === member2 === member3 ? true
//                em.persist(member3);
                // Q : em.persist(member2); 객체가 insert 될까?
                // A : 안된다. 아무 작업 하지 않는다.
                System.out.println("출력 member3 = " + member3);
            }




//            List<Member> members = findTeam2.getMembers();
//            for (Member member1 : members) {
//                System.out.println("출력 member1 = " + member1);
//            }


//            member.setName("당신의 이름은?");
//            member.setAge(10);
//            findTeam2.getMemberList().add(member);
//            // Q : DB 에 member 객체가 Insert 될까?
//            // A : 안된다. DB member 테이블에 저장되지 않는다. @OneToMany(mappedBy = "team") 설정으로 무시된다. 자세히 공부 필요!
//            //       findTeam2.getMemberList() 의 List<memberList> 에 member 객체가 add 된다.  (List<memberList>.size() + 1)
//
//            System.out.println("출력 member = " + member);
//
//            // team 에서 memberList 를 가져온다
//            List<Member> memberList = findTeam2.getMemberList();
//            Member member1 = memberList.get(0);
//
//            member1.setName("첫번째 이름변경");
//            // Q : Member member1 = memberList.get(0); 의 이름이 update 될까?
//            // A : 된다. 이름이 update 된다.
//            //      역방향으로 가져온 객체에는 @Transient 로 설정한 필드를 읽어 오지 못한다. (DB 에 저장되지 않으니 당연한 얘기인듯)
//            System.out.println("출력 member1 = " + member1);
//
//            //em.persist(member1);
//
//            for (Member member2 : memberList) {
////                em.persist(member2);
//                // Q : em.persist(member2); 객체가 insert 될까?
//                // A : 안된다. exception 오류남
//                System.out.println("출력 member2 = " + member2);
//            }

            // 객체 지향 모델링 (객체의 참조와 테이블의 외래 키를 매핑) E

            ////////////////////////////////////////////////

            //
            em.flush();   // DB 에 쿼리를 다 보냄
            em.clear();   // 캐쉬를 깨끗하게 다 비움

            tx.commit(); // 커밋
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close(); // em 닫기
        }

        emf.close(); // emf 닫기
    }
}
