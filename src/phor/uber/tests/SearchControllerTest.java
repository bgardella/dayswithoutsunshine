package phor.uber.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import phor.uber.web.SearchController;

//@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
//@WebAppConfiguration
public class SearchControllerTest {

    @InjectMocks
    private SearchController searchController;
    
    private MockMvc mockMvc;
    
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void rootPagetest() {
        
        try {
            mockMvc.perform(get("/"))
                            .andExpect(status().isOk())
                            .andExpect(model().attributeExists("urlBase"))
                            .andExpect(model().attributeDoesNotExist("fooBar"));
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void searchTests() {
        
        try {
            mockMvc.perform(get("/byid/22"))
                            .andExpect(status().isOk());
            
            mockMvc.perform(get("/selectAll"))
                            .andExpect(status().isOk());

            mockMvc.perform(get("/autocomplete/bac"))
                            .andExpect(status().isOk());
            
            mockMvc.perform(post("/exactSearch")
                            .param("search", "kevin bacon"))
                            .andExpect(status().isOk());

            mockMvc.perform(post("/looseSearch")
                    .param("search", "kevin bacon"))
                    .andExpect(status().isOk());
            
            mockMvc.perform(get("/byField/release_year/1984"))
                    .andExpect(status().isOk());
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
