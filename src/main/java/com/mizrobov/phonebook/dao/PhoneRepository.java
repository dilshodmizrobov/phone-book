package com.mizrobov.phonebook.dao;

import com.mizrobov.phonebook.entity.Person;
import com.mizrobov.phonebook.entity.Phone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneRepository extends CrudRepository<Phone,Long> {
    public List<Phone> getAllByPersonId(Long personeId);

    public Phone findByIdAndPersonId(Long id, Long personId);

    public void deletePhoneByIdAndPersonId(Long phoneId, Long personId);

}
