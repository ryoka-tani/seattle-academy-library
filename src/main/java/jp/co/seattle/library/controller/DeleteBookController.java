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

/**
 * 削除コントローラー
 */
@Controller //APIの入り口
public class DeleteBookController {
    final static Logger logger = LoggerFactory.getLogger(DeleteBookController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private RentService rentService;

    /**
     * 対象書籍を削除する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/deleteBook", method = RequestMethod.POST)
    public String deleteBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);
        //貸出状況を確認
        int number = rentService.rentCount(bookId);
        //ifで貸出状況に応じて処理を変える
        //データがある場合（貸出中の場合、１）
        if (number == 1) {
            //（削除しない）
            //詳細情報を取得
            model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
            model.addAttribute("lendingStatus", "貸出中");
            model.addAttribute("error", "貸出中のため削除できません。");
            //（画面は遷移なし詳細のまま）
            return "details";
        } else {//データがない場合（貸出可能の場合、０）
            //（削除する）    
            booksService.deletingSystem(bookId);
            //ホーム画面上での一覧情報を取得
            model.addAttribute("bookList", booksService.getBookList());
            //（画面はホームに戻る）    
            return "home";
        }
    }
}
