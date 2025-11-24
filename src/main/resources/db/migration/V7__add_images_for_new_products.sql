-- Add product images for the new products added in V6
-- Using available images across all categories

-- Electronics Products Images (15 products)
INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-LAPTOP-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/camera.jpg', true, 1 
FROM products WHERE sku = 'SKU-MONITOR-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/headphones.jpg', true, 1 
FROM products WHERE sku = 'SKU-KEYBOARD-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-MOUSE-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/camera.jpg', true, 1 
FROM products WHERE sku = 'SKU-USBHUB-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-SSD-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/headphones.jpg', true, 1 
FROM products WHERE sku = 'SKU-WEBCAM-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/camera.jpg', true, 1 
FROM products WHERE sku = 'SKU-SPEAKER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-TABLET-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/headphones.jpg', true, 1 
FROM products WHERE sku = 'SKU-POWERBANK-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/camera.jpg', true, 1 
FROM products WHERE sku = 'SKU-STAND-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-LAMP-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/headphones.jpg', true, 1 
FROM products WHERE sku = 'SKU-CHARGER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/camera.jpg', true, 1 
FROM products WHERE sku = 'SKU-EARBUDS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/smartwatch.jpg', true, 1 
FROM products WHERE sku = 'SKU-TABLET-DRAW-001';

-- Clothing Products Images (15 products)
INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-JEANS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-HOODIE-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-SHIRT-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-JACKET-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-DRESS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-POLO-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-CHINO-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-CARDIGAN-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-SHORTS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-TANK-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-BLAZER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-SOCKS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-SCARF-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/shoes.jpg', true, 1 
FROM products WHERE sku = 'SKU-BELT-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/tshirt.jpg', true, 1 
FROM products WHERE sku = 'SKU-HAT-001';

-- Sports Products Images (10 products)
INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-YOGAMAT-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-DUMBBELL-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-BANDS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-SOCCER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-BASKETBALL-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-TENNIS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-JUMPROPE-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-FITNESS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-GYMBAG-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/yoga.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOTTLE-001';

-- Books Products Images (10 products)
INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-CLEAN-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-PATTERNS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-PRAGMATIC-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-JAVA-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-JS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-HEADFIRST-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-MICRO-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-DDD-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-REFACTOR-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/book.jpg', true, 1 
FROM products WHERE sku = 'SKU-BOOK-BUILD-001';

-- Home & Garden Products Images (10 products)
INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-POT-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-WATER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-HOSE-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-SHEARS-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-SOLAR-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-GLOVES-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-MOWER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-COMPOST-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-FEEDER-001';

INSERT INTO product_images (product_id, image_url, is_primary, display_order)
SELECT id, '/images/products/garden.jpg', true, 1 
FROM products WHERE sku = 'SKU-BED-001';
