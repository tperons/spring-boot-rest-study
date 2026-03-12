INSERT INTO tb_person_book (person_id, book_id)
SELECT
    p.id AS person_id,
    b.id AS book_id
FROM
    (SELECT id FROM tb_person WHERE id <= 12) p
CROSS JOIN
    (SELECT id FROM tb_book ORDER BY RANDOM() LIMIT 10) b;

INSERT INTO tb_person_book (person_id, book_id)
SELECT
    p.id AS person_id,
    b.id AS book_id
FROM
    (SELECT id FROM tb_person WHERE id > 12) p
CROSS JOIN
    (SELECT id FROM tb_book ORDER BY RANDOM() LIMIT 3) b;
