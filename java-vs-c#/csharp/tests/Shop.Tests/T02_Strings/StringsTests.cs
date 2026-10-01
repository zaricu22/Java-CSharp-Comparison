// VERDICT | T02 Strings | BETTER: C#
// WHY: interpolation puts each value where it is printed ({x,-12} {y:F2}); Java only has positional %s placeholders.

namespace Shop.T02_Strings;

public class StringsTests
{
    [Fact]
    public void InvoiceLineIsPaddedAndAligned()
    {
        var line = SampleData.Orders[0].Lines[0];
        Assert.Equal("Clean Code   x 2    71.00", InvoiceFormatter.Line(line));
    }

    [Fact]
    public void GreetingPluralises()
    {
        Assert.Equal("Hello Ana, you have 1 order.", InvoiceFormatter.Greeting("Ana", 1));
        Assert.Equal("Hello Ana, you have 2 orders.", InvoiceFormatter.Greeting("Ana", 2));
    }

    [Fact]
    public void InvoiceHasHeaderLinesAndTotal()
    {
        // Raw strings keep the source file's line endings (Java text blocks always use \n)
        var lines = InvoiceFormatter.Invoice(SampleData.Orders[0]).ReplaceLineEndings("\n").Split('\n');
        Assert.Equal(6, lines.Length);
        Assert.Equal("INVOICE #1", lines[0]);
        Assert.Equal("Date:     2026-01-10", lines[2]);
        Assert.Equal("TOTAL:    160.99", lines[5]);
    }

    [Fact]
    public void JsonContainsQuotedValues()
    {
        Assert.Equal("""{"id": 1, "customer": "Ana", "total": 160.99}""", InvoiceFormatter.ToJson(SampleData.Orders[0]));
    }
}
