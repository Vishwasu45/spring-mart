-- Add sample images for products

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/headphones.jpg', true, 1 
FROM products WHERE sku = 'SKU-HEADPHONES-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-WATCH-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-TSHIRT-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-SHOES-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-GARDEN-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/camera.jpg', true, 1 
FROM products WHERE sku = 'SKU-CAMERA-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-YOGA-001';
