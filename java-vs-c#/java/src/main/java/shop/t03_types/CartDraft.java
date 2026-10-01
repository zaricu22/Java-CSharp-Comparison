// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

package shop.t03_types;

public class CartDraft {

    private String note = "new cart";

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
