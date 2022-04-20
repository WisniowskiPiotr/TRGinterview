DROP TABLE IF EXISTS crimes_output;

CREATE TABLE crimes_output
STORED AS PARQUET
LOCATION './src/main/resources/data/output/crimes_output/'
AS
SELECT
    crimes_input.crime_id,
    crimes_input.district_name,
    crimes_input.longitude,
    crimes_input.latitude,
    crimes_input.crime_type,
    NVL(crimes_input_outcomes.outcome_type, crimes_input.last_outcome_category) AS last_outcome
FROM crimes_input
LEFT JOIN crimes_input_outcomes
    ON crimes_input.crime_id = crimes_input_outcomes.crime_id
CLUSTER BY '' -- to put everything in single file
;