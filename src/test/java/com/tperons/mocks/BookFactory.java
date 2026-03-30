package com.tperons.mocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tperons.dto.BookDTO;
import com.tperons.entity.Book;

public class BookFactory {

    private static final int DEFAULT_MOCK_LIST_SIZE = 12;

    private static final List<String> TITLES = List.of(
            "Clean Code", "Effective Java", "The Pragmatic Programmer",
            "Design Patterns", "Refactoring", "Domain-Driven Design",
            "The Clean Coder", "Working Effectively with Legacy Code",
            "Head First Design Patterns", "Java Concurrency in Practice",
            "Continuous Delivery", "Release It!", "Building Microservices",
            "Software Engineering at Google");

    private static final List<String> AUTHORS = List.of(
            "Robert C. Martin", "Joshua Bloch", "Andy Hunt",
            "Erich Gamma", "Martin Fowler", "Eric Evans",
            "Robert C. Martin", "Michael Feathers",
            "Elisabeth Freeman", "Brian Goetz",
            "Jez Humble", "Michael Nygard", "Sam Newman",
            "Titus Winters");

    private BookFactory() {
    }

    public static Book createMockEntity() {
        return createMockEntity(0);
    }

    public static BookDTO createMockDTO() {
        return createMockDTO(0);
    }

    public static List<Book> createMockEntityList() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < DEFAULT_MOCK_LIST_SIZE; i++) {
            books.add(createMockEntity(i));
        }
        return books;
    }

    public static List<BookDTO> createMockDTOList() {
        List<BookDTO> books = new ArrayList<>();
        for (int i = 0; i < DEFAULT_MOCK_LIST_SIZE; i++) {
            books.add(createMockDTO(i));
        }
        return books;
    }

    public static Book createMockEntity(int number) {
        int index = Math.abs(number) % TITLES.size();

        Book book = new Book(
                (long) number,
                TITLES.get(index),
                AUTHORS.get(index),
                LocalDate.now().minusYears(number),
                resolvePrice(number));

        return book;
    }

    public static BookDTO createMockDTO(int number) {
        Book book = createMockEntity(number);
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getLaunchDate(),
                book.getPrice());
    }

    private static double resolvePrice(int number) {
        return Math.round((19.99 + (number * Math.PI)) * 100.0) / 100.0;
    }

}
