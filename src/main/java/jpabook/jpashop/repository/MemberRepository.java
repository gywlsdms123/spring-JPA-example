package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    // DB에 insert 쿼리 날라가는 부분
    public void save(Member member){
        em.persist(member);
    }

    // 단 건 조회
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    // from의 대상이 테이블이 아니라 엔티티의 대상
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 특정 회원 찾는 부분
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
