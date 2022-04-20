DROP TABLE IF EXISTS crimes_by_district;

CREATE TABLE crimes_by_district
STORED AS PARQUET
LOCATION './src/main/resources/data/output/crimes_by_district/'
AS
SELECT
    district_name,
    COUNT(*) AS count_crimes
FROM crimes_output
GROUP BY 1
CLUSTER BY '' -- to put everything in single file
;

DROP TABLE IF EXISTS crimes_by_crime_type;

CREATE TABLE crimes_by_crime_type
STORED AS PARQUET
LOCATION './src/main/resources/data/output/crimes_by_crime_type/'
AS
SELECT
    crime_type,
    COUNT(*) AS count_crimes
FROM crimes_output
GROUP BY 1
CLUSTER BY '' -- to put everything in single file
;

DROP TABLE IF EXISTS crimes_by_last_outcome;

CREATE TABLE crimes_by_last_outcome
STORED AS PARQUET
LOCATION './src/main/resources/data/output/crimes_by_last_outcome/'
AS
SELECT
    last_outcome,
    COUNT(*) AS count_crimes
FROM crimes_output
GROUP BY 1
CLUSTER BY '' -- to put everything in single file
;