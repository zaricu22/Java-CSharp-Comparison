// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

using System.Diagnostics;
using System.Dynamic;

namespace Shop.T13_CompileTime;

public static class BuildInfo
{
    private static readonly List<string> TraceLog = [];

    // Conditional compilation: the build configuration (dotnet build -c Release) picks the branch.
#if DEBUG
    public const string Mode = "Debug";
#else
    public const string Mode = "Release";
#endif

    /// <summary>
    /// In a Release build every call to Trace - including evaluation of its arguments -
    /// is removed from the calling code by the compiler.
    /// </summary>
    [Conditional("DEBUG")]
    public static void Trace(string message) => TraceLog.Add(message);

    public static IReadOnlyList<string> Traced => TraceLog.ToList();
}

/// <summary>
/// dynamic: member access is bound at runtime. Handy for COM/JSON/scripting interop,
/// rarely the right tool otherwise - errors move from compile time to runtime.
/// </summary>
public static class DynamicSettings
{
    public static dynamic Defaults()
    {
        dynamic settings = new ExpandoObject();
        settings.Theme = "dark";
        settings.PageSize = 20;
        return settings;
    }

    public static int NextPageSize(dynamic settings) => settings.PageSize * 2;
}
