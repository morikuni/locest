CREATE TABLE area(
  area_id INTEGER CONSTRAINT area_area_id_pkey PRIMARY KEY,
	prefecture VARCHAR(10) NOT NULL,
	city VARCHAR(20) NOT NULL,
	center GEOMETRY(POINT, 4326) NOT NULL
);
CREATE TABLE shape(
	area_id INTEGER CONSTRAINT shape_area_id_fkey REFERENCES Area(area_id),
	shape GEOMETRY(POLYGON, 4326) NOT NULL UNIQUE
);
CREATE INDEX shape_shape_index ON shape USING GiST(shape);

