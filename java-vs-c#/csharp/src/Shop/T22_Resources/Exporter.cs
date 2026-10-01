// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

namespace Shop.T22_Resources;

/// <summary>A fake file that records what happens to it in a shared journal.</summary>
public sealed class ExportFile : IDisposable
{
    private readonly string _name;
    private readonly List<string> _journal;
    private readonly bool _failOnDispose;

    public ExportFile(string name, List<string> journal, bool failOnDispose)
    {
        (_name, _journal, _failOnDispose) = (name, journal, failOnDispose);
        journal.Add($"{name}: opened");
    }

    public void Write(string line)
    {
        if (line.Length == 0)
        {
            throw new InvalidOperationException("write failed");
        }
        _journal.Add($"{_name}: {line}");
    }

    public void Dispose()
    {
        _journal.Add($"{_name}: closed");
        if (_failOnDispose)
        {
            throw new InvalidOperationException("close failed");
        }
    }
}

/// <summary>
/// "using var" disposes at the end of the enclosing scope, in reverse order, with no extra
/// nesting. The catch: if the body AND Dispose() both throw, the Dispose exception
/// replaces the original one, and the real cause ("write failed") is lost.
/// </summary>
public static class Exporter
{
    public static void ExportOrders(List<string> journal, string orderLine, bool failOnDispose)
    {
        using var orders = new ExportFile("orders.csv", journal, failOnDispose);
        using var lines = new ExportFile("lines.csv", journal, false);
        orders.Write(orderLine);
        lines.Write("1,B1,2");
    }
}
