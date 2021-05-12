package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.RentService;

@Controller
public class LendingController {
    final static Logger logger = LoggerFactory.getLogger(LendingController.class);

    @Autowired
    private RentService RentService;

    @Autowired
    private BooksService booksService;

    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String borrowingBook(Locale locale,
            @RequestParam("bookId") int bookId,
            Model model) {
        //LOGGERで記録する
        logger.info("Welcome borrowingBook.java! The client locale is {}.", locale);
        //貸出を登録する
        RentService.rentBook(bookId);
        //書籍情報を再取得する（リターンした時に詳細画面に書籍情報を表示する為）
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        model.addAttribute("lendingStatus", "貸出中");
        //貸出登録後画面遷移（→書籍詳細画面のまま）
        return "details";
    }

    @Transactional
    @RequestMapping(value = "/returnBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String returnBook(Locale locale,
            @RequestParam("bookId") int bookId,
            Model model) {
        //LOGGERで記録する
        logger.info("Welcome returnBook.java! The client locale is {}.", locale);
        //返却する(貸出テーブルからデータを削除)(deletingRentBookメソッドを使って)
        RentService.deletingRentBook(bookId);
        //書籍情報を再取得する（リターンした時に詳細画面に書籍情報を表示する為）
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        model.addAttribute("lendingStatus", "貸出可");
        //貸出登録後画面遷移（→書籍詳細画面のまま）
        return "details";
    }

}