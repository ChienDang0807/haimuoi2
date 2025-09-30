package vn.chiendt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShoppingCartController {

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @GetMapping("/shopping-cart")
    public String shoppingCart(Model model) {
        model.addAttribute("publishableKey", publishableKey);
        return "shopping-cart";
    }
}