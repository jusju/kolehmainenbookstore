package fi.haagahelia.kolehmainenbookstore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import fi.haagahelia.kolehmainenbookstore.domain.Category;
import fi.haagahelia.kolehmainenbookstore.domain.CategoryRepository;

@DataJpaTest
public class CategoryRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = new Category("TestCategory1");
        category2 = new Category("TestCategory2");
    }

    @Test
    public void saveNewCategory() {
        categoryRepository.save(category1);
        assertThat(category1.getId()).isNotNull();

        Long categoryId = category1.getId();

        entityManager.flush();
        entityManager.clear();

        Category savedCategory =
                categoryRepository.findById(categoryId).orElseThrow();

        assertThat(savedCategory.getName()).isEqualTo("TestCategory1");
    }

    @Test
    public void findAllIncludesSavedCategories() {
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Long categoryId1 = category1.getId();
        Long categoryId2 = category2.getId();

        entityManager.flush();
        entityManager.clear();

        Iterable<Category> categories = categoryRepository.findAll();

        assertThat(categories)
                .extracting(Category::getId)
                .contains(categoryId1, categoryId2);
    }

    @Test
    public void updatingCategoryChangesNameAndKeepsId() {
        categoryRepository.save(category1);
        Long categoryId = category1.getId();

        category1.setName("UpdatedCategory");

        entityManager.flush();
        entityManager.clear();

        Category updatedCategory =
                categoryRepository.findById(categoryId).orElseThrow();

        assertThat(updatedCategory.getId()).isEqualTo(categoryId);
        assertThat(updatedCategory.getName()).isEqualTo("UpdatedCategory");
    }

    @Test
    public void deletedCategoryCannotBeFoundById() {
        categoryRepository.save(category1);
        Long categoryId = category1.getId();

        categoryRepository.delete(category1);

        entityManager.flush();
        entityManager.clear();

        assertThat(categoryRepository.findById(categoryId)).isEmpty();
    }
}