package com.mizrobov.phonebook.dao;

import com.mizrobov.phonebook.entity.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person,Long> {
    @Query(nativeQuery = true, value = "select distinct * from persons p left join phones pn on (p.id=pn.person_id) where concat(p.first_name,' ',p.last_name) like %?1% or pn.number like %?1%")
    public List<Person> searchFullText(String keyWord);
}
