-- ===================================
-- FoundIt+ Database Setup Script
-- ===================================
-- This script ensures all necessary tables exist with proper constraints

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN', 'MODERATOR') NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    INDEX idx_email (email)
);

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    UNIQUE KEY uk_category_name (category_name)
);

-- Create items table with all required fields
CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    brand VARCHAR(255),
    color VARCHAR(255),
    image_path VARCHAR(255),
    category_id BIGINT,
    reported_by BIGINT,
    views INT NOT NULL DEFAULT 0,
    date_created DATE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    status ENUM('LOST', 'FOUND', 'CLAIMED', 'CLOSED') DEFAULT 'LOST',
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (reported_by) REFERENCES users(user_id),
    INDEX idx_status (status),
    INDEX idx_category (category_id),
    INDEX idx_reported_by (reported_by),
    INDEX idx_created_at (created_at)
);

-- Create claims table
CREATE TABLE IF NOT EXISTS claims (
    claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    claimant_id BIGINT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'WITHDRAWN') DEFAULT 'PENDING',
    proof_description TEXT,
    proof_image_path VARCHAR(255),
    review_notes VARCHAR(255),
    reviewed_by BIGINT,
    reviewed_at DATETIME(6),
    claim_date DATE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    FOREIGN KEY (item_id) REFERENCES items(item_id),
    FOREIGN KEY (claimant_id) REFERENCES users(user_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    UNIQUE KEY uk_item_claimant (item_id, claimant_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Create matches table
CREATE TABLE IF NOT EXISTS matches (
    match_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id BIGINT NOT NULL,
    found_item_id BIGINT NOT NULL,
    match_score INT DEFAULT 0,
    confirmed BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6),
    FOREIGN KEY (lost_item_id) REFERENCES items(item_id),
    FOREIGN KEY (found_item_id) REFERENCES items(item_id),
    INDEX idx_created_at (created_at)
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    message TEXT NOT NULL,
    link VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- Insert default categories if they don't exist
INSERT INTO categories (category_name, icon) VALUES
    ('Electronics', 'bi-phone'),
    ('Books', 'bi-journal'),
    ('Clothing', 'bi-handbag'),
    ('Accessories', 'bi-gem'),
    ('Documents', 'bi-file-earmark'),
    ('Bags & Wallets', 'bi-briefcase'),
    ('Keys', 'bi-key'),
    ('Sports Equipment', 'bi-dribbble'),
    ('Other', 'bi-inbox')
ON DUPLICATE KEY UPDATE category_name = category_name;

-- Enable logging (optional)
SET GLOBAL log_bin_trust_function_creators = 1;

COMMIT;

