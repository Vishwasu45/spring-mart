package com.springmart.controller.view;

import com.springmart.service.CategoryService;
import com.springmart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        model.addAttribute("latestProducts", productService.getLatestProducts(8));
        model.addAttribute("topRatedProducts", productService.getTopRatedProducts(8));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "home";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {

        if (search != null && !search.isEmpty()) {
            model.addAttribute("products", productService.searchProducts(search, pageable));
            model.addAttribute("searchKeyword", search);
        } else if (categoryId != null) {
            model.addAttribute("products", productService.getProductsByCategory(categoryId, pageable));
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            model.addAttribute("products", productService.getAllActiveProducts(pageable));
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        return "products";
    }

    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {
        model.addAttribute("product", productService.getProductBySlug(slug));
        return "product-detail";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
