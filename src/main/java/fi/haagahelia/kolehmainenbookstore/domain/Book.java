package fi.haagahelia.kolehmainenbookstore.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;

@Entity
//@Table(name="kirja") 
public class Book {

    @Id 
    @GeneratedValue (strategy = GenerationType.AUTO) 
    private Long id;
    
    //@Column (name="title")
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private Double price;

    @JsonIgnoreProperties("categoryBooks")
    @ManyToOne
    //@JoinColumn(name="namedCol")
    private Category category;

    public Book() {
    }


    public Book(String title, String author, String isbn, int publicationYear, Double price, Category category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.price = price;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + title + ", author=" + author + ", isbn=" + isbn + ", publicationYear="
                + publicationYear + ", price=" + price + ", category=" + category + "]";
    }
    
    
}
