-- WordRepository
CREATE TABLE Word(
  word_id SERIAL CONSTRAINT word_word_id_pkey PRIMARY KEY,
  word_text TEXT NOT NULL
);
CREATE UNIQUE INDEX word_text_index ON Word (text);

-- FrequencyInformationRepository
CREATE TABLE Frequency(
  word_id SERIAL CONSTRAINT frequency_word_id_fkey REFERENCES Word (word_id),
  area_id INTEGER NOT NULL,
  word_count INTEGER NOT NULL,
  CONSTRAINT frequency_word_id_area_id_pkey PRIMARY KEY (word_id, area_id)
);
CREATE INDEX frequency_word_id_index ON Frequency (word_id);