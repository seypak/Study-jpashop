package jpabook.jpashop.domain.service;

import jpabook.jpashop.item.Book;
import jpabook.jpashop.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    ItemService itemService;
    
    @Autowired
    ItemRepository itemRepository;
    
    @Autowired
    EntityManager em;
    
    @Test
    @Rollback(value = false)
    public void 상품등록() throws Exception {
        //given
        Book book = new Book();
        book.setName("상품1");
        book.setPrice(1000);
        book.setStockQuantity(100);
        
        //when
        itemRepository.save(book);

        Item findItem = itemRepository.findOne(book.getId());

        em.flush();

        //then
        assertEquals(book.getName(), findItem.getName());
    }

}