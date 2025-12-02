-- Create promo_codes table
CREATE TABLE IF NOT EXISTS promo_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE or FIXED_AMOUNT
    discount_value DECIMAL(10, 2) NOT NULL,
    min_purchase_amount DECIMAL(10, 2),
    max_discount_amount DECIMAL(10, 2),
    usage_limit INTEGER,
    usage_count INTEGER DEFAULT 0,
    per_user_limit INTEGER DEFAULT 1,
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create promo_code_usage table
CREATE TABLE IF NOT EXISTS promo_code_usage (
    id BIGSERIAL PRIMARY KEY,
    promo_code_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL,
    used_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_promo_usage_code FOREIGN KEY (promo_code_id) REFERENCES promo_codes(id) ON DELETE CASCADE,
    CONSTRAINT fk_promo_usage_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_promo_usage_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Add promo code fields to orders table
ALTER TABLE orders ADD COLUMN promo_code_id BIGINT;
ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10, 2) DEFAULT 0;
ALTER TABLE orders ADD CONSTRAINT fk_order_promo_code FOREIGN KEY (promo_code_id) REFERENCES promo_codes(id) ON DELETE SET NULL;

-- Add indexes
CREATE INDEX idx_promo_code_code ON promo_codes(code);
CREATE INDEX idx_promo_code_active ON promo_codes(is_active);
CREATE INDEX idx_promo_code_valid ON promo_codes(valid_from, valid_until);
CREATE INDEX idx_promo_usage_user ON promo_code_usage(user_id);
CREATE INDEX idx_promo_usage_code ON promo_code_usage(promo_code_id);
