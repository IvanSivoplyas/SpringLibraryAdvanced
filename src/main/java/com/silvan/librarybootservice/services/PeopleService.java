package com.silvan.librarybootservice.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.silvan.librarybootservice.models.Book;
import com.silvan.librarybootservice.models.Person;
import com.silvan.librarybootservice.repositories.PeopleRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }
    public List<Person> findAll(){
        return peopleRepository.findAll();
    }

    public Person show(int id){
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }
    @Transactional
    public void save(Person person){
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id, Person updatedPerson){
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }
    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }
    public Optional<Person> getPersonByFullName(String fullName){
        return peopleRepository.findByFullName(fullName);
    }
    public List<Book> getBooksByPersonId(int id){
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()){
            Hibernate.initialize(person.get().getBooks());

            person.get().getBooks().forEach(book -> {
                long expiration = Math.abs(book.getTakenAt().getTime() - new Date().getTime());

                if (expiration > 604800000)
                    book.setExpired(true);
            });
            return person.get().getBooks();
        }
        else {
            return Collections.emptyList();
        }
    }

}
