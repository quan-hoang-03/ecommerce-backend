package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Category;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.repository.CategoryRepository;
import com.quanhm.ecommerce.be.repository.ProductRepository;
import com.quanhm.ecommerce.be.request.CreateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProductServiceImplementation implements ProductService{

    private ProductRepository productRepository;
    private UserService userService;
    private CategoryRepository categoryRepository;

    public ProductServiceImplementation(ProductRepository productRepository,UserService userService,CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(CreateProductRequest req) {
        Category topLevel = categoryRepository.findByName(req.getTopLavelCategory());
        if(topLevel == null){
            Category topLavelCategory = new Category();
            topLavelCategory.setName(req.getTopLavelCategory());
            topLavelCategory.setLevel(1);

            topLevel = categoryRepository.save(topLavelCategory);
        }

        Category secondLevel = categoryRepository.findByNameAndParant(req.getSecondLavelCategory(),topLevel.getName());
        if(secondLevel == null){
            Category secondLavelCategory = new Category();
            secondLavelCategory.setName(req.getSecondLavelCategory());
            secondLavelCategory.setParentCategory(topLevel);
            secondLavelCategory.setLevel(2);

            secondLevel = categoryRepository.save(secondLavelCategory);
        }

        Category thirdLevel = categoryRepository.findByNameAndParant(req.getThirdLavelCategory(),secondLevel.getName());
        if(thirdLevel == null){
            Category thirdLavelCategory = new Category();
            thirdLavelCategory.setName(req.getThirdLavelCategory());
            thirdLavelCategory.setParentCategory(secondLevel);
            thirdLavelCategory.setLevel(3);

            thirdLevel = categoryRepository.save(thirdLavelCategory);
        }

        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setCategory(thirdLevel);
        product.setPrice(req.getPrice());
        product.setDiscountPrice(req.getDiscountPrice());
        product.setDiscountPersent(req.getDiscountPersent());
        product.setColor(req.getColor());
        product.setBrand(req.getBrand());
        product.setImageUrl(req.getImageUrl());
        product.setQuantity(req.getQuantity());
        product.setCreatedAt(LocalDateTime.now());
        product.setSizes(req.getSize());

        Product savedProduct = productRepository.save(product);

        return savedProduct;
    }

    @Override
    public String deleteProduct(Long productId) throws ProductExpection {
        Product product = findProductById(productId);
        product.getSizes().clear();
        productRepository.delete(product);
        return "Xóa sản phẩm thành công";
    }

    @Override
    public Product updateProduct(Long productId, Product req) throws ProductExpection {
        Product product = findProductById(productId);
        if(req.getQuantity() != 0){
            product.setQuantity(req.getQuantity());

        }

        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long id) throws ProductExpection {
        Optional<Product> opt = productRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        throw new ProductExpection("Không tìm thấy sản phẩm"+id);
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        return List.of();
    }

    @Override
    public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice, Integer maxPrice, Integer page, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {
        Pageable pageble = PageRequest.of(pageNumber,pageSize);
        List<Product> products = productRepository.filterProducts(category,minPrice+"",maxPrice+"",minDiscount+"",sort);
        if (!colors.isEmpty()) {
            products = products.stream()
                    .filter(p -> colors.stream().anyMatch(c -> c.equalsIgnoreCase(p.getColor())))
                    .collect(Collectors.toList());
        }
        if(stock!=null){
            if(stock.equals("in_stock")){
                products = products.stream().filter(p -> p.getQuantity() > 0).collect(Collectors.toList());
            }else if(stock.equals("out_stock")){
                products = products.stream().filter(p -> p.getQuantity() <1).collect(Collectors.toList());
            }
        }
        int startIndex = (int)pageble.getOffset();
        int endIndex = Math.min(startIndex + pageble.getPageSize(), products.size());
        return null;
    }
}
