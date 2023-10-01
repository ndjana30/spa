package com.gestion.spa.rest.controllers;

import com.gestion.spa.models.Product;
import com.gestion.spa.models.UserEntity;
import com.gestion.spa.repositories.ProductRepository;
import com.gestion.spa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    private ProductRepository productRepository;
    private UserRepository userRepository;
    @Autowired
    private ProductController(ProductRepository productRepository, UserRepository userRepository)
    {
        this.productRepository=productRepository;
        this.userRepository=userRepository;
    }
    @PostMapping("add")
    public Object addProduct(@RequestBody Product product)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<UserEntity> logged_in_user = userRepository.findByUsername(auth.getName());
        if(logged_in_user.isPresent())
        {
           long id= logged_in_user.get().getId();
           product.setEmployee_id(id);
           product.setDateTime(LocalDate.now());
           productRepository.save(product);

           return product;
        }

         return null;

    }
    @GetMapping("all")
    public List<Product> findAllProducts()
    {
        return productRepository.findAll();
    }
}
