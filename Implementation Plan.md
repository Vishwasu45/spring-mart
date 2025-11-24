# Home Page Enhancement Implementation Plan

## Goal

Transform the SpringMart home page into a production-grade e-commerce experience with modern features including hero carousel, enhanced category cards, featured products, flash deals, customer testimonials, and newsletter subscription.

## User Review Required

**IMPORTANT**

Scope Confirmation: This plan includes significant frontend and backend changes. Please review the proposed features and let me know if you'd like to:

- Prioritize certain sections over others
- Adjust the design approach
- Add/remove any specific features

## Proposed Changes

### Frontend Enhancements

**[MODIFY] `home.html`**

1. **Hero Carousel Section (Lines 11–28)**
   - Replace static hero with Bootstrap carousel
   - Add 3–4 rotating banners with different promotions
   - Include auto-play functionality with manual controls
   - Add gradient overlays for better text readability

2. **Enhanced Category Cards (Lines 30–39)**
   - Replace simple pills with visual category cards
   - Add category images/icons
   - Display product count per category
   - Add hover effects with smooth transitions
   - Use grid layout: 3 columns on desktop, responsive on mobile

3. **Flash Deals / Deals of the Day Section (NEW)**
   - Add time-limited offer section below hero
   - Show countdown timer for deals
   - Display 4–6 products with discount badges
   - Highlight percentage off and original price

4. **Featured Products Section (NEW)**
   - Add curated "Featured Products" section
   - Different from "Latest Products" – hand-picked items
   - Show featured badge on product cards
   - Position strategically after category section

5. **Customer Testimonials (NEW)**
   - Add testimonials section with customer reviews
   - Carousel format with 3 testimonials visible
   - Include customer name, rating, and review text
   - Use professional styling with quote marks

6. **Newsletter Subscription (NEW)**
   - Add newsletter signup form in footer area
   - Email input with subscribe button
   - Show success message on submission
   - Add simple validation

7. **Promotional Banner (NEW)**
   - Add sticky/floating promotional banner
   - Highlight current sales or free shipping
   - Make it dismissible with close button
   - Position at top or bottom

8. **Statistics Section (NEW)**
   - Add trust indicators (users served, products sold, etc.)
   - Optional: animated counters
   - Professional icon-based design

### Backend Enhancements

**[MODIFY] `HomeController.java`**

Changes:

- Add `featuredProducts` to model (limit 4)
- Add `flashDeals` to model (products with active deals)
- Add `testimonials` to model
- Add `categoryStats` to model (product count per category)

**[MODIFY] `ProductRepository.java`**

Add methods:

```java
@Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.isActive = true ORDER BY p.createdAt DESC")
Page<Product> findFeaturedProducts(Pageable pageable);

@Query("SELECT p FROM Product p WHERE p.discountPercentage > 0 AND p.isActive = true ORDER BY p.discountPercentage DESC")
Page<Product> findProductsOnSale(Pageable pageable);
```

**[NEW] `Newsletter.java`**

Create entity for newsletter subscriptions:

- `id`
- `email`
- `subscribedAt`
- `isActive`

**[NEW] `NewsletterRepository.java`**

- Repository for newsletter management

**[NEW] `NewsletterController.java`**

REST API endpoint:

- `POST /api/newsletter/subscribe` – Subscribe to newsletter

**[MODIFY] `Product.java`**

Add fields:

- `isFeatured` (`Boolean`) – Mark products as featured
- `discountPercentage` (`Integer`) – Store discount percentage for flash deals

**[NEW] `V5__add_product_enhancements.sql`**

Database migration:

```sql
ALTER TABLE products ADD COLUMN is_featured BOOLEAN DEFAULT FALSE;
ALTER TABLE products ADD COLUMN discount_percentage INTEGER DEFAULT 0;

CREATE TABLE newsletter_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### CSS Enhancements

**[MODIFY] `style.css`**

Add styles for:

- Category card hover effects
- Product discount badge styling
- Testimonial card styling
- Newsletter form styling
- Carousel controls customization
- Animation for counters (optional)

## Verification Plan

### Manual Verification

#### Test 1: Visual Review

1. Start the application:
   
   ```bash
   ./start.sh
   ```

2. Navigate to `http://localhost:8080`.
3. Verify each new section appears correctly:
   - Hero carousel rotates automatically
   - Category cards display with images
   - Flash deals section shows discounted products
   - Featured products section displays
   - Testimonials carousel works
   - Newsletter form is visible
4. Test responsive design by resizing browser to mobile width.

#### Test 2: Newsletter Subscription

1. Scroll to newsletter section.
2. Enter email, e.g. `test@example.com`.
3. Click **Subscribe** button.
4. Verify success message appears.
5. Check database:
   
   ```sql
   SELECT * FROM newsletter_subscriptions;
   ```

6. Confirm email is stored.

#### Test 3: Interactive Elements

- Click carousel navigation arrows (left/right).
- Wait for auto-rotation (should happen every 5 seconds).
- Click category cards – should navigate to filtered products.
- Click on a flash deal product – should show product details.
- Hover over product cards – should show visual feedback.

### Browser Testing

- Test in Chrome, Firefox, Safari.
- Test on mobile devices or responsive mode.
- Verify all images load correctly.
- Check for console errors.

### Database Verification

After running migration:

- Verify new columns exist:

  ```sql
  \d products;
  ```

- Check newsletter table created:

  ```sql
  \d newsletter_subscriptions;
  ```

- Verify featured products:

  ```sql
  SELECT name, is_featured FROM products WHERE is_featured = true;
  ```

### Performance Testing

- Check page load time using browser DevTools.
- Verify images are optimized.
- Ensure no JavaScript errors appear in console.
