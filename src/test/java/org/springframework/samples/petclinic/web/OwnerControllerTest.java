package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

    //   @InjectMocks
    @Autowired
    OwnerController ownerController;

    // @Mock
    @Autowired
    ClinicService clinicService;

    MockMvc mockMvc;

    Collection<Owner> results;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
    }

    @AfterEach
    void tearDown() {
        reset(clinicService);
    }

    @Test
    void testUpdateOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", 1)
                .param("firstName", "Jimmy")
                .param("lastName", "Buffett")
                .param("address", "123 Duval St ")
                .param("city", "Key West")
                .param("telephone", "3151231234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));
    }


    @Test
    void processUpdateOwnerFormNotValid() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", 5)
                .param("firstName", "Jimmy")
                .param("lastName", "Buffett")
                .param("city", "Key West"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name(OwnerController.VIEWS_OWNER_CREATE_OR_UPDATE_FORM));
    }


    @Test
    void testNewOwnerPostNotValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Jimmy")
                .param("lastName", "Buffett")
                .param("city", "Key West"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "address"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testNewOwnerPostValid() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Jimmy")
                .param("lastName", "Buffett")
                .param("Address", "123 Duval St ")
                .param("city", "Key West")
                .param("telephone", "123456789"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testReturnListOfOwners() throws Exception {
        given(clinicService.findOwnerByLastName("")).willReturn(Lists.newArrayList(new Owner(), new Owner()));

        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));

        then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("");
    }

    @Test
    void testFindByNameNotFound() throws Exception {
        mockMvc.perform(get("/owners")
                .param("lastName", "Dont find ME!"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testFindByNameOneFound() throws Exception {
        Owner owner = new Owner();
        owner.setLastName("pippo");
        owner.setId(5);
        given(clinicService.findOwnerByLastName("pippo")).willReturn(Collections.singleton(owner));
        mockMvc.perform(get("/owners")
                .param("lastName", "pippo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/5"));
    }

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
}