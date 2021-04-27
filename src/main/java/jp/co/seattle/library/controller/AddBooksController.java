package jp.co.seattle.library.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.multipart.MultipartFile;

import com.mysql.jdbc.StringUtils;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * Handles requests for the application home page.
 */

@Controller //APIの入り口
public class AddBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/addBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addBook";
    }

    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam("isbn") String isbn,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublishDate(publishDate);
        bookInfo.setDescription(description);
        bookInfo.setIsbn(isbn);

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "addBook";
            }
        }
        if (StringUtils.isNullOrEmpty(title) || StringUtils.isNullOrEmpty(author)
                || StringUtils.isNullOrEmpty(publisher)
                || StringUtils.isNullOrEmpty(publishDate)) {
            model.addAttribute("error", "必須項目を入力してください");
            return "addBook";
        }
        // TODO 登録した書籍の詳細情報を表示するように実装
        //出版日、半角数字のYYYYMMDDでなければエラーを出す
        //ISBN文字数13まで、半角数字じゃないとエラーを出す

        try {
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            df.setLenient(false);
            df.format(df.parse(publishDate));
        } catch (ParseException p) {
            model.addAttribute("error", "ISBNの桁数または半角数字が正しくありません<br>出版日は半角数字のYYYYMMDD形式で入力してください");
            return "addBook";
        }
        boolean isValidIsbn = isbn.matches("[0-9]{10}||[0-9]{13}?");
        if (!isValidIsbn) {
            model.addAttribute("error", "ISBNの桁数または半角数字が正しくありません<br>出版日は半角数字のYYYYMMDD形式で入力してください");
            return "addBook";
        }

        // 書籍情報を新規登録する
        booksService.registBook(bookInfo);
        model.addAttribute("resultMessage", "登録完了");
        model.addAttribute("bookDetailsInfo", bookInfo);
        //  詳細画面に遷移する
        return "details";
    }
}
