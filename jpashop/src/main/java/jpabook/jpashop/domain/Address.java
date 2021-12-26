package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
//Setter제공 안함 : 값타입(Embed타입)은 값이 변경되면 안된다. -> Setter없다.
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
        //jpa는 entity를 생성할 때, 리플렉션, 프록시 등의 기술을 쓴다. 이때, 기본 constructor가 필요하다.
        //하지만, 그렇다고 이 기본 constructor를 public으로 아무나 가져다가 쓰게 할 수는 없다. 의도치 않는 오류가 발생할 수 있다.
        //-> jpa는 protected까지는 허용한다.
        //protected : 상속한 것까지 접근 허용.
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
