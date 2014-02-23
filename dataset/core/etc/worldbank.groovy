columns = columnSet(array([
	"country", Datatype.STRING, 
	"year", Datatype.NUMERIC, 
	"co2", Datatype.NUMERIC,
	"electric", Datatype.NUMERIC,
	"energy", Datatype.NUMERIC,
	"fertility", Datatype.NUMERIC,
	"gni", Datatype.NUMERIC,
	"internet", Datatype.NUMERIC,
	"lifespan", Datatype.NUMERIC,
	"military", Datatype.NUMERIC,
	"population", Datatype.NUMERIC,
	"hiv", Datatype.NUMERIC
]));

wb_ds = Csv.obj()
	.columns(columns)
	.skipLines(1)
	.file(new java.io.File("etc/worldbank.csv"))
	.build();
	
results = Stats.summary(wb_ds, wb_ds.columnSet.excludes(["year", "country"]).columnNames)

echo(results)

sorted = Sort.asc(wb_ds, ["year", "country"])