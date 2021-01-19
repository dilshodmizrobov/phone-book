package com.mizrobov.phonebook.service.impl;

import com.mizrobov.phonebook.dao.PersonRepository;
import com.mizrobov.phonebook.dao.PhoneRepository;
import com.mizrobov.phonebook.entity.Person;
import com.mizrobov.phonebook.entity.Phone;
import com.mizrobov.phonebook.exception.UserNotFoundException;
import com.mizrobov.phonebook.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private PersonRepository personRepository;
    private PhoneRepository phoneRepository;

    @Autowired
    public ContactServiceImpl(PersonRepository personRepository, PhoneRepository phoneRepository) {
        this.personRepository = personRepository;
        this.phoneRepository = phoneRepository;
    }

    @Override
    public Person findOne(Long id) {
        return personRepository.findById(id).orElseThrow(()->new UserNotFoundException("User with id '" + id + "' is not found"));
    }

    @Override
    public Person save(Person person) {
       return personRepository.save(person);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public Person update(Person person) {
        return personRepository.save(person);
    }

    @Override
    public void delete(Long id) {
        Person person=findOne(id);
        personRepository.delete(person);
    }

    @Override
    public List<Person> getContacts() {
        return (List<Person>) personRepository.findAll();
    }

    @Override
    public List<Phone> getContactPhones(Long personId) {
        return phoneRepository.getAllByPersonId(personId);
    }

    @Override
    public Phone getPone(Long personId, Long phoneId) {
        return phoneRepository.findByIdAndPersonId(phoneId,personId);
    }

    @Override
    public Phone savePhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    @Override
    public Phone updatePhone(Long personId, Phone person) {
        return null;
    }

    @Override
    public void deletePhone(Long personId, Long phoneId) {
        phoneRepository.deletePhoneByIdAndPersonId(phoneId, personId);
    }

    @Override
    public List<Person> searchFullText(String query) {
        return personRepository.searchFullText(query).stream().distinct().collect(Collectors.toList());
    }
}
