-- Sample Products Data
-- This will be loaded after the initial schema

-- Insert sample users (for testing - in production, users are created via OAuth2)
INSERT INTO users (email, name, password, enabled) VALUES
    ('seller@example.com', 'Demo Seller', '$2a$10$demopasswordhash', true),
    ('admin@example.com', 'Admin User', '$2a$10$demopasswordhash', true);

-- Assign roles to sample users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'seller@example.com' AND r.name = 'ROLE_SELLER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ROLE_ADMIN';

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'Wireless Bluetooth Headphones',
    'Premium noise-cancelling headphones with 30-hour battery life. Perfect for music lovers and professionals.',
    149.99,
    50,
    c.id,
    u.id,
    'SKU-HEADPHONES-001',
    'wireless-bluetooth-headphones-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'Smart Watch Pro',
    'Feature-packed smartwatch with health tracking, GPS, and 7-day battery life.',
    299.99,
    30,
    c.id,
    u.id,
    'SKU-WATCH-001',
    'smart-watch-pro-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'Premium Cotton T-Shirt',
    'Comfortable 100% organic cotton t-shirt available in multiple colors. Perfect for everyday wear.',
    29.99,
    100,
    c.id,
    u.id,
    'SKU-TSHIRT-001',
    'premium-cotton-tshirt-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'Running Shoes Ultra',
    'Lightweight running shoes with advanced cushioning technology for maximum comfort.',
    89.99,
    75,
    c.id,
    u.id,
    'SKU-SHOES-001',
    'running-shoes-ultra-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'The Complete Guide to Spring Boot',
    'Comprehensive guide covering Spring Boot 3, Spring Security, and microservices architecture.',
    49.99,
    200,
    c.id,
    u.id,
    'SKU-BOOK-001',
    'complete-guide-spring-boot-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'Garden Tool Set',
    'Professional 10-piece garden tool set with ergonomic handles and storage bag.',
    79.99,
    40,
    c.id,
    u.id,
    'SKU-GARDEN-001',
    'garden-tool-set-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    '4K Ultra HD Camera',
    'Professional mirrorless camera with 4K video recording and advanced autofocus system.',
    1299.99,
    15,
    c.id,
    u.id,
    'SKU-CAMERA-001',
    '4k-ultra-hd-camera-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT 
    'Yoga Mat Premium',
    'Extra-thick yoga mat with non-slip surface and carrying strap. Perfect for all yoga styles.',
    39.99,
    60,
    c.id,
    u.id,
    'SKU-YOGA-001',
    'yoga-mat-premium-' || EXTRACT(EPOCH FROM NOW())::bigint,
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';
