package fi.haagahelia.kolehmainenbookstore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
public class WebLayerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser 
    public void uthenticatedUserCanViewBookList() throws Exception {
        this.mockMvc.perform(get("/list"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("booklist"))
            .andExpect(model().attributeExists("books"));
    }

    @Test
    public void unauthenticatedUserCantViewBooklist() throws Exception {
        mockMvc.perform(get("/list"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser 
    public void uthenticatedUserCanViewAddbook() throws Exception {
        this.mockMvc.perform(get("/add"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("addbook"))
            .andExpect(model().attributeExists("book"));
    }

    @Test
    public void unauthenticatedUserCantViewAddBook() throws Exception {
        mockMvc.perform(get("/add"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

}
