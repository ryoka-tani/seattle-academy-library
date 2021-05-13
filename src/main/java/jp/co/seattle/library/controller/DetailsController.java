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
 * 詳細表示コントローラー
 */
@Controller
public class DetailsController {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);

    @Autowired
    private BooksService bookdService;

    @Autowired
    private RentService RentService;

    /**
     * 詳細画面に遷移する
     * @param locale
     * @param bookId
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public String detailsBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        //ホームからdetailsに飛んだ時に貸出テーブルにデータがあるかないか
        //貸出状況を確認
        int number = RentService.rentCount(bookId);
        //ifで貸出状況に応じて処理を変える
        //データがある場合（貸出中の場合、１）
        if (number == 1) {
            //借りるボタンは非活性 
            //返すボタンは活性           
            //削除ボタンは非活性
            //貸出ステータスは貸出し中
            model.addAttribute("lendingStatus", "貸出中");
        } else {//データがない場合（貸出可能の場合、０）
            //借りるボタンは活性
            //返すボタンは非活性
            //削除ボタンは活性
            //貸出ステータスはなし
            model.addAttribute("lendingStatus", "貸出可");
        }
        model.addAttribute("bookDetailsInfo", bookdService.getBookInfo(bookId));

        return "details";
    }

}
