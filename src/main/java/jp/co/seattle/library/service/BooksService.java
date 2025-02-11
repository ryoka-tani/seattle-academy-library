package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正,
        //id,書籍名、出版社、著者、出版日、サムネイル1つ
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "SELECT id, title, author, publisher, publish_date, thumbnail_url, description, isbn FROM books ORDER BY title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }

    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBook(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title, author,publisher,thumbnail_name,thumbnail_url,publish_date,description,isbn,reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getDescription() + "','"
                + bookInfo.getIsbn() + "',"
                + "sysdate(),"
                + "sysdate())";

        jdbcTemplate.update(sql);
    }

    /**
     * 最大値のIDを取得する
     * @return getBookId
     */
    public int getBookId() {
        String sql = "SELECT MAX(ID)FROM books";
        int getBookId = jdbcTemplate.queryForObject(sql, Integer.class);
        return getBookId;
    }

    /**bookIDに紐付いたデータを書籍テーブル上から削除
     * @param bookId 
     */
    public void deletingSystem(int bookId) {
        String sql = "DELETE FROM books WHERE id =" + bookId + ";";
        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を編集する
     *
     * @param bookInfo 書籍情報
     */
    public void editBook(BookDetailsInfo bookInfo) {
        String sql = "UPDATE books set title ='" + bookInfo.getTitle()
                + "',author ='" + bookInfo.getAuthor()
                + "',publisher ='" + bookInfo.getPublisher()
                + "',thumbnail_name='" + bookInfo.getThumbnailName()
                + "',thumbnail_url='" + bookInfo.getThumbnailUrl()
                + "',publish_date='" + bookInfo.getPublishDate()
                + "',description='" + bookInfo.getDescription()
                + "',isbn='" + bookInfo.getIsbn()
                + "',upd_date =" + "sysdate()"
                + "where id =" + bookInfo.getBookId() + ";";

        jdbcTemplate.update(sql);
    }

    /**
     * 検索フォームの追加
     */
    /**
     * @param searchTitle 
     * @return searchBookLis
     */
    public List<BookInfo> getSearchBookList(String searchTitle) {
        List<BookInfo> searchBookList = jdbcTemplate.query(
                "SELECT * FROM books WHERE title LIKE '%" + searchTitle + "%' ORDER BY title asc",
                new BookInfoRowMapper());
        return searchBookList;
    }

}
