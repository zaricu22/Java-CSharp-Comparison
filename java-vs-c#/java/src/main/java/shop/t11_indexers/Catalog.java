// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

package shop.t11_indexers;

import shop.domain.Category;
import shop.domain.Product;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** No indexers: {@code catalog["B1"]} has to be spelled {@code catalog.get("B1")}. */
public final class Catalog {

    private final Map<String, Product> bySku = new LinkedHashMap<>();

    public Catalog(List<Product> products) {
        products.forEach(this::put);
    }

    public Product get(String sku) {
        Product product = bySku.get(sku);
        if (product == null) {
            throw new NoSuchElementException("Unknown sku " + sku);
        }
        return product;
    }

    public void put(Product product) {
        bySku.put(product.sku(), product);
    }

    public List<Product> get(Category category) {
        return bySku.values().stream().filter(p -> p.category() == category).toList();
    }
}
