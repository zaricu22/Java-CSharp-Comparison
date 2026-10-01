// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

namespace Shop.T16_InnerClasses;

public interface IDiscountRule
{
    string Name { get; }

    bool AppliesTo(decimal subtotal);

    decimal Discount(decimal subtotal);

    decimal Apply(decimal subtotal) => AppliesTo(subtotal) ? subtotal - Discount(subtotal) : subtotal;
}

public static class Discounts
{
    public static IDiscountRule OneTimePercent(decimal threshold, int percent) => new OneTimePercentRule(threshold, percent);

    /// <summary>
    /// C# has no anonymous interface implementations (anonymous types are data-only),
    /// so a stateful multi-method rule needs a named class.
    /// </summary>
    private sealed class OneTimePercentRule(decimal threshold, int percent) : IDiscountRule
    {
        private bool _used;

        public string Name => $"{percent}% once over {threshold}";

        public bool AppliesTo(decimal subtotal) => !_used && subtotal >= threshold;

        public decimal Discount(decimal subtotal)
        {
            _used = true;
            return Math.Round(subtotal * percent / 100, 2);
        }
    }
}

public sealed class ShoppingCart
{
    private readonly List<Item> _items = [];

    /// <summary>
    /// A nested class has no implicit link to an outer instance:
    /// the cart must be passed in, stored and referenced explicitly.
    /// </summary>
    public sealed class Item
    {
        private readonly ShoppingCart _cart;

        internal Item(ShoppingCart cart, Product product, int quantity)
        {
            _cart = cart;
            Product = product;
            Quantity = quantity;
        }

        public Product Product { get; }

        public int Quantity { get; private set; }

        public decimal LineTotal => Product.Price * Quantity;

        public decimal ShareOfCart => Math.Round(LineTotal * 100 / _cart.Subtotal, 1);

        public void Increase() => Quantity++;

        public void Remove() => _cart._items.Remove(this);
    }

    public Item Add(Product product, int quantity)
    {
        var item = new Item(this, product, quantity);
        _items.Add(item);
        return item;
    }

    public decimal Subtotal => _items.Sum(i => i.LineTotal);

    public int Count => _items.Count;
}
