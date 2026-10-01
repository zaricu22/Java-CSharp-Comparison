// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

package shop.t22_resources;

import java.util.List;

/** A fake file that records what happens to it in a shared journal. */
public final class ExportFile implements AutoCloseable {

    private final String name;
    private final List<String> journal;
    private final boolean failOnClose;

    public ExportFile(String name, List<String> journal, boolean failOnClose) {
        this.name = name;
        this.journal = journal;
        this.failOnClose = failOnClose;
        journal.add(name + ": opened");
    }

    public void write(String line) {
        if (line.isEmpty()) {
            throw new IllegalStateException("write failed");
        }
        journal.add(name + ": " + line);
    }

    @Override
    public void close() {
        journal.add(name + ": closed");
        if (failOnClose) {
            throw new IllegalStateException("close failed");
        }
    }
}
