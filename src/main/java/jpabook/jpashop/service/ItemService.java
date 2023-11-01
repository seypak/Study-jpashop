package jpabook.jpashop.service;

import jpabook.jpashop.controller.BookForm;
import jpabook.jpashop.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 변경 감지와 병합(merge)
     * 준영속 엔티티를 수정하는 2가지 방법
     * 1. 변경 감지 기능 사용
     * 2. 병합( merge ) 사용
     * => 주의: 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만 병합을 사용하면 모든 속성이 변경된다.
     * 병합시 값이 없으면 null 로 업데이트 할 위험도 있다. (병합은 모든 필드를 교체한다.)
     * ==> 가장 좋은 해결 방법 : 엔티티를 변경할 때는 항상 변경 감지를 사용하세요
     */
    @Transactional
    public void updateBook(Long itemId, BookForm book) {

        // findOne으로 준영속 -> 영속상태가 되고 이 상태에서 엔티티의 변화는 DB의 수정이 가능하다 (변경감지:dirty check)
        // 해서 아래와같이 save를 호출할 필요없이 엔티티의 셋팅만으로 DB Update 쿼리가 날아간다.
        Item findItem = itemRepository.findOne(itemId);

        /**
         * 여러곳에서 setter를 사용하고있으면 추적하기 어렵기때문에
         * setter를 사용하지말고 변경하고자 하는 메서드를 만들어 사용하세요.
         */
        findItem.changeItem(book.getName(), book.getPrice(), book.getStockQuantity());
//        findItem.setName(book.getName());
//        findItem.setPrice(book.getPrice());
//        findItem.setStockQuantity(book.getStockQuantity());
//        itemRepository.save(findItem);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
