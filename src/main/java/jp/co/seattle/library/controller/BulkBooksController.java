package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Handles requests for the application home page.
 */

@Controller //APIの入り口
public class BulkBooksController {
    final static Logger logger = LoggerFactory.getLogger(BulkBooksController.class);

    @Autowired
    BooksService booksService;

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/bulkBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    public String login(Model model) {
        return "bulkBook";
    }

    /**
     * @param locale 
     * @param uploadFile アップロードされたcsvファイル
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/bulkBooks", method = RequestMethod.POST)
    public String fileContents(Locale locale,
            @RequestParam("uploadFile") MultipartFile uploadFile, Model model) {
        List<String[]> booklist = new ArrayList<String[]>();
        String line;
        try (java.io.InputStream stream = uploadFile.getInputStream();
                Reader reader = new InputStreamReader(stream);
                BufferedReader buf = new BufferedReader(reader);) {
            while ((line = buf.readLine()) != null) {
                String[] book = (line.split(",", -1));
                booklist.add(book);
            }
        } catch (IOException e) {
            model.addAttribute("error", "Can't read contents.");
        }
        boolean flag = false;

        //エラーのリスト作成
        List<String> error = new ArrayList<String>();
        for (int i = 0; i < booklist.size(); i++) {
            if (StringUtils.isNullOrEmpty(booklist.get(i)[0]) || StringUtils.isNullOrEmpty(booklist.get(i)[1])
                    || StringUtils.isNullOrEmpty(booklist.get(i)[2])
                    || StringUtils.isNullOrEmpty(booklist.get(i)[3])) {
                error.add((i + 1) + "行目の必須項目を入力してください");
                flag = true;
            }
            try {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                df.setLenient(false);
                df.format(df.parse(booklist.get(i)[3]));
            } catch (ParseException p) {
                error.add((i + 1) + "行目の出版日は半角数字のYYYYMMDD形式で入力してください");
                flag = true;
            }
            boolean isValidIsbn = booklist.get(i)[4].matches("[0-9]{10}||[0-9]{13}?");
            if (!isValidIsbn) {
                error.add((i + 1) + "行目のISBNの桁数または半角数字が正しくありません");
                flag = true;
            }
        }
        if (flag) {
            model.addAttribute("error", error);
            return "bulkBook";
        }
        //CSVで入力された文字が何番目の本か指定する。
        for (int i = 0; i < booklist.size(); i++) {
            BookDetailsInfo bookInfo = new BookDetailsInfo();
            bookInfo.setTitle(booklist.get(i)[0]);
            bookInfo.setAuthor(booklist.get(i)[1]);
            bookInfo.setPublisher(booklist.get(i)[2]);
            bookInfo.setPublishDate(booklist.get(i)[3]);
            bookInfo.setIsbn(booklist.get(i)[4]);
            bookInfo.setDescription(booklist.get(i)[5]);
            //保存
            booksService.registBook(bookInfo);
        }
        model.addAttribute("resultMessage", "登録完了");
        //  詳細画面に遷移する
        return "bulkBook";
    }
}
