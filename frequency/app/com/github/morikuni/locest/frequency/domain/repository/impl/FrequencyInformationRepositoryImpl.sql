CREATE TABLE Frequency(
  word_id INTEGER,
  area_id INTEGER,
  word_count INTEGER,
  CONSTRAINT frequency_word_id_area_id_pkey PRIMARY KEY (word_id, area_id)
);
CREATE INDEX frequency_word_id_index ON Frequency (word_id);