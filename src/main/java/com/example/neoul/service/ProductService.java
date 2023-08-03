package com.example.neoul.service;

import com.example.neoul.dto.brand.BrandRes;
import com.example.neoul.dto.product.ProductRes;
import com.example.neoul.entity.brand.Brand;
import com.example.neoul.entity.brand.Product;
import com.example.neoul.entity.brand.ProductImage;
import com.example.neoul.entity.user.User;
import com.example.neoul.entity.user.UserLikedBrand;
import com.example.neoul.entity.user.UserLikedProduct;
import com.example.neoul.global.exception.BadRequestException;
import com.example.neoul.global.exception.NotFoundException;
import com.example.neoul.repository.ProductImageRepository;
import com.example.neoul.repository.ProductRepository;
import com.example.neoul.repository.UserLikedProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final UserLikedProductRepository userLikedProductRepository;

    private final UserService userService;



    // 상품 전체 리스트
    public List<ProductRes.ProductDetailRes> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductRes.ProductDetailRes> result = new ArrayList<>();

        for(Product product : products){
            ProductRes.ProductDetailRes e = getProduct(product.getId());
            result.add(e);
        }

        return result;
    }

    public ProductRes.ProductDetailRes getProduct(Long productId) {
        Product product = getProductByProductId(productId);
        List<ProductImage> productImages = productImageRepository.findAllByProduct(product);
        List<String> productImgList = new ArrayList<>();

        for(ProductImage productImage : productImages){
            productImgList.add(productImage.getUrl());
        }

        return ProductRes.ProductDetailRes.builder()
                .productId(product.getId())
                .brandId(product.getBrand().getId())
                .categoryPId(product.getCategoryP().getId())
                .categoryName(product.getCategoryP().getName()) //얘는 살려야함
                .productName(product.getName())
                .price(product.getPrice())
                .productImgList(productImgList) //얘는 살려야함
                .deliveryInfo(product.getDeliveryInfo())
                .productUrl(product.getProductUrl())
//                .pLikeCNT(product.getLikeCount())
//                .pHearted(product.isHearted())
                .createdAt(product.getCreatedAt().toLocalDate())
//                .clickedAt(product.getClickedAt())
                .build();
    }


    public Product getProductByProductId(Long productId){
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isEmpty()) {
            throw new NotFoundException("존재하지 않는 상품입니다");
        }
        return optionalProduct.get();
    }




    public void likeBrand(Long productId) {
        User user = userService.findNowLoginUser();
        Product product = getProductByProductId(productId);
        UserLikedProduct userLikedProduct = UserLikedProduct.builder()
                .user(user)
                .product(product)
                .build();

        userLikedProductRepository.save(userLikedProduct);
    }

    public void deleteLikedBrand(Long productId) {
        User user = userService.findNowLoginUser();
        Product product = getProductByProductId(productId);

        UserLikedProduct userLikedProduct = userLikedProductRepository.findByUserAndProduct(user, product).orElse(null);

        if(userLikedProduct == null)
            throw new BadRequestException("해당 상품을 찜하지 않았습니다");

        userLikedProductRepository.delete(userLikedProduct);
    }

    public ProductRes.getLikedProductRes getUserLikedBrand() {
        User user = userService.findNowLoginUser();
        List<UserLikedProduct> userLikedProductList = userLikedProductRepository.findAllByUser(user);
        List<ProductRes.LikedProductList> list = new ArrayList<>();

        for(UserLikedProduct userLikedProduct : userLikedProductList){
            ProductRes.LikedProductList e = ProductRes.LikedProductList.builder()
                    .productId(userLikedProduct.getProduct().getId())
                    .productName(userLikedProduct.getProduct().getName())
                    .build();
            list.add(e);
        }


        return ProductRes.getLikedProductRes.builder()
                .userId(user.getUserId())
                .productCnt(list.size())
                .likedProduct(list)
                .build();
    }





}
