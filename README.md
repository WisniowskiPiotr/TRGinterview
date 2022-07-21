# TRGInterview

## Engine code:
In `/src/main/scala` there is a code for spark sql runner engine - app that allows to run sql files taken from resources folder on local spark cluster.
The app is written in purely functional manner by using ZIO lib. To run the engine simply `run /list/of /sql/files`

## Transformation code:
Transformations used to transform the data and produce final results are present in `/src/main/resources/sqls`. it is assumed that the input data should be present in `/src/main/resources/data/input`.
The output will be generated into `/src/main/resources/data/output/`.

## Running
To run the computation simply put the input data in `/src/main/resources/data/input/` and on sbt console: `run "/sqls/01_load_the_data.sql" "/sqls/02_create_output_table.sql" "/sqls/03_compute_kpis.sql"`.

## Notes:
- on the page from which the data should be taken - it is possible to get data since March 2019. It seems that earlier data is not available.
- also the names of the downloaded fles are a bit diferent than the ones in the instruction (files end either in `-outcomes.csv` or `-street.csv`)
- in the Main.scala there exists some boilerplate that potentially should be eliminated. This is left only to develop the code faster.
- no unit tests are present as in matter of time. Typically such generic app should be written in BDD in mind (TDD + real specifications), but to save time I did not do this.
- the scala code is very generic and written by using functional programming with ZIO library - this allows for full testing (even loggin could be tested) ass all dependencies are ingested on top layer.
- the actual logic to do the computation of the data is written in the SQL files - typically for real development this files would also require unit tests.
