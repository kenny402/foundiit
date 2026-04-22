CREATE DATABASE IF NOT EXISTS foundiit;
USE foundiit;

-- Users table
CREATE TABLE IF NOT EXISTS users (
   user_id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(100),
   email VARCHAR(150) UNIQUE,
   phone VARCHAR(20),
   password VARCHAR(255),
   role ENUM('user','admin','moderator') DEFAULT 'user',
   enabled BOOLEAN DEFAULT TRUE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
   category_id INT AUTO_INCREMENT PRIMARY KEY,
   category_name VARCHAR(100)
);

-- Items table
CREATE TABLE IF NOT EXISTS items (
   item_id INT AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(100) NOT NULL,
   description TEXT,
   category_id INT,
   color VARCHAR(50),
   brand VARCHAR(50),
   status ENUM('LOST','FOUND','CLAIMED') DEFAULT 'LOST',
   location VARCHAR(255),
   image_path VARCHAR(255),
   reported_by INT,
   views INT DEFAULT 0,
   date_created DATE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (category_id) REFERENCES categories(category_id),
   FOREIGN KEY (reported_by) REFERENCES users(user_id)
);

-- Locations table
CREATE TABLE IF NOT EXISTS locations (
   location_id INT AUTO_INCREMENT PRIMARY KEY,
   place_name VARCHAR(100),
   description TEXT
);

-- Reports table
CREATE TABLE IF NOT EXISTS reports (
   report_id INT AUTO_INCREMENT PRIMARY KEY,
   user_id INT,
   item_id INT,
   report_type ENUM('LOST','FOUND'),
   location_id INT,
   report_date DATE,
   FOREIGN KEY (user_id) REFERENCES users(user_id),
   FOREIGN KEY (item_id) REFERENCES items(item_id),
   FOREIGN KEY (location_id) REFERENCES locations(location_id)
);

-- Claims table
CREATE TABLE IF NOT EXISTS claims (
   claim_id INT AUTO_INCREMENT PRIMARY KEY,
   item_id INT,
   claimant_id INT,
   proof_description TEXT,
   proof_image_path VARCHAR(255),
   status ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
   review_notes TEXT,
   reviewed_by INT,
   reviewed_at TIMESTAMP NULL,
   claim_date DATE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (item_id) REFERENCES items(item_id),
   FOREIGN KEY (claimant_id) REFERENCES users(user_id),
   FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

-- Images table
CREATE TABLE IF NOT EXISTS images (
   image_id INT AUTO_INCREMENT PRIMARY KEY,
   item_id INT,
   image_url VARCHAR(255),
   FOREIGN KEY (item_id) REFERENCES items(item_id)
);

-- Matches table
CREATE TABLE IF NOT EXISTS matches (
   id INT AUTO_INCREMENT PRIMARY KEY,
   lost_item_id INT,
   found_item_id INT,
   match_score INT DEFAULT 0,
   confirmed BOOLEAN DEFAULT FALSE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (lost_item_id) REFERENCES items(item_id),
   FOREIGN KEY (found_item_id) REFERENCES items(item_id)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
   notification_id INT AUTO_INCREMENT PRIMARY KEY,
   user_id INT,
   title VARCHAR(100),
   message TEXT,
   link VARCHAR(255),
   is_read BOOLEAN DEFAULT FALSE,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (user_id) REFERENCES users(user_id)
);
