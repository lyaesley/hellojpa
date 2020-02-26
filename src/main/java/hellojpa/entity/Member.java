package hellojpa.entity;

import javax.persistence.*;
import java.util.Date;

@Entity // note: ! javax.persistence.Entity 얘를 import 한다. 다른애들은 아니다!
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 20) // DB TABLE 컬럼명 지정
    private String name;

    private int age;

    // 객체를 테이블에 맞추어 데이터 중심으로 모델링
    @Column(name = "TEAM_ID_OLD")
    private Long teamId;

    // 객체 지향 모델링 (객체의 참조와 테이블의 외래 키를 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private  Team team;

    @Temporal(TemporalType.TIMESTAMP)   // 시간과 관련
    private Date regDate;

    // Enumerated : 디폴트는 EnumType.ORDINAL (enum 의 INDEX 숫자로 입력됨. 중간에 enum 이 추가 될 경우 INDEX가 꼬임 절대사용X)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Transient
    private String transientTest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public String getTransientTest() {
        return transientTest;
    }

    public void setTransientTest(String transientTest) {
        this.transientTest = transientTest;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", teamId=" + teamId +
                ", team=" + team +
                ", regDate=" + regDate +
                ", memberType=" + memberType +
                ", transientTest='" + transientTest + '\'' +
                '}';
    }
}

