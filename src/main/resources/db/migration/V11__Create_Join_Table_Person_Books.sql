CREATE TABLE public.tb_person_book (
  person_id bigint NOT NULL,
  book_id bigint NOT NULL,
  PRIMARY KEY (person_id, book_id),
  CONSTRAINT fk_person_book_person FOREIGN KEY (person_id) REFERENCES public.tb_person(id) ON DELETE CASCADE,
  CONSTRAINT fk_person_book_book FOREIGN KEY (book_id) REFERENCES public.tb_book(id) ON DELETE CASCADE
);
