package fi.haagahelia.kolehmainenbookstore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import fi.haagahelia.kolehmainenbookstore.domain.Book;
import fi.haagahelia.kolehmainenbookstore.domain.BookRepository;
import fi.haagahelia.kolehmainenbookstore.domain.Category;
import fi.haagahelia.kolehmainenbookstore.domain.CategoryRepository;

@DataJpaTest
public class BookRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(new Category("TestCategory"));

        book1 = new Book(
                "TestBook1", "TestAuthor1", "123412345",
                1990, 9.99, category);

        book2 = new Book(
                "TestBook2", "TestAuthor2", "1234123456",
                1990, 9.99, category);
    }

    @Test
    public void saveNewBook() {
        bookRepository.save(book1);
        assertThat(book1.getId()).isNotNull();

        Long bookId = book1.getId();
        Long categoryId = category.getId();

        entityManager.flush();
        entityManager.clear();

        Book savedBook = bookRepository.findById(bookId).orElseThrow();

        assertThat(savedBook.getTitle()).isEqualTo("TestBook1");
        assertThat(savedBook.getCategory().getId()).isEqualTo(categoryId);
    }

    @Test
    public void findBookByTitle() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        Long bookId = book1.getId();

        entityManager.flush();
        entityManager.clear();

        List<Book> books = bookRepository.findByTitle("TestBook1");

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getId()).isEqualTo(bookId);
        assertThat(books.get(0).getAuthor()).isEqualTo("TestAuthor1");
        assertThat(books.get(0).getIsbn()).isEqualTo("123412345");
    }

    @Test
    public void updateBook() {
        bookRepository.save(book1);
        Long bookId = book1.getId();

        book1.setAuthor("TestAuthor2");

        entityManager.flush();
        entityManager.clear();

        Book updatedBook = bookRepository.findById(bookId).orElseThrow();

        assertThat(updatedBook.getId()).isEqualTo(bookId);
        assertThat(updatedBook.getAuthor()).isEqualTo("TestAuthor2");
    }

    @Test
    public void deleteBook() {
        bookRepository.save(book1);
        Long bookId = book1.getId();

        bookRepository.delete(book1);

        entityManager.flush();
        entityManager.clear();

        assertThat(bookRepository.findById(bookId)).isEmpty();
    }
}