package com.app.library.controllers;

import com.app.library.models.Book;
import com.app.library.models.Member;
import com.app.library.models.BorrowingRecord;
import com.app.library.services.LibraryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/api")
public class LibraryController {

    // Create a logger instance
    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @Autowired
    private LibraryService libraryService;

    // ==================== Book Endpoints ====================

    // Get all books
    @GetMapping("/books")
    public ResponseEntity<Collection<Book>> getAllBooks() {
        Collection<Book> books = libraryService.getAllBooks();
        logger.info("The list of books returned"+books);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Get a book by ID
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = libraryService.getBookById(id);
        logger.info("The book returned"+book);

		if(book != null) {
			return new ResponseEntity<>(book, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }

    // Add new books
    @PostMapping("/books")
    public ResponseEntity<Integer> addBook(@RequestBody Book[] books) {
        libraryService.addBooks(books);

        logger.info("The books were added");
        return new ResponseEntity<>(libraryService.getAllBooks().size(), HttpStatus.CREATED);
    }

    // Update a book
    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        if (libraryService.getBookById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedBook.setId(id);
        libraryService.updateBook(updatedBook);
        logger.info("The book has been updated "+updatedBook);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    // Delete a book
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (libraryService.getBookById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        libraryService.deleteBook(id);
        logger.info("The book has been deleted ");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //It accepts a query parameter genre, for example, /books/genre?genre=Fiction.
    @GetMapping("/books/genre")
    public ResponseEntity<Collection<Book>> getBooksByGenre(@RequestParam String genre){
        return new ResponseEntity<>(libraryService.getBooksByGenre(genre), HttpStatus.OK);
    }

    //An optional query parameter genre (e.g., /books/author/Harper%20Lee?genre=Fiction)
    @GetMapping("/books/author/{author}")
    public ResponseEntity<Collection<Book>> checkGenreForAuthor(
            @PathVariable String author,
            @RequestParam(required = false) String genre) {
        Collection<Book> books = libraryService.getBooksByAuthorAndGenre(author, genre);
        logger.info("The books retrieved for the author and genre {} - {}", author, genre);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    //It accepts a query parameter dueDate in the format dd/MM/yyyy. For example, /books/dueondate?dueDate=20/03/2025).
    @GetMapping("/books/dueondate")
    public ResponseEntity<Collection<Book>> getBooksDueOnDate(
            @RequestParam("dueDate") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dueDate) {
        Collection<Book> books = libraryService.getBooksDueOnDate(dueDate);
        logger.info("The books retrieved by due date {}", dueDate);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    //It accepts a query parameter bookId (e.g., /bookavailabileDate?bookId=1)
    @GetMapping("/bookavailabileDate")
    public ResponseEntity<LocalDate> checkAvailability(
            @RequestParam Long bookId) {
        LocalDate avlDate = libraryService.checkAvailability(bookId);
        if(avlDate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(avlDate, HttpStatus.OK);
        }
    }

    // ==================== Member Endpoints ====================

    // Get all members
    @GetMapping("/members")
    public ResponseEntity<Collection<Member>> getAllMembers() {
        Collection<Member> members = libraryService.getAllMembers();
        logger.info("The members in the system " + members);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    // Get a member by ID
    @GetMapping("/members/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = libraryService.getMemberById(id);
        logger.info("The member you retrieved "+member);
		if(member != null) {
			return new ResponseEntity<>(member, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }

    // Add new members
    @PostMapping("/members")
    public ResponseEntity<Integer> addMember(@RequestBody Member[] members) {
            libraryService.addMember(members);

        logger.info("The members have been added ");
        return new ResponseEntity<>(libraryService.getAllMembers().size(), HttpStatus.CREATED);
    }

    // Update a member
    @PutMapping("/members/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member updatedMember) {
        if (libraryService.getMemberById(id) != null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedMember.setId(id);
        libraryService.updateMember(updatedMember);
        logger.info("The member has been updated "+updatedMember);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    // Delete a member
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (libraryService.getMemberById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        libraryService.deleteMember(id);
        logger.info("The member has been deleted "+id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ==================== BorrowingRecord Endpoints ====================

    // Get all borrowing records
    @GetMapping("/borrowing-records")
    public ResponseEntity<Collection<BorrowingRecord>> getAllBorrowingRecords() {
        Collection<BorrowingRecord> records = libraryService.getAllBorrowingRecords();
        logger.info("The records has been retrieved "+records);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<BorrowingRecord> borrowBook(@RequestBody BorrowingRecord record) {
        // Set borrow date and due date (e.g., due date = borrow date + 14 days)
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        libraryService.borrowBook(record);
        logger.info("The book has been borrowed "+record);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    // Return a book
    @PutMapping("/return/{recordId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long recordId) {
        libraryService.returnBook(recordId, LocalDate.now());
        logger.info("The book has been retrieved "+recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
