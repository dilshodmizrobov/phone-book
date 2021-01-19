package com.mizrobov.phonebook.service;

import com.mizrobov.phonebook.entity.Person;
import com.mizrobov.phonebook.entity.Phone;

import java.util.List;

public interface ContactService {

    public List<Person> getContacts();

    public Person findOne(Long id);

    public Person save(Person person);

    public Person update(Person person);

    public void delete(Long id);

    public List<Phone> getContactPhones(Long contactId);

    public Phone getPone(Long personId,Long phoneId);

    public Phone savePhone(Phone phone);

    public Phone updatePhone(Long personId,Phone person);

    public void deletePhone(Long personId,Long id);

    public List<Person> searchFullText(String query);

}

