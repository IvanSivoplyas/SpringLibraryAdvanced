package com.silvan.librarybootservice.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.silvan.librarybootservice.models.Book;
import com.silvan.librarybootservice.models.Person;
import com.silvan.librarybootservice.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;

    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sortByYear){
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else
            return booksRepository.findAll();
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage,boolean sortbyYear){
        if (sortbyYear)
            return booksRepository.findAll(PageRequest.of(page,booksPerPage, Sort.by("year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page,booksPerPage)).getContent();
    }
    public Book show(int bookId){
        Optional<Book> foundBook = booksRepository.findById(bookId);
        return foundBook.orElse(null);
    }
    public List<Book> findByTitle(String query){
        return booksRepository.findByTitleStartingWith(query);
    }
    @Transactional
    public void save(Book book){
        booksRepository.save(book);
    }
    @Transactional
    public void update(int bookId, Book updatedBook){
        Book bookForUpdate = booksRepository.findById(bookId).get();

        updatedBook.setBookId(bookId);
        updatedBook.setOwner(bookForUpdate.getOwner());

        booksRepository.save(updatedBook);
    }
    @Transactional
    public void delete(int bookId){
        booksRepository.deleteById(bookId);
    }
    public Person getBookOwner(int bookId){
        return booksRepository.findById(bookId).map(Book::getOwner).orElse(null);
    }
    @Transactional
    public void release(int bookId){
        booksRepository.findById(bookId).ifPresent(book -> {book.setOwner(null);
        book.setTakenAt(null);});
    }
    @Transactional
    public void assign(int bookId, Person selectedPerson){
        booksRepository.findById(bookId).ifPresent(book -> {
            book.setOwner(selectedPerson);
            book.setTakenAt(new Date());
        });
    }

}
