package com.openshift.test;


import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import com.openshift.jenkins.EmployeeController;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeController checkService;

   // @InjectMocks
  //  private Controller checkResource;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(checkService)
                .build();
    }

    @Test
    public void testCheck() throws Exception {

        when(checkService.info()).thenReturn("hello");

        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk());

        verify(checkService).info();
    }

    /*
    @Test
    public void testOpenshiftdJson() throws Exception {
        mockMvc.perform(get("/api/employee/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Openshift-Jenkins")))
                .andExpect(jsonPath("$.*", Matchers.hasSize(3)));
    }*/

}