package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue("M") //클래스 - 테이블 매칭 : Single Table에서 dtype에 들어갈 value
// M으로 설정하지 않으면, 자동으로 클래스 이름인 Movie가 value르 들어갈 것이다.
public class Movie extends Item{

    private String director;
    private String actor;
}
