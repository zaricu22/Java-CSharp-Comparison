// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

namespace Shop.T17_Variance;

public class VarianceTests
{
    private readonly List<Book> _books = [new("Clean Code", 35.50m, 464), new("Refactoring", 42.00m, 448)];

    [Fact]
    public void ProducerAcceptsListOfSubtype()
    {
        Assert.Equal(77.50m, Variance.Total(_books));
    }

    [Fact]
    public void ConsumerNeedsDelegateWorkaround()
    {
        List<object> anything = [];
        List<ISellable> sellables = [];
        List<Ebook> ebooks = [];

        // Variance.AddFreebie(sellables) with an ICollection<Ebook> parameter would not compile
        Variance.AddFreebie(anything.Add);
        Variance.AddFreebie(sellables.Add);
        Variance.AddFreebie(ebooks.Add);

        Assert.IsType<Ebook>(anything[0]);
        Assert.Equal("Java Cheat Sheet", sellables[0].Title);
        Assert.Single(ebooks);
    }

    [Fact]
    public void CopyFromSubtypeListIntoSupertypeList()
    {
        List<ISellable> basket = [];
        Variance.CopyMatching(_books, basket, b => b.Price < 40m);
        Assert.Equal(["Clean Code"], basket.Select(s => s.Title));
    }

    [Fact]
    public void ComparerOfSupertypeWorksForSubtype()
    {
        IComparer<ISellable> byPrice = Comparer<ISellable>.Create((a, b) => a.Price.CompareTo(b.Price));
        List<Ebook> ebooks = [new("Kotlin in Action", 30m, 12), new("Effective Java", 28m, 9)];
        Assert.Equal("Effective Java", Variance.Cheapest(ebooks, byPrice).Title);
    }
}
