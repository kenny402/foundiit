-- FoundIt+ Database Permission Fix
-- Run this script in your MariaDB/MySQL console to resolve "Host not allowed" errors.

CREATE DATABASE IF NOT EXISTS foundiit;

-- Grant permissions for root from localhost (Socket/TCP)
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY '';

-- Grant permissions for root from 127.0.0.1 (TCP Loopback)
GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' IDENTIFIED BY '';

-- Optional: Grant from any host if you are in a safe local environment
-- GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '';

FLUSH PRIVILEGES;

-- Verification query
SELECT User, Host FROM mysql.user WHERE User = 'root';
