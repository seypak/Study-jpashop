package jpabook.jpashop.controller;

import jpabook.jpashop.item.Book;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {

        /**
         * Order에서 본 것처럼 Static 생성자 메소드를 가지고 의도에 맞게 사용하게 하는 게 제일 좋다.
         * 초기에 생성했던 테스트코드때문에 생성자를 막지는 않았다.
         * 필요하다면 @NoArgsConstructor(access = AccessLevel.PROTECTED)도 추가할 것.
         */
        Book book = Book.createBook(form.getName(), form.getPrice(), form.getStockQuantity(), form.getAuthor(), form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/";
    }
}
