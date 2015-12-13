CREATE TABLE Area(
  area_id INTEGER CONSTRAINT area_area_id_pkey PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	center GEOMETRY(POINT, 4326) NOT NULL
);
CREATE TABLE Shape(
	area_id INTEGER CONSTRAINT shape_area_id_fkey REFERENCES Area(area_id),
	shape GEOMETRY(POLYGON, 4326) NOT NULL
);
CREATE INDEX shape_shape_index ON shape USING GiST(shape);