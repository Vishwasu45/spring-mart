package com.springmart.repository;

import com.springmart.dto.ProductFilterRequest;
import com.springmart.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Product> findWithFilters(ProductFilterRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Main query for results
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        
        // Join with reviews for rating filter and sorting
        Join<Object, Object> reviews = product.join("reviews", JoinType.LEFT);
        
        // Build predicates
        List<Predicate> predicates = buildPredicates(cb, product, filterRequest);
        
        // Always filter for active products
        predicates.add(cb.isTrue(product.get("isActive")));
        
        // Apply predicates
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        
        // Group by product id for aggregation
        query.groupBy(product.get("id"));
        
        // Apply sorting
        applySorting(cb, query, product, reviews, filterRequest.getSortBy());
        
        // Create typed query
        TypedQuery<Product> typedQuery = entityManager.createQuery(query);
        
        // Get total count
        long totalElements = getTotalCount(filterRequest);
        
        // Apply pagination
        Pageable pageable = PageRequest.of(
            filterRequest.getPage() != null ? filterRequest.getPage() : 0,
            filterRequest.getSize() != null ? filterRequest.getSize() : 12
        );
        
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<Product> results = typedQuery.getResultList();
        
        return new PageImpl<>(results, pageable, totalElements);
    }
    
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> product, 
                                           ProductFilterRequest filterRequest) {
        List<Predicate> predicates = new ArrayList<>();
        
        // Category filter
        if (filterRequest.getCategoryIds() != null && !filterRequest.getCategoryIds().isEmpty()) {
            predicates.add(product.get("category").get("id").in(filterRequest.getCategoryIds()));
        }
        
        // Price range filter
        if (filterRequest.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), filterRequest.getMinPrice()));
        }
        if (filterRequest.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get("price"), filterRequest.getMaxPrice()));
        }
        
        // Stock filter
        if (filterRequest.getInStock() != null && filterRequest.getInStock()) {
            predicates.add(cb.greaterThan(product.get("stockQuantity"), 0));
        }
        
        // On sale filter
        if (filterRequest.getOnSale() != null && filterRequest.getOnSale()) {
            predicates.add(cb.greaterThan(product.get("discountPercentage"), 0));
        }
        
        // Featured filter
        if (filterRequest.getFeatured() != null && filterRequest.getFeatured()) {
            predicates.add(cb.isTrue(product.get("isFeatured")));
        }
        
        // Rating filter - will be applied with HAVING clause in count query
        
        return predicates;
    }
    
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Product> query, 
                             Root<Product> product, Join<Object, Object> reviews, 
                             String sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "newest";
        }
        
        switch (sortBy) {
            case "price_asc":
                query.orderBy(cb.asc(product.get("price")));
                break;
            case "price_desc":
                query.orderBy(cb.desc(product.get("price")));
                break;
            case "rating_desc":
                query.orderBy(cb.desc(cb.avg(reviews.get("rating"))));
                break;
            case "name_asc":
                query.orderBy(cb.asc(product.get("name")));
                break;
            case "newest":
            default:
                query.orderBy(cb.desc(product.get("createdAt")));
                break;
        }
    }
    
    private long getTotalCount(ProductFilterRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> product = countQuery.from(Product.class);
        
        List<Predicate> predicates = buildPredicates(cb, product, filterRequest);
        predicates.add(cb.isTrue(product.get("isActive")));
        
        if (!predicates.isEmpty()) {
            countQuery.where(predicates.toArray(new Predicate[0]));
        }
        
        countQuery.select(cb.countDistinct(product));
        
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
