// VERDICT | T07 Transactions | BETTER: SPRING
// WHY: @Transactional makes a whole service method atomic across repositories and nested calls; EF's SaveChanges is one unit of work, several saves need BeginTransaction/Commit.

package shop.t07_transactions;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.domain.InsufficientStockException;
import shop.t07_transactions.OrderService.OrderConfirmation;
import shop.t07_transactions.OrderService.UnknownProductException;

import java.net.URI;

@RestController
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping("/api/orders")
    public ResponseEntity<OrderConfirmation> place(@Valid @RequestBody OrderRequest request) {
        var confirmation = orders.placeOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + confirmation.orderId())).body(confirmation);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail outOfStock(InsufficientStockException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UnknownProductException.class)
    public ProblemDetail unknownProduct(UnknownProductException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
