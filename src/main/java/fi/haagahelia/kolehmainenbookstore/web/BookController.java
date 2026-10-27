package fi.haagahelia.kolehmainenbookstore.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.haagahelia.kolehmainenbookstore.domain.Book;
import fi.haagahelia.kolehmainenbookstore.domain.BookRepository;
import fi.haagahelia.kolehmainenbookstore.domain.CategoryRepository;



@Controller
public class BookController {

    //FIELD nimeltään bookRepository
    private BookRepository bookRepository;
    private CategoryRepository categoryRepository;

    //CONSTRUCTOR injection
    public BookController(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository =categoryRepository;
    }

    @RequestMapping(value="/login")
	public String login() {
		return "login";
	}

    @GetMapping(value="/books")
        public @ResponseBody List<Book> listBooksREST() {
            return (List<Book>) bookRepository.findAll();
        }

    @GetMapping(value="/books/{id}")
        public @ResponseBody Optional<Book> findBookREST(@PathVariable ("id") Long id) {
            return bookRepository.findById(id);
        }

    @GetMapping("/list")
    public String listBooks(Model model) {
        List<Book> books = (ArrayList<Book>)bookRepository.findAll();
        model.addAttribute("books", books);
        return "booklist";
    }

    @GetMapping("/add")
    public String addBooks(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "addbook";
    }

    
    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id).orElseThrow();
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryRepository.findAll());
        return "addbook";
    }

    @PostMapping("/save")
    public String saveBook(Book book) {
        bookRepository.save(book);
        return "redirect:/list";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/list";
    }

    /*@GetMapping("/delete")
        public String deleteBook(@RequestParam Long id) {
        } */
    
}
