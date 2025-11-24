-- Add 50+ diverse products across all categories
-- This will provide a sizeable dataset for pagination testing

-- Electronics Products (15 products)
INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, is_featured, discount_percentage)
SELECT
    'Laptop Pro 15"',
    'High-performance laptop with Intel i7 processor, 16GB RAM, and 512GB SSD. Perfect for professionals.',
    1299.99,
    25,
    c.id,
    u.id,
    'SKU-LAPTOP-001',
    'laptop-pro-15',
    true,
    true,
    10
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    '4K Ultra HD Monitor 27"',
    'Stunning 4K display with HDR support and ultra-slim bezels. Ideal for content creators.',
    449.99,
    40,
    c.id,
    u.id,
    'SKU-MONITOR-001',
    '4k-ultra-hd-monitor-27',
    true,
    15
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Mechanical Gaming Keyboard',
    'RGB mechanical keyboard with customizable keys and programmable macros.',
    129.99,
    60,
    c.id,
    u.id,
    'SKU-KEYBOARD-001',
    'mechanical-gaming-keyboard',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, is_featured)
SELECT
    'Wireless Gaming Mouse',
    'Precision wireless mouse with 16000 DPI sensor and customizable buttons.',
    79.99,
    80,
    c.id,
    u.id,
    'SKU-MOUSE-001',
    'wireless-gaming-mouse',
    true,
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'USB-C Hub 7-in-1',
    'Multi-port USB-C hub with HDMI, USB 3.0, SD card reader, and power delivery.',
    49.99,
    120,
    c.id,
    u.id,
    'SKU-USBHUB-001',
    'usb-c-hub-7in1',
    true,
    20
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Portable SSD 1TB',
    'Ultra-fast portable SSD with USB 3.2 Gen 2 for blazing transfer speeds.',
    159.99,
    50,
    c.id,
    u.id,
    'SKU-SSD-001',
    'portable-ssd-1tb',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Webcam 1080p HD',
    'High-definition webcam with auto-focus and noise-canceling microphone.',
    69.99,
    90,
    c.id,
    u.id,
    'SKU-WEBCAM-001',
    'webcam-1080p-hd',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Smart Speaker with Alexa',
    'Voice-controlled smart speaker with premium sound and smart home integration.',
    99.99,
    100,
    c.id,
    u.id,
    'SKU-SPEAKER-001',
    'smart-speaker-alexa',
    true,
    25
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Tablet 10.5" 128GB',
    'Powerful tablet with stunning display, perfect for entertainment and productivity.',
    399.99,
    45,
    c.id,
    u.id,
    'SKU-TABLET-001',
    'tablet-10-5-128gb',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, is_featured)
SELECT
    'Power Bank 20000mAh',
    'High-capacity power bank with fast charging and multiple USB ports.',
    39.99,
    150,
    c.id,
    u.id,
    'SKU-POWERBANK-001',
    'power-bank-20000mah',
    true,
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Smartphone Stand Adjustable',
    'Universal adjustable stand for smartphones and tablets with anti-slip base.',
    19.99,
    200,
    c.id,
    u.id,
    'SKU-STAND-001',
    'smartphone-stand-adjustable',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'LED Desk Lamp',
    'Modern LED desk lamp with touch control and adjustable brightness levels.',
    34.99,
    110,
    c.id,
    u.id,
    'SKU-LAMP-001',
    'led-desk-lamp',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Wireless Charger Pad',
    'Fast wireless charging pad compatible with all Qi-enabled devices.',
    24.99,
    180,
    c.id,
    u.id,
    'SKU-CHARGER-001',
    'wireless-charger-pad',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Bluetooth Earbuds Pro',
    'True wireless earbuds with active noise cancellation and premium sound quality.',
    199.99,
    70,
    c.id,
    u.id,
    'SKU-EARBUDS-001',
    'bluetooth-earbuds-pro',
    true
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Graphics Tablet Drawing Pad',
    'Professional graphics tablet with pressure-sensitive pen for digital artists.',
    179.99,
    35,
    c.id,
    u.id,
    'SKU-TABLET-DRAW-001',
    'graphics-tablet-drawing-pad',
    true,
    15
FROM categories c, users u
WHERE c.slug = 'electronics' AND u.email = 'seller@example.com';

-- Clothing Products (15 products)
INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Denim Jeans Classic Fit',
    'Comfortable classic fit jeans made from premium denim. Available in multiple sizes.',
    59.99,
    120,
    c.id,
    u.id,
    'SKU-JEANS-001',
    'denim-jeans-classic-fit',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Hoodie Pullover Fleece',
    'Cozy fleece hoodie perfect for casual wear. Soft and warm.',
    49.99,
    90,
    c.id,
    u.id,
    'SKU-HOODIE-001',
    'hoodie-pullover-fleece',
    true,
    20
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Formal Dress Shirt',
    'Elegant dress shirt for formal occasions. Wrinkle-resistant fabric.',
    44.99,
    80,
    c.id,
    u.id,
    'SKU-SHIRT-001',
    'formal-dress-shirt',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Leather Jacket Genuine',
    'Genuine leather jacket with classic design. Perfect for all seasons.',
    249.99,
    30,
    c.id,
    u.id,
    'SKU-JACKET-001',
    'leather-jacket-genuine',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Summer Dress Floral',
    'Beautiful floral summer dress made from breathable cotton.',
    39.99,
    100,
    c.id,
    u.id,
    'SKU-DRESS-001',
    'summer-dress-floral',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Polo Shirt Cotton',
    'Classic cotton polo shirt available in various colors.',
    34.99,
    110,
    c.id,
    u.id,
    'SKU-POLO-001',
    'polo-shirt-cotton',
    true,
    15
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Chino Pants Slim Fit',
    'Modern slim-fit chino pants perfect for smart casual wear.',
    54.99,
    85,
    c.id,
    u.id,
    'SKU-CHINO-001',
    'chino-pants-slim-fit',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Cardigan Sweater Wool',
    'Warm wool cardigan with button closure. Perfect for layering.',
    69.99,
    60,
    c.id,
    u.id,
    'SKU-CARDIGAN-001',
    'cardigan-sweater-wool',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Shorts Cargo Pocket',
    'Comfortable cargo shorts with multiple pockets. Great for outdoor activities.',
    39.99,
    95,
    c.id,
    u.id,
    'SKU-SHORTS-001',
    'shorts-cargo-pocket',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Tank Top Athletic',
    'Moisture-wicking athletic tank top for workouts and running.',
    19.99,
    140,
    c.id,
    u.id,
    'SKU-TANK-001',
    'tank-top-athletic',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Blazer Formal Business',
    'Professional business blazer with modern fit.',
    149.99,
    40,
    c.id,
    u.id,
    'SKU-BLAZER-001',
    'blazer-formal-business',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Socks Pack of 5',
    'Comfortable cotton socks pack. Perfect for daily wear.',
    14.99,
    200,
    c.id,
    u.id,
    'SKU-SOCKS-001',
    'socks-pack-of-5',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Scarf Wool Winter',
    'Warm wool scarf for cold weather. Soft and stylish.',
    29.99,
    75,
    c.id,
    u.id,
    'SKU-SCARF-001',
    'scarf-wool-winter',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Belt Leather Classic',
    'Genuine leather belt with metal buckle. Timeless design.',
    34.99,
    90,
    c.id,
    u.id,
    'SKU-BELT-001',
    'belt-leather-classic',
    true,
    10
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Hat Baseball Cap',
    'Adjustable baseball cap with embroidered logo. One size fits all.',
    24.99,
    130,
    c.id,
    u.id,
    'SKU-HAT-001',
    'hat-baseball-cap',
    true
FROM categories c, users u
WHERE c.slug = 'clothing' AND u.email = 'seller@example.com';

-- Sports Products (10 products)
INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Yoga Mat Premium',
    'Non-slip yoga mat with extra cushioning for comfort during workouts.',
    49.99,
    80,
    c.id,
    u.id,
    'SKU-YOGAMAT-001',
    'yoga-mat-premium',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Dumbbell Set 20kg',
    'Adjustable dumbbell set perfect for home gym workouts.',
    129.99,
    40,
    c.id,
    u.id,
    'SKU-DUMBBELL-001',
    'dumbbell-set-20kg',
    true,
    20
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Resistance Bands Set',
    'Set of 5 resistance bands with varying strength levels for versatile workouts.',
    29.99,
    100,
    c.id,
    u.id,
    'SKU-BANDS-001',
    'resistance-bands-set',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Soccer Ball Professional',
    'FIFA-approved soccer ball with premium construction.',
    39.99,
    60,
    c.id,
    u.id,
    'SKU-SOCCER-001',
    'soccer-ball-professional',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Basketball Indoor/Outdoor',
    'Durable basketball suitable for indoor and outdoor play.',
    34.99,
    70,
    c.id,
    u.id,
    'SKU-BASKETBALL-001',
    'basketball-indoor-outdoor',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Tennis Racket Carbon',
    'Lightweight carbon fiber tennis racket for advanced players.',
    149.99,
    35,
    c.id,
    u.id,
    'SKU-TENNIS-001',
    'tennis-racket-carbon',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Jump Rope Speed',
    'Adjustable speed jump rope perfect for cardio workouts.',
    14.99,
    120,
    c.id,
    u.id,
    'SKU-JUMPROPE-001',
    'jump-rope-speed',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Fitness Tracker Watch',
    'Smart fitness tracker with heart rate monitor and sleep tracking.',
    79.99,
    90,
    c.id,
    u.id,
    'SKU-FITNESS-001',
    'fitness-tracker-watch',
    true,
    25
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Gym Bag Duffel',
    'Spacious gym bag with multiple compartments and shoe section.',
    44.99,
    65,
    c.id,
    u.id,
    'SKU-GYMBAG-001',
    'gym-bag-duffel',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Water Bottle Insulated',
    'Stainless steel insulated water bottle keeps drinks cold for 24 hours.',
    24.99,
    150,
    c.id,
    u.id,
    'SKU-BOTTLE-001',
    'water-bottle-insulated',
    true
FROM categories c, users u
WHERE c.slug = 'sports' AND u.email = 'seller@example.com';

-- Books Products (10 products)
INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Clean Code by Robert Martin',
    'Essential reading for software developers. Learn to write better, cleaner code.',
    39.99,
    150,
    c.id,
    u.id,
    'SKU-BOOK-CLEAN-001',
    'clean-code-robert-martin',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Design Patterns: Gang of Four',
    'Classic software engineering book covering essential design patterns.',
    44.99,
    100,
    c.id,
    u.id,
    'SKU-BOOK-PATTERNS-001',
    'design-patterns-gang-of-four',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'The Pragmatic Programmer',
    'From journeyman to master. Timeless lessons for software craftsmanship.',
    42.99,
    120,
    c.id,
    u.id,
    'SKU-BOOK-PRAGMATIC-001',
    'pragmatic-programmer',
    true,
    15
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Effective Java 3rd Edition',
    'Best practices for Java programming by Joshua Bloch.',
    46.99,
    90,
    c.id,
    u.id,
    'SKU-BOOK-JAVA-001',
    'effective-java-3rd-edition',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'JavaScript: The Good Parts',
    'Master the essential parts of JavaScript with Douglas Crockford.',
    29.99,
    110,
    c.id,
    u.id,
    'SKU-BOOK-JS-001',
    'javascript-good-parts',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Head First Design Patterns',
    'Brain-friendly guide to software design patterns.',
    44.99,
    85,
    c.id,
    u.id,
    'SKU-BOOK-HEADFIRST-001',
    'head-first-design-patterns',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Microservices Patterns',
    'Learn to build microservices architecture with proven patterns.',
    49.99,
    75,
    c.id,
    u.id,
    'SKU-BOOK-MICRO-001',
    'microservices-patterns',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Domain-Driven Design',
    'Tackling complexity in the heart of software by Eric Evans.',
    54.99,
    60,
    c.id,
    u.id,
    'SKU-BOOK-DDD-001',
    'domain-driven-design',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Refactoring by Martin Fowler',
    'Improving the design of existing code. Essential for maintaining codebases.',
    47.99,
    95,
    c.id,
    u.id,
    'SKU-BOOK-REFACTOR-001',
    'refactoring-martin-fowler',
    true,
    20
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Building Microservices 2nd Ed',
    'Designing fine-grained systems by Sam Newman.',
    52.99,
    70,
    c.id,
    u.id,
    'SKU-BOOK-BUILD-001',
    'building-microservices-2nd-ed',
    true
FROM categories c, users u
WHERE c.slug = 'books' AND u.email = 'seller@example.com';

-- Home & Garden Products (10 products)
INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Indoor Plant Pot Set',
    'Set of 3 ceramic planters with drainage holes and saucers.',
    34.99,
    80,
    c.id,
    u.id,
    'SKU-POT-001',
    'indoor-plant-pot-set',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Watering Can 2 Gallon',
    'Durable plastic watering can with long spout for easy watering.',
    19.99,
    100,
    c.id,
    u.id,
    'SKU-WATER-001',
    'watering-can-2-gallon',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Garden Hose 50ft',
    'Flexible garden hose with spray nozzle and brass fittings.',
    44.99,
    60,
    c.id,
    u.id,
    'SKU-HOSE-001',
    'garden-hose-50ft',
    true,
    15
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Pruning Shears Professional',
    'Sharp stainless steel pruning shears with comfortable grip.',
    24.99,
    90,
    c.id,
    u.id,
    'SKU-SHEARS-001',
    'pruning-shears-professional',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Outdoor Solar Lights 8-Pack',
    'Solar-powered pathway lights for garden and walkway illumination.',
    59.99,
    70,
    c.id,
    u.id,
    'SKU-SOLAR-001',
    'outdoor-solar-lights-8pack',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Gardening Gloves Premium',
    'Breathable gardening gloves with reinforced fingertips.',
    14.99,
    120,
    c.id,
    u.id,
    'SKU-GLOVES-001',
    'gardening-gloves-premium',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Lawn Mower Electric',
    'Cordless electric lawn mower with adjustable cutting height.',
    299.99,
    25,
    c.id,
    u.id,
    'SKU-MOWER-001',
    'lawn-mower-electric',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active, discount_percentage)
SELECT
    'Compost Bin 80 Gallon',
    'Large capacity compost bin for organic waste recycling.',
    89.99,
    35,
    c.id,
    u.id,
    'SKU-COMPOST-001',
    'compost-bin-80-gallon',
    true,
    20
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Bird Feeder Hanging',
    'Decorative bird feeder with seed capacity for multiple birds.',
    29.99,
    75,
    c.id,
    u.id,
    'SKU-FEEDER-001',
    'bird-feeder-hanging',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

INSERT INTO products (name, description, price, stock_quantity, category_id, seller_id, sku, slug, is_active)
SELECT
    'Raised Garden Bed Kit',
    'Easy-assembly raised garden bed kit, perfect for vegetables and herbs.',
    149.99,
    30,
    c.id,
    u.id,
    'SKU-BED-001',
    'raised-garden-bed-kit',
    true
FROM categories c, users u
WHERE c.slug = 'home-garden' AND u.email = 'seller@example.com';

