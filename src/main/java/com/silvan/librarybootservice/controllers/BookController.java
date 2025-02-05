package com.silvan.librarybootservice.controllers;

import com.silvan.librarybootservice.models.Book;
import com.silvan.librarybootservice.models.Person;
import com.silvan.librarybootservice.services.BooksService;
import com.silvan.librarybootservice.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/books")
public class BookController {
    private final BooksService booksService;
    private final PeopleService peopleService;

    public BookController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model,
                        @RequestParam(value = "page", required = false)Integer page,
                        @RequestParam(value = "books_per_page",required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year",required = false) boolean sortByYear){

        if (page == null || booksPerPage == null)
            model.addAttribute("books",booksService.findAll(sortByYear));
        else
            model.addAttribute("books",booksService.findWithPagination(page,booksPerPage,sortByYear));

        return "books/index";
    }

    @GetMapping("/{book_id}")
    public String show(@PathVariable("book_id") int book_id,
                       Model model,
                       @ModelAttribute("person") Person person){
        model.addAttribute("book", booksService.show(book_id));

        Person bookOwner = booksService.getBookOwner(book_id);

        if (bookOwner != null)
            model.addAttribute("owner", bookOwner);
        else
            model.addAttribute("people", peopleService.findAll());

        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book){
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.save(book);
        return "redirect:/books";
    }

    @PatchMapping("/{book_id}/edit")
    public String edit(Model model, @PathVariable("book_id") int book_id){
        model.addAttribute("book", booksService.show(book_id));
        return "books/edit";
    }

    @PatchMapping("/{book_id}")
    public String update(@ModelAttribute("book") Book book,
                         BindingResult bindingResult,
                         @PathVariable("book_id") int book_id){
        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(book_id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{book_id}")
    public String delete(@PathVariable("book_id") int book_id){
        booksService.delete(book_id);
        return "redirect:/books";
    }

    @PatchMapping("/{book_id}/release")
    public String release(@PathVariable("book_id") int book_id){
        booksService.release(book_id);
        return "redirect:/books/{book_id}";
    }

    @PatchMapping("/{book_id}/assign")
    public String assign(@PathVariable("book_id") int book_id,
                         @ModelAttribute("person") Person selectedPerson){
        booksService.assign(book_id, selectedPerson);
        return "redirect:/books/{book_id}";
    }

    @GetMapping("/search")
    public String searchPage(){
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model,@RequestParam("query") String query){
        model.addAttribute("books",booksService.findByTitle(query));
        return "books/search";
    }
}
