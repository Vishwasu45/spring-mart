-- Add product enhancement columns
ALTER TABLE products ADD COLUMN is_featured BOOLEAN DEFAULT FALSE;
ALTER TABLE products ADD COLUMN discount_percentage INTEGER DEFAULT 0;

-- Create newsletter subscriptions table
CREATE TABLE newsletter_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_newsletter_email ON newsletter_subscriptions(email);
CREATE INDEX idx_newsletter_active ON newsletter_subscriptions(is_active);

-- Mark some products as featured for demo
UPDATE products SET is_featured = TRUE WHERE id IN (1, 2, 3, 4);

-- Add some discount percentages for flash deals
UPDATE products SET discount_percentage = 20 WHERE id = 1;
UPDATE products SET discount_percentage = 15 WHERE id = 2;
UPDATE products SET discount_percentage = 30 WHERE id = 5;
