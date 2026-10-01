// VERDICT | T07 Transactions | BETTER: SPRING
// WHY: @Transactional makes a whole service method atomic across repositories and nested calls; EF's SaveChanges is one unit of work, several saves need BeginTransaction/Commit.

package shop.t07_transactions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderRequest(@NotBlank String customer, @NotEmpty List<@Valid Line> lines) {

    public record Line(@NotBlank String sku, @Positive int quantity) {}
}
