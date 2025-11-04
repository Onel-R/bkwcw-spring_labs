package com.app.library.services;

import com.app.library.models.Book;
import com.app.library.models.Member;
import com.app.library.models.BorrowingRecord;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class LibraryService {

    private Map<Long, Book> books = new HashMap<Long, Book>();
    private Map<Long, Member> members = new HashMap<Long, Member>();
    private Map<Long, BorrowingRecord> borrowingRecords = new HashMap<Long, BorrowingRecord>();

    // ==================== Book Methods ====================

    // Get all books
    public Collection<Book> getAllBooks() {
        return books.values();
    }

    // Get a book by ID
    public Book getBookById(Long id) {
        return books.get(id);
    }

    // Add a new book
    public void addBooks(Book[] newBooks) {
        Map<Long, Book> newMapBooks = Arrays.stream(newBooks).collect(
                        Collectors.toMap(Book::getId, book -> book));

        books.putAll(newMapBooks);
    }

    // Update a book
    public void updateBook(Book updatedBook) {
        books.put(updatedBook.getId(), updatedBook);
    }

    // Delete a book by ID
    public void deleteBook(Long id) {
        books.remove(id);
    }

    public Collection<Book> getBooksByGenre(String genre) {
        Collection<Book> genreBooks = (Collection<Book>)books.values();
        return genreBooks.stream().filter(book -> (book.getGenre()).toLowerCase().contains(genre.toLowerCase())).collect(Collectors.toList());
    }

    public Collection<Book> getBooksByAuthorAndGenre(String author, String genre) {
        Collection<Book> allBooks = (Collection<Book>) (books.values());
        return allBooks.stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author)) // Filter by author
                .filter(book -> genre == null || book.getGenre().toLowerCase().contains(genre.toLowerCase())) // Optional filter by genre
                .collect(Collectors.toList());
    }

    public Collection<Book> getBooksDueOnDate(LocalDate dueDate) {
        Collection<BorrowingRecord> allRecords = (Collection<BorrowingRecord>)(borrowingRecords.values());
        ArrayList<Book> dueBooks = new ArrayList<Book>();
        Collection<BorrowingRecord> tempRecords = allRecords.stream()
                .filter(record -> record.getDueDate().equals(dueDate)) // Filter by dueDate
                .toList();
        // For each filtered record, find the corresponding book and add it to the dueBooks list
        for (BorrowingRecord record : tempRecords) {
            Book book = books.get(record.getBookId());
            if (book != null) {
                dueBooks.add(book); // Add the book to the dueBooks list
            }
        }
        return dueBooks;
    }

    public LocalDate checkAvailability(Long bookId) {
        Collection<BorrowingRecord> allRecords = (Collection<BorrowingRecord>)(borrowingRecords.values());
        Book bookToCheck = books.get(bookId);
        if (bookToCheck == null) {
            return null;
        } else {
            if (bookToCheck.getAvailableCopies() >= 1) {
                return LocalDate.now();
            } else {
                List<BorrowingRecord> sortedRecords = allRecords.stream()
                        .filter(record -> Objects.equals(record.getBookId(), bookId)) // Filter by bookId
                        .sorted((b1, b2) -> b1.getDueDate().compareTo(b2.getDueDate())) // Sort by dueDate (soonest to latest)
                        .toList();
                return sortedRecords.getFirst().getDueDate();
            }
        }
    }

    // ==================== Member Methods ====================

    // Get all members
    public Collection<Member> getAllMembers() {
        return members.values();
    }

    // Get a member by ID
    public Member getMemberById(Long id) {
        return members.get(id);
    }

    // Add a new member
    public void addMember(Member[] newMembers) {
        Map<Long, Member> newMapMembers = Arrays.stream(newMembers).collect(
                            Collectors.toMap(Member::getId, member -> member));

        members.putAll(newMapMembers);
    }

    // Update a member
    public void updateMember(Member updatedMember) {
        members.put(updatedMember.getId(), updatedMember);
    }

    // Delete a member by ID
    public void deleteMember(Long id) {
        members.remove(id);
    }

    // ==================== BorrowingRecord Methods ====================

    // Get all borrowing records
    public Collection<BorrowingRecord> getAllBorrowingRecords() {
        return borrowingRecords.values();
    }

    // Borrow a book (create a new borrowing record)
    public void borrowBook(BorrowingRecord record) {
        System.out.println(record);
        // Set borrow date and due date (e.g., due date = borrow date + 14 days)
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        borrowingRecords.put(record.getId(), record);
        // Decrease the available copies of the book
        System.out.println("record.getBookId() " + record.getBookId());
        System.out.println("book " + books.get(record.getBookId()));
        Book book = books.get(record.getBookId());
        book.setAvailableCopies(book.getAvailableCopies() - 1);
    }

    // Return a book (update the borrowing record with the return date)
    public void returnBook(Long recordId, LocalDate returnDate) {
        BorrowingRecord record = borrowingRecords.get(recordId);
        // Increase the available copies of the book
        Book book = books.get(record.getBookId());
        book.setAvailableCopies(book.getAvailableCopies() +1 );
    }
}
