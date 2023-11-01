package jpabook.jpashop.domain;

import jpabook.jpashop.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    // 실무에서는 @ManyToMany를 사용하지말 것.
    // 이유: JoinTable이 생성될 때 필드를 더 추가할 수가 없기때문에 (ex: 등록/수정일 등)
    // 해결방법: JoinTable이 아닌 Category <1:N> 중간테이블 <N:1> Item 관계로 해결할 것.
    // https://seypark.tistory.com/193 - '다대다 매핑의 한계' 참고
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child;

    //== 연관관계 메서드 ==//
    public void addChildCategory(Category child) {
        parent.getChild().add(child);
        child.setParent(this);
    }
}
