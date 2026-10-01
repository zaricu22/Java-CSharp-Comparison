// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

namespace Shop.T17_Variance;

public static class Demo
{
    public static void Run()
    {
        List<Book> books = [new("Clean Code", 35.50m, 464), new("Refactoring", 42.00m, 448)];
        Console.WriteLine($"Books total: {Variance.Total(books)}");

        List<ISellable> basket = [];
        Variance.CopyMatching(books, basket, b => b.Price < 40m);
        Variance.AddFreebie(basket.Add);
        Console.WriteLine($"Basket: [{string.Join(", ", basket.Select(s => s.Title))}]");

        IComparer<ISellable> byPrice = Comparer<ISellable>.Create((a, b) => a.Price.CompareTo(b.Price));
        List<Ebook> ebooks = [new("Kotlin in Action", 30m, 12), new("Effective Java", 28m, 9)];
        Console.WriteLine($"Cheapest ebook: {Variance.Cheapest(ebooks, byPrice).Title}");
    }
}
