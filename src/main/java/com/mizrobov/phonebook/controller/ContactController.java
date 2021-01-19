package com.mizrobov.phonebook.controller;

import com.mizrobov.phonebook.entity.Person;
import com.mizrobov.phonebook.entity.Phone;
import com.mizrobov.phonebook.exception.ApiError;
import com.mizrobov.phonebook.exception.UserNotFoundException;
import com.mizrobov.phonebook.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private static Logger logger = LoggerFactory.getLogger(ContactController.class);
    private ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("")
    public ResponseEntity<List<Person>> getContacts() {
        HttpHeaders headers =new HttpHeaders();
        List<Person> contacts = contactService.getContacts();
        headers.add("X-Contact-Total",Long.toString(contacts.stream().count()));
        return new ResponseEntity<>(contacts, headers, HttpStatus.OK);
    }
    @GetMapping(value = "/search",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> searchFullText(@RequestParam String keyWord){
        return contactService.searchFullText(keyWord);
    }
    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Person getContact(@PathVariable Long id){
        return contactService.findOne(id);
    }

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> saveContact(@RequestBody Person person, UriComponentsBuilder componentsBuilder){
        this.contactService.save(person);
        return ResponseEntity.created(componentsBuilder.path("/contacts/{id}").build(person.getId())).build();
    }

    @PutMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public Person updateContact(@RequestBody Person person,@PathVariable("id") Long id){
        Person oldPerson=contactService.findOne(id);
        if (oldPerson!=null) {
            oldPerson.setFistName(person.getFistName());
            oldPerson.setLastName(person.getLastName());
            oldPerson.setEmail(person.getEmail());
            oldPerson.setJobTitle(person.getJobTitle());
            oldPerson.setCompamy(person.getCompamy());

            oldPerson.getPhones().clear();
            if (!person.getPhones().isEmpty()){
                person.getPhones().forEach(phone -> {
                    oldPerson.addPhone(phone);
                });
            }
            return contactService.update(oldPerson);
        }
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable("id") Long id){
        contactService.delete(id);
        return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    }

    @GetMapping(value = "/{personId}/phones")
    public List<Phone> getContactPhones(@PathVariable Long personId){
        return contactService.getContactPhones(personId);
    }

    @GetMapping(value = "/{personId}/phones/{phoneId}")
    public Phone getContactPhone(@PathVariable Long personId,@PathVariable Long phoneId){
        return contactService.getPone(personId, phoneId);
    }

    @PostMapping(value ="/{personId}/phones" ,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Phone> savePhone(@RequestBody Phone phone, @PathVariable Long personId, UriComponentsBuilder componentsBuilder){
        Person person = contactService.findOne(personId);
        logger.warn(person.toString());

        if (person!=null){
            phone.setPerson(person);
            contactService.savePhone(phone);
            return ResponseEntity.created(componentsBuilder.path("/contacts/{Id}/phones/{id}").build(person.getId(),phone.getId())).build();
        }
        return null;
    }

    @PutMapping(value = "/{personId}/phones/{phoneId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public Phone updateContactPhone(@RequestBody Phone phone,@PathVariable("personId") Long personId,
                                    @PathVariable("phoneId") Long phoneId){
        Person person = contactService.findOne(personId);
        if (person!=null){
            Phone oldPhone = contactService.getPone(personId, phoneId);
            if (oldPhone!=null){
                oldPhone.setNumber(phone.getNumber());
                oldPhone.setType(phone.getType());
                return contactService.savePhone(oldPhone);
            }
            return null;
        }
        return null;
    }

    @DeleteMapping(value = "/{personId}/phones/{phoneId}")
    public ResponseEntity<HttpStatus> deleteContactPhone(@PathVariable("personId") Long personId,@PathVariable("phoneId") Long phoneId){
        contactService.deletePhone(personId,phoneId);
        return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public List<ApiError> handleNotFoundExceptions(UserNotFoundException ex) {
        return Collections.singletonList(new ApiError("user.notfound", ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public List<ApiError> handleOtherException(Exception ex) {
        return Collections.singletonList(new ApiError(ex.getClass().getCanonicalName(), ex.getMessage()));
    }

}
