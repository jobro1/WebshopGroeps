package com.luxuryproductsholding.api.utils;

import com.luxuryproductsholding.api.dao.*;
import com.luxuryproductsholding.api.models.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Seeder {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSpecificationsRepository productSpecificationsRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageUrlRepository imageUrlRepository;

    public Seeder(ProductRepository productRepository,
                  ProductCategoryRepository productCategoryRepository,
                  ProductSpecificationsRepository productSpecificationsRepository,
                  UserRepository userRepository, OrderRepository orderRepository,
                  OrderItemRepository orderItemRepository, ImageUrlRepository imageUrlRepository) {

        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productSpecificationsRepository = productSpecificationsRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.imageUrlRepository = imageUrlRepository;
    }

    @EventListener
    public void Seed(ContextRefreshedEvent event) {
        this.seedProduct();
    }

    private void seedProduct() {
        ProductCategory watches = new ProductCategory("Horloges");
        ProductCategory bracelets = new ProductCategory("Armbanden");
        ProductCategory bags = new ProductCategory("Tassen");
        ProductCategory glasses = new ProductCategory("Brillen");

        this.productCategoryRepository.saveAll(List.of(watches, bracelets, bags, glasses));

        Product p1 = new Product("Rolex Submariner", "RSUB2025", 7999.99, "Iconisch duikhorloge met zwarte wijzerplaat en keramische bezel.", "Rolex", "2 jaar", 5, watches);
        productRepository.save(p1);
        imageUrlRepository.save(new ImageUrl("https://i0.wp.com/wannabuyawatch.com/wp-content/uploads/2021/03/52078.jpg?fit=960%2C1280&ssl=1", p1));

        Product p2 = new Product("Omega Speedmaster", "OSPEED1969", 6299.00, "De legendarische Moonwatch, gedragen tijdens Apollo-missies.", "Omega", "2 jaar", 4, watches);
        productRepository.save(p2);
        imageUrlRepository.save(new ImageUrl("https://www.eugenevanbaal.nl/media/catalog/product/cache/4f2c711297192d1e648fdfaf4a68b673/3/2/32930445104001.webp", p2));

        Product p3 = new Product("Cartier Love Bracelet", "CLB2025", 7150.00, "Gouden armband met schroefsysteem, symbool van eeuwige liefde.", "Cartier", "2 jaar", 10, bracelets);
        productRepository.save(p3);
        imageUrlRepository.save(new ImageUrl("https://a.1stdibscdn.com/archivesE/upload/j_214/15_15/org_mtsj118232/MTSJ118232_l.jpeg?disable=upscale&auto=webp&quality=60&width=1400", p3));

        Product p4 = new Product("Tiffany T Wire Bracelet", "TTWB2025", 3450.00, "Minimalistisch design in 18k goud met iconisch T-ontwerp.", "Tiffany & Co.", "2 jaar", 8, bracelets);
        productRepository.save(p4);
        imageUrlRepository.save(new ImageUrl("https://www.net-a-porter.com/variants/images/17957409494218415/in/w2000_q60.jpg", p4));

        Product p5 = new Product("Louis Vuitton Neverfull MM", "LVNFMM2025", 1650.00, "Luxe handtas van gecoat canvas en leer, met LV-monogram.", "Louis Vuitton", "2 jaar", 6, bags);
        productRepository.save(p5);
        imageUrlRepository.save(new ImageUrl("https://en.louisvuitton.com/images/is/image/lv/1/PP_VP_L/louis-vuitton-neverfull-mm--N40599_PM1_Side%20view.png?wid=2400&hei=2400", p5));

        Product p6 = new Product("Chanel Classic Flap Bag", "CCFB2025", 7950.00, "Elegante tas met kettingband en quilted leer.", "Chanel", "2 jaar", 3, bags);
        productRepository.save(p6);
        imageUrlRepository.save(new ImageUrl("https://images.vestiairecollective.com/images/resized/w=1024,q=75,f=auto,/produit/chanel-timeless-classique-leer-zwart-handtas-48641947-1_3.jpg", p6));

        Product p7 = new Product("Ray-Ban Aviator Gold", "RBAG2025", 599.00, "De originele pilotenbril met gouden frame.", "Ray-Ban", "2 jaar", 15, glasses);
        productRepository.save(p7);
        imageUrlRepository.save(new ImageUrl("https://grandvision-media.imgix.net/m/6167739297197404/original_png-0RB3025__001_51__STD__shad__qt.png?w=1440&auto=format", p7));

        Product p8 = new Product("Gucci GG0406S", "GG0406S", 690.00, "Statement zonnebril met oversized lenzen en goudkleurig frame.", "Gucci", "2 jaar", 10, glasses);
        productRepository.save(p8);
        imageUrlRepository.save(new ImageUrl("https://grandvision-media.imgix.net/m/7a7a65e179d137dc/original_png-gucci_gg0121o_8056376076967_00025.png?w=1440&auto=format", p8));

        Product p9 = new Product("TAG Heuer Carrera Calibre 5", "THCC5", 2850.00, "Automatisch horloge met sportief design en Zwitserse precisie.", "TAG Heuer", "2 jaar", 5, watches);
        productRepository.save(p9);
        imageUrlRepository.save(new ImageUrl("https://chronexttime.imgix.net/V/8/V85849/V85849_1_det.png?w=570&ar=1:1&auto=format&fm=png&q=55&usm=50&usmrad=1.5&dpr=2&trim=color&fit=fill&auto=compress&bg=FFFFFF&bg-remove=true", p9));
    }
}
