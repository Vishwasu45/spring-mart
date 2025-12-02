-- V12: Add Guest Checkout Support

-- Create guest_sessions table to track guest shopping sessions
CREATE TABLE IF NOT EXISTS guest_sessions (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_guest_sessions_session_id ON guest_sessions(session_id);
CREATE INDEX idx_guest_sessions_email ON guest_sessions(email);
CREATE INDEX idx_guest_sessions_expires_at ON guest_sessions(expires_at);

-- Create guest_cart_items table for guest cart management
CREATE TABLE IF NOT EXISTS guest_cart_items (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guest_cart_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_guest_session_product UNIQUE (session_id, product_id)
);

CREATE INDEX idx_guest_cart_session_id ON guest_cart_items(session_id);
CREATE INDEX idx_guest_cart_product_id ON guest_cart_items(product_id);

-- Add guest_session_id to orders table to support guest orders
ALTER TABLE orders ADD COLUMN IF NOT EXISTS guest_session_id VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS guest_email VARCHAR(255);

-- Make user_id nullable to allow guest orders
ALTER TABLE orders ALTER COLUMN user_id DROP NOT NULL;

CREATE INDEX idx_orders_guest_session ON orders(guest_session_id);
CREATE INDEX idx_orders_guest_email ON orders(guest_email);

-- Add constraint to ensure either user_id or guest_session_id is present
ALTER TABLE orders ADD CONSTRAINT chk_orders_user_or_guest 
    CHECK (user_id IS NOT NULL OR guest_session_id IS NOT NULL);
