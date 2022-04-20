CREATE TEMPORARY VIEW crimes_input_raw
USING csv
OPTIONS (
    path 'src/main/resources/data/input/*/*-street.csv',
    header true,
    nullValue ''
);

CREATE TEMPORARY VIEW crimes_input AS
WITH final AS (
    SELECT
        REPLACE( SUBSTRING( input_file_name(), LOCATE( 'input/' , input_file_name())+6+8+8) , '-street.csv', '') AS district_name,
        `Crime ID` AS crime_id,
        `Month` AS month_string,
        `Reported by` AS reported_by,
        `Falls within` AS falls_within,
        CAST(`Longitude` AS DOUBLE) AS longitude,
        CAST(`Latitude` AS DOUBLE) AS latitude,
        `Location` AS location,
        `LSOA code` AS lsoa_code,
        `LSOA name` AS lsoa_name,
        `Crime type` AS crime_type,
        `Last outcome category` AS last_outcome_category,
        `Context` AS context
    FROM crimes_input_raw
)
SELECT * FROM final
;

CREATE TEMPORARY VIEW crimes_input_raw_outcomes
USING csv
OPTIONS (
    path 'src/main/resources/data/input/*/*-outcomes.csv',
    header true,
    nullValue ''
);

CREATE TEMPORARY VIEW crimes_input_outcomes AS
WITH final AS (
    SELECT
        REPLACE( SUBSTRING( input_file_name(), LOCATE( 'input/' , input_file_name())+6+8+8) , '-outcomes.csv', '') AS district_name,
        `Crime ID` AS crime_id,
        `Month` AS month_string,
        `Reported by` AS reported_by,
        `Falls within` AS falls_within,
        CAST(`Longitude` AS DOUBLE) AS longitude,
        CAST(`Latitude` AS DOUBLE) AS latitude,
        `Location` AS location,
        `LSOA code` AS lsoa_code,
        `LSOA name` AS lsoa_name,
        `Outcome type` AS outcome_type
    FROM crimes_input_raw_outcomes
)
SELECT * FROM final
;
