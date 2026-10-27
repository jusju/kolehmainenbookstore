package fi.haagahelia.kolehmainenbookstore.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

//@RepositoryRestResource(path="library")
public interface BookRepository extends CrudRepository <Book, Long> {
    @RestResource(path="title")
    List<Book> findByTitle(@Param("title") String title);
    @RestResource(path="title-starts-with")
    List<Book>findByTitleStartingWith(@Param("prefix")String prefix);
    /*    
    List<Book> findByAuthor(String author);
    List<Book> findByAuthorAndPublicationYear(String author, int publicationYear);
    List<Book> findByPublicationYearGreaterThan(int publicationYear);
    List<Book> findByPublicationYearGreaterEqualThan(int publicationYear);
    List<Book> findByPublicationYearLessThan(int publicationYear);
    List<Book> findByPublicationYearLessEqualThan(int publicationYear);
    
    List<Book>findByTitleContaining(String text);*/
}
