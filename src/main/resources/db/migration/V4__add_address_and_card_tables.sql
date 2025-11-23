CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    zip_code VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE saved_cards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_number_last4 VARCHAR(4) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    expiry_month VARCHAR(255) NOT NULL,
    expiry_year VARCHAR(255) NOT NULL,
    holder_name VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_saved_cards_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_saved_cards_user_id ON saved_cards(user_id);
