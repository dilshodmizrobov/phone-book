package com.mizrobov.phonebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mizrobov.phonebook.entity.Person;
import com.mizrobov.phonebook.entity.Phone;
import com.mizrobov.phonebook.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class PhoneBookApplicationTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private List<Person> contacts = null;

	@MockBean
	private ContactService contactService;

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext,
					  RestDocumentationContextProvider restDocumentation){
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation))
				.build();

		Person person1=new Person(1L,"Dilshod","Mizrobov","dilshod@gmail.com",
				"Java Developer","anor.tj");
		Person person2=new Person(2L,"Dilovar","Mizrobov","dilovar@gmail.com",
				"Web Developer","anor.tj");

		person1.addPhone(new Phone(1L,"+992 91540323","mobile"));
		person1.addPhone(new Phone(2L,"+992 2213443","home"));
		person2.addPhone(new Phone(3L,"+7 9533664341","mobile"));
		contacts = Stream.of(person1,person2).collect(Collectors.toList());

	}


	@Test
	public void testGetContacts() throws Exception {

		when(contactService.getContacts()).thenReturn(contacts);
		mockMvc.perform(get("/contacts")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(contacts)))
				.andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}
	
	@Test
	public void testGetContact() throws Exception {
		when(contactService.findOne(1L)).thenReturn(contacts.get(0));

		mockMvc.perform(get("/contacts/{id}",1L)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
				.andExpect(MockMvcResultMatchers.jsonPath("$").exists())
				.andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	@Test
	public void testSaveContact() throws Exception {

		String contactJson=objectMapper.writeValueAsString(createPerson());
		mockMvc.perform(post("/contacts")
				.content(contactJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
		verify(contactService).save(ArgumentMatchers.any(Person.class));
	}

	@Test
	public void testUpdateContact()throws Exception{
		when(contactService.findOne(1L)).thenReturn(createPerson());
		String contactsJson=new ObjectMapper().writeValueAsString(createPerson());
		mockMvc.perform(put("/contacts/{id}",1L)
				.content(contactsJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
		verify(contactService).update(ArgumentMatchers.any(Person.class));
	}

	@Test
	public void testDeleteContact()throws Exception{

		mockMvc.perform(delete("/contacts/{id}", 9) )
				.andExpect(status().isOk())
				.andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	@Test
	public void testSearchContact() throws Exception {

		Person person=new Person(1L,"Dilshod","Mizrobov","dilshod@gmail.com",
				"Java Developer","anor.tj");

		person.addPhone(new Phone(1L,"+992 91540323","mobile"));
		person.addPhone(new Phone(2L,"+992 2213443","home"));
		contacts = Stream.of(person).collect(Collectors.toList());

		when(contactService.searchFullText("Dilshod")).thenReturn(contacts);

		mockMvc.perform(get("/contacts/search").param("searchQuery", "Dilshod")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andDo(document("{methodName}",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));
	}

	private Person createPerson(){
		Person person=new Person("Dilshod","Mizrobov","dilshod@gmail.com",
				"Java Developer","anor.tj");

		person.addPhone(new Phone("+992 91540323","mobile"));
		person.addPhone(new Phone("+992 2213443","home"));
		return person;
	}

}
