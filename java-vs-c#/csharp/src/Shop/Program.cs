// VERDICT | Demo runner | BETTER: -
// WHY: runs every topic's Demo in order; pass a topic number (e.g. 04) to run just one.

(string Name, Func<Task> Run)[] demos =
[
    ("01 Properties & records", Sync(Shop.T01_Properties.Demo.Run)),
    ("02 Strings", Sync(Shop.T02_Strings.Demo.Run)),
    ("03 Type system & generics", Sync(Shop.T03_Types.Demo.Run)),
    ("04 LINQ vs Streams", Sync(Shop.T04_Queries.Demo.Run)),
    ("05 Operators & value types", Sync(Shop.T05_Operators.Demo.Run)),
    ("06 Extension methods", Sync(Shop.T06_Extensions.Demo.Run)),
    ("07 Events & functions", Sync(Shop.T07_Events.Demo.Run)),
    ("08 Async", Shop.T08_Async.Demo.RunAsync),
    ("09 Pattern matching", Sync(Shop.T09_Patterns.Demo.Run)),
    ("10 Method parameters", Sync(Shop.T10_Parameters.Demo.Run)),
    ("11 Indexers & ranges", Sync(Shop.T11_Indexers.Demo.Run)),
    ("12 Exception filters & overflow", Sync(Shop.T12_Errors.Demo.Run)),
    ("13 Compile-time features", Sync(Shop.T13_CompileTime.Demo.Run)),
    ("14 Checked exceptions", Sync(Shop.T14_Checked.Demo.Run)),
    ("15 Enums", Sync(Shop.T15_Enums.Demo.Run)),
    ("16 Anonymous & inner classes", Sync(Shop.T16_InnerClasses.Demo.Run)),
    ("17 Generic variance", Sync(Shop.T17_Variance.Demo.Run)),
    ("18 Null safety", Sync(Shop.T18_NullSafety.Demo.Run)),
    ("19 Iterators & generators", Shop.T19_Iterators.Demo.RunAsync),
    ("20 Expression trees", Sync(Shop.T20_Expressions.Demo.Run)),
    ("21 Memory & value types", Sync(Shop.T21_Memory.Demo.Run)),
    ("22 Resource cleanup", Sync(Shop.T22_Resources.Demo.Run)),
    ("23 Collection expressions", Sync(Shop.T23_Collections.Demo.Run)),
    ("24 Labeled break & continue", Sync(Shop.T24_Loops.Demo.Run)),
];

var only = args.FirstOrDefault();
foreach (var (name, run) in demos.Where(d => only is null || d.Name.StartsWith(only)))
{
    Console.WriteLine($"== {name} ==");
    await run();
    Console.WriteLine();
}

static Func<Task> Sync(Action action) => () =>
{
    action();
    return Task.CompletedTask;
};
