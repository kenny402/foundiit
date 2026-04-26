-- ===================================
-- Fix Schema Script
-- ===================================

SET FOREIGN_KEY_CHECKS = 0;

-- Fix items table
ALTER TABLE items ADD COLUMN IF NOT EXISTS views INT NOT NULL DEFAULT 0 AFTER reported_by;
ALTER TABLE items ADD COLUMN IF NOT EXISTS date_created DATE AFTER views;

-- Fix notifications table
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS title VARCHAR(255) AFTER user_id;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS link VARCHAR(255) AFTER message;
-- Remove old columns if they exist and are not in entity
ALTER TABLE notifications DROP COLUMN IF EXISTS item_id;
ALTER TABLE notifications DROP COLUMN IF EXISTS claim_id;
ALTER TABLE notifications DROP COLUMN IF EXISTS notification_type;

-- Fix matches table
ALTER TABLE matches CHANGE COLUMN IF EXISTS item_a_id lost_item_id BIGINT NOT NULL;
ALTER TABLE matches CHANGE COLUMN IF EXISTS item_b_id found_item_id BIGINT NOT NULL;
ALTER TABLE matches CHANGE COLUMN IF EXISTS similarity_score match_score INT DEFAULT 0;
ALTER TABLE matches ADD COLUMN IF NOT EXISTS confirmed BOOLEAN DEFAULT FALSE AFTER match_score;

-- Fix claims table
ALTER TABLE claims CHANGE COLUMN IF EXISTS claim_reason proof_description TEXT;
ALTER TABLE claims ADD COLUMN IF NOT EXISTS review_notes VARCHAR(255) AFTER proof_image_path;
ALTER TABLE claims ADD COLUMN IF NOT EXISTS reviewed_by BIGINT AFTER review_notes;
ALTER TABLE claims ADD COLUMN IF NOT EXISTS reviewed_at DATETIME(6) AFTER reviewed_by;
ALTER TABLE claims ADD COLUMN IF NOT EXISTS claim_date DATE AFTER reviewed_at;

-- Ensure IDs are BIGINT to match Long in Java
ALTER TABLE categories MODIFY COLUMN category_id BIGINT AUTO_INCREMENT;
ALTER TABLE users MODIFY COLUMN user_id BIGINT AUTO_INCREMENT;
ALTER TABLE items MODIFY COLUMN item_id BIGINT AUTO_INCREMENT;
ALTER TABLE claims MODIFY COLUMN claim_id BIGINT AUTO_INCREMENT;
ALTER TABLE matches MODIFY COLUMN match_id BIGINT AUTO_INCREMENT;
ALTER TABLE notifications MODIFY COLUMN notification_id BIGINT AUTO_INCREMENT;

SET FOREIGN_KEY_CHECKS = 1;
