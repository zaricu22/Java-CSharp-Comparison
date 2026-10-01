// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

using System.Text.RegularExpressions;

namespace Shop.T06_Extensions;

/// <summary>Classic extension methods ("this" parameter): called as if they were on string.</summary>
public static class StringExtensions
{
    public static string Truncate(this string s, int max) => s.Length <= max ? s : s[..max] + "...";

    public static string ToSlug(this string s) =>
        Regex.Replace(s.ToLowerInvariant().Trim(), "[^a-z0-9]+", "-").Trim('-');
}

/// <summary>C# 14 extension blocks: extension methods AND extension properties for a type.</summary>
public static class OrderExtensions
{
    extension(IEnumerable<Order> orders)
    {
        public IEnumerable<Order> PlacedIn(int year, int month) =>
            orders.Where(o => o.Date.Year == year && o.Date.Month == month);

        public IEnumerable<Order> From(string city) => orders.Where(o => o.Customer.City == city);

        public decimal Revenue => orders.Sum(o => o.Total);
    }
}
