// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

using System.Globalization;
using System.Linq.Expressions;

namespace Shop.T20_Expressions;

/// <summary>
/// Expression trees: the compiler turns a lambda into a data structure the program can inspect.
/// One ordinary, type-checked lambda is compiled to run in memory, or translated to SQL here.
/// This is how EF Core turns db.Products.Where(p => p.Price > 40) into SQL.
/// </summary>
public static class SqlTranslator
{
    public static string Where<T>(Expression<Func<T, bool>> predicate) =>
        $"SELECT * FROM {typeof(T).Name}s WHERE {Visit(predicate.Body)}";

    public static List<T> Filter<T>(IEnumerable<T> items, Expression<Func<T, bool>> predicate) =>
        items.Where(predicate.Compile()).ToList();

    private static string Visit(Expression e) => e switch
    {
        BinaryExpression b => $"({Visit(b.Left)} {Operator(b.NodeType)} {Visit(b.Right)})",
        MemberExpression { Expression: ParameterExpression } m => m.Member.Name,     // p.Price -> column
        MemberExpression { Expression: null or ConstantExpression } m => Literal(Evaluate(m)), // captured variable -> value
        ConstantExpression c => Literal(c.Value),
        MethodCallExpression { Method.Name: nameof(string.StartsWith) } call =>
            $"{Visit(call.Object!)} LIKE {Literal(Evaluate(call.Arguments[0]) + "%")}",
        _ => throw new NotSupportedException($"Cannot translate {e.NodeType}"),
    };

    private static string Operator(ExpressionType type) => type switch
    {
        ExpressionType.AndAlso => "AND",
        ExpressionType.OrElse => "OR",
        ExpressionType.Equal => "=",
        ExpressionType.NotEqual => "<>",
        ExpressionType.GreaterThan => ">",
        ExpressionType.GreaterThanOrEqual => ">=",
        ExpressionType.LessThan => "<",
        ExpressionType.LessThanOrEqual => "<=",
        _ => throw new NotSupportedException($"Cannot translate {type}"),
    };

    private static object? Evaluate(Expression e) => Expression.Lambda(e).Compile().DynamicInvoke();

    private static string Literal(object? value) => value switch
    {
        null => "NULL",
        string s => $"'{s.Replace("'", "''")}'",
        IFormattable f => f.ToString(null, CultureInfo.InvariantCulture),
        _ => value.ToString()!,
    };
}
